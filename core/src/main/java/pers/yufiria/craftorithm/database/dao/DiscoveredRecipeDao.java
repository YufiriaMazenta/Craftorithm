package pers.yufiria.craftorithm.database.dao;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.database.connection.ConnectionSource;
import crypticlib.database.dao.Dao;
import crypticlib.database.dao.DaoManager;
import crypticlib.database.table.TableUtils;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.scheduler.CrypticLibRunnable;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.database.DataSourceManager;
import pers.yufiria.craftorithm.database.entity.DiscoveredRecipe;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ACTIVE, isAsync = true),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, isAsync = true),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
    }
)
public enum DiscoveredRecipeDao implements LifecycleTask {

    INSTANCE;

    private Dao<DiscoveredRecipe> dao;
    private CrypticLibRunnable periodSaveTask;

    public void initTable() {
        try {
            ConnectionSource connectionSource = DataSourceManager.INSTANCE.databaseConnection();
            dao = DaoManager.createDao(connectionSource, DiscoveredRecipe.class);
            TableUtils.createTableIfNotExists(connectionSource, DiscoveredRecipe.class);
        } catch (SQLException e) {
            CrypticLib.info("&cFailed to initialize discovered_recipes table: " + e.getMessage());
        }
    }

    public Set<String> getDiscoveredRecipes(UUID playerUuid) {
        try {
            List<DiscoveredRecipe> recipes = dao.queryBuilder().where(
                where -> where.equals("player_uuid", playerUuid)
            ).query();
            Set<String> result = new HashSet<>();
            for (DiscoveredRecipe recipe : recipes) {
                result.add(recipe.getRecipeKey());
            }
            return result;
        } catch (SQLException e) {
            CrypticLib.info("&cFailed to get discovered recipes for " + playerUuid + ": " + e.getMessage());
            return new HashSet<>();
        }
    }

    public void saveDiscoveredRecipes(UUID playerUuid, Set<String> recipeKeys) {
        try {
            Set<String> currentKeys = getDiscoveredRecipes(playerUuid);

            //删除数据库里已经不存在的配方
            Set<String> toRemove = new HashSet<>(currentKeys);
            toRemove.removeAll(recipeKeys);
            if (!toRemove.isEmpty()) {
                dao.deleteBuilder().where(
                    where -> where.equals("player_uuid", playerUuid).and().in("recipe_key", toRemove.toArray())
                ).delete();
            }
            int removedRecipesCount = toRemove.size();
            if (removedRecipesCount > 0) {
                CrypticLib.info("Removed " + removedRecipesCount + " discovered recipes for player: " + playerUuid);
            }

            //存入新增的已解锁配方
            Set<String> toAdd = new HashSet<>(recipeKeys);
            toAdd.removeAll(currentKeys);
            for (String recipeKey : toAdd) {
                dao.create(new DiscoveredRecipe(playerUuid, recipeKey));
            }
            int addedRecipesCount = toAdd.size();
            if (addedRecipesCount > 0) {
                CrypticLib.info("Saved " + toAdd.size() + " discovered recipes for player: " + playerUuid);
            }
        } catch (SQLException e) {
            CrypticLib.info("&cFailed to save discovered recipes for " + playerUuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveAllOnlinePlayersData() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty()) {
            return;
        }
        Set<String> serverRecipeKeys = RecipeManager.INSTANCE.serverRecipeKeys().stream()
            .map(Objects::toString)
            .collect(Collectors.toSet());
        for (Player player : onlinePlayers) {
            UUID uuid = player.getUniqueId();
            Set<String> localRecipes = player.getDiscoveredRecipes().stream()
                .map(NamespacedKey::toString)
                .collect(Collectors.toSet());
            localRecipes.retainAll(serverRecipeKeys);
            saveDiscoveredRecipes(uuid, localRecipes);
        }
        CrypticLib.info("Saved online players' discovered recipes");
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        switch (lifecyclePhase) {
            case ACTIVE, RELOAD -> {
                if (PluginConfigs.SAVE_DISCOVERED_RECIPES_ENABLE.value()) {
                    initTable();
                    startPeriodicSaveTask();
                }
            }
            case DISABLE -> {
                if (PluginConfigs.SAVE_DISCOVERED_RECIPES_ENABLE.value()) {
                    try {
                        saveAllOnlinePlayersData();
                    } catch (Exception e) {
                        CrypticLib.info("&cFailed to save discovered recipes on disable: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void startPeriodicSaveTask() {
        int intervalTicks = PluginConfigs.SAVE_DISCOVERED_RECIPES_INTERVAL_TICKS.value();
        if (periodSaveTask != null) {
            periodSaveTask.cancel();
        }
        periodSaveTask = new CrypticLibRunnable() {
            @Override
            public void run() {
                if (!PluginConfigs.SAVE_DISCOVERED_RECIPES_ENABLE.value()) return;
                try {
                    saveAllOnlinePlayersData();
                } catch (Exception e) {
                    CrypticLib.info("&cFailed to periodic save discovered recipes: " + e.getMessage());
                }
            }
        };
        periodSaveTask.asyncTimer(intervalTicks, intervalTicks);
    }

    /**
     * 从数据库读取所有在线玩家的已解锁配方并解锁
     */
    public void loadOnlinePlayersDiscoveredRecipes() {
        if (!PluginConfigs.SAVE_DISCOVERED_RECIPES_ENABLE.value()) return;
        Set<String> serverRecipeKeys = RecipeManager.INSTANCE.serverRecipeKeys().stream()
            .map(Objects::toString)
            .collect(Collectors.toSet());
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Set<String> dbRecipes = getDiscoveredRecipes(player.getUniqueId());
                dbRecipes.retainAll(serverRecipeKeys);
                if (!dbRecipes.isEmpty()) {
                    List<NamespacedKey> keys = dbRecipes.stream()
                        .map(NamespacedKey::fromString)
                        .filter(Objects::nonNull)
                        .toList();
                    CrypticLibBukkit.scheduler().runOnEntity(player, () -> player.discoverRecipes(keys), () -> {});
                }
            } catch (Exception e) {
                CrypticLib.info("&cFailed to load discovered recipes for " + player.getName() + ": " + e.getMessage());
            }
        }
        CrypticLib.info("Loaded discovered recipes for online players");
    }

}
