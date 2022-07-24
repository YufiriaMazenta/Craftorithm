package top.oasismc.oasisrecipe.listener;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.annotation.Untested;
import top.oasismc.oasisrecipe.recipe.RecipeManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static top.oasismc.oasisrecipe.OasisRecipe.color;
import static top.oasismc.oasisrecipe.OasisRecipe.info;

public enum RecipeCheckListener implements Listener {

    INSTANCE;

    private Economy economy;
    private PlayerPoints playerPoints;
    private final Map<String, BiFunction<String, Player, Boolean>> checkFuncMap;
    private final Map<UUID, Set<Consumer<Player>>> operationListMap;

    RecipeCheckListener() {
        checkFuncMap = new ConcurrentHashMap<>();
        operationListMap = new ConcurrentHashMap<>();
        regDefCheckFunc();
    }

    private void regDefCheckFunc() {
        boolean isVaultLoaded = loadVault();
        regCheckFunc("spendLvl", (recipeName, player) -> {
            String value = RecipeManager.INSTANCE.getRecipeFile().getConfig().getString(recipeName + ".spendLvl", "");
            if (value.isEmpty())
                return true;
            int needLvl;
            try {
                needLvl = Integer.parseInt(value);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            if (player.getLevel() < needLvl)
                return false;
            operationListMap.get(player.getUniqueId()).add(player1 -> player1.setLevel(player1.getLevel() - needLvl));
            return true;
        });

        regCheckFunc("perm", (recipeName, player) -> {
            String value = RecipeManager.INSTANCE.getRecipeFile().getConfig().getString(recipeName + ".perm", "");
            if (value.isEmpty())
                return true;
            return player.hasPermission(value);
        });

        String messageKey;
        if (isVaultLoaded) {
            regCheckFunc("spendMoney", (recipeName, player) -> {
                String value = RecipeManager.INSTANCE.getRecipeFile().getConfig().getString(recipeName + ".spendMoney", "");
                if (value.isEmpty())
                    return true;
                double needMoney;
                try {
                    needMoney = Double.parseDouble(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                if (economy.getBalance(player) < needMoney)
                    return false;
                operationListMap.get(player.getUniqueId()).add(player1 -> economy.withdrawPlayer(player1, needMoney));
                return true;
            });
            messageKey = "messages.load.vaultSuccess";
        } else {
            messageKey = "messages.load.vaultFailed";
        }
        info(color(OasisRecipe.getPlugin().getConfig().getString(messageKey, messageKey)));

        boolean isPlayerPointsLoaded = loadPlayerPoints();
        if (isPlayerPointsLoaded) {
            regCheckFunc("spendPoints", (recipeName, player) -> {
                String value = RecipeManager.INSTANCE.getRecipeFile().getConfig().getString(recipeName + ".spendPoints", "");
                if (value.isEmpty())
                    return true;
                int needPoints;
                try {
                    needPoints = Integer.parseInt(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                if (playerPoints.getAPI().look(player.getUniqueId()) < needPoints)
                    return false;
                operationListMap.get(player.getUniqueId()).add(player1 -> playerPoints.getAPI().take(player1.getUniqueId(), needPoints));
                return true;
            });
            messageKey = "messages.load.pointsSuccess";
        } else {
            messageKey = "messages.load.pointsFailed";
        }
        info(color(OasisRecipe.getPlugin().getConfig().getString(messageKey, messageKey)));
    }

    public void regCheckFunc(String key, BiFunction<String, Player, Boolean> checkFunc) {
        checkFuncMap.put(key, checkFunc);
    }

    @Untested
    @EventHandler(priority = EventPriority.LOWEST)
    public void craftCheck(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null)
            return;
        String recipeName = RecipeManager.INSTANCE.getRecipeName(event.getRecipe());
        boolean canCraft = true;
        List<UUID> viewPlayers = new ArrayList<>();
        for (HumanEntity human : event.getViewers()) {
            if (!(human instanceof Player))
                continue;
            Player player = ((Player) human);
            UUID uuid = player.getUniqueId();
            viewPlayers.add(uuid);
            operationListMap.put(uuid, new HashSet<>());
            for (String key : checkFuncMap.keySet()) {
                if (!checkFuncMap.get(key).apply(recipeName, player)) {
                    event.getInventory().setResult(null);
                    canCraft = false;
                    break;
                }
            }
            if (!canCraft)
                break;
        }

        if (!canCraft) {
            for (UUID uuid : viewPlayers) {
                operationListMap.remove(uuid);
            }
            return;
        }

        for (UUID uuid : viewPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                continue;
            for (Consumer<Player> consumer : operationListMap.get(uuid)) {
                consumer.accept(player);
            }
            operationListMap.remove(uuid);
        }
    }

    @EventHandler
    public void craft(PrepareItemCraftEvent event) {


    }

    private boolean loadVault() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> vaultRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (vaultRsp == null) {
            return false;
        }
        economy = vaultRsp.getProvider();
        return true;
    }

    private boolean loadPlayerPoints() {
        playerPoints = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");
        return playerPoints != null;
    }

}
