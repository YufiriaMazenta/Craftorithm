package pers.yufiria.craftorithm.database.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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

    private Dao<DiscoveredRecipe, Long> dao;
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
            List<DiscoveredRecipe> recipes = dao.queryForEq("player_uuid", playerUuid);
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

            Set<String> toAdd = new HashSet<>(recipeKeys);
            toAdd.removeAll(currentKeys);

            Set<String> toRemove = new HashSet<>(currentKeys);
            toRemove.removeAll(recipeKeys);

            if (!toRemove.isEmpty()) {
                DeleteBuilder<DiscoveredRecipe, Long> deleteBuilder = dao.deleteBuilder();
                deleteBuilder.where().eq("player_uuid", playerUuid).and().in("recipe_key", toRemove);
                deleteBuilder.delete();
            }

            long now = System.currentTimeMillis();
            for (String recipeKey : toAdd) {
                dao.create(new DiscoveredRecipe(playerUuid, recipeKey, now));
            }
        } catch (SQLException e) {
            CrypticLib.info("&cFailed to set discovered recipes for " + playerUuid + ": " + e.getMessage());
        }
    }

    public void saveAllOnlinePlayersData() {
        Set<String> serverRecipeKeys = RecipeManager.INSTANCE.serverRecipeKeys().stream()
            .map(Objects::toString)
            .collect(Collectors.toSet());
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            Set<String> localRecipes = player.getDiscoveredRecipes().stream()
                .map(NamespacedKey::toString)
                .collect(Collectors.toSet());
            localRecipes.retainAll(serverRecipeKeys);
            saveDiscoveredRecipes(uuid, localRecipes);
        }
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        switch (lifecyclePhase) {
            case ACTIVE, RELOAD -> {
                if (PluginConfigs.RECIPE_DISCOVERY_SYNC_ENABLE.value()) {
                    initTable();
                    startPeriodicSave();
                }
            }
            case DISABLE -> {
                if (PluginConfigs.RECIPE_DISCOVERY_SYNC_ENABLE.value()) {
                    saveAllOnlinePlayersData();
                }
            }
        }
    }

    private void startPeriodicSave() {
        int intervalTicks = PluginConfigs.RECIPE_DISCOVERY_SYNC_INTERVAL_TICKS.value();
        if (periodSaveTask != null) {
            periodSaveTask.cancel();
        }
        periodSaveTask = new CrypticLibRunnable() {
            @Override
            public void run() {
                if (!PluginConfigs.RECIPE_DISCOVERY_SYNC_ENABLE.value()) return;
                try {
                    saveAllOnlinePlayersData();
                    CrypticLib.info("Saved online players' discovered recipes");
                } catch (Exception e) {
                    CrypticLib.info("&cFailed to periodic save discovered recipes: " + e.getMessage());
                }
            }
        };
        periodSaveTask.asyncTimer(0, intervalTicks);
    }

}
