package top.oasismc.oasisrecipe.listener;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.item.ItemLoader;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static top.oasismc.oasisrecipe.OasisRecipe.color;
import static top.oasismc.oasisrecipe.OasisRecipe.info;
import static top.oasismc.oasisrecipe.recipe.RecipeManager.getManager;

public class RecipeCheckListener implements Listener {

    private Economy economy;
    private PlayerPoints playerPoints;
    private final Map<String, BiFunction<String, CraftItemEvent, Boolean>> checkFuncMap;
    private final Map<UUID, Set<Consumer<Player>>> operationListMap;

    private static final RecipeCheckListener listener;

    static {
        listener = new RecipeCheckListener();
    }

    private RecipeCheckListener() {
        checkFuncMap = new ConcurrentHashMap<>();
        operationListMap = new ConcurrentHashMap<>();
        regDefCheckFunc();
    }

    public static RecipeCheckListener getListener() {
        return listener;
    }


    private void regDefCheckFunc() {
        boolean isVaultLoaded = loadVault();
        regCheckFunc("spendLvl", (recipeName, event) -> {
            String value = getManager().getRecipeFile().getConfig().getString(recipeName + ".spendLvl", "");
            if (value.equals(""))
                return true;
            int needLvl;
            try {
                needLvl = Integer.parseInt(value);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            Player player = (Player) event.getWhoClicked();
            if (player.getLevel() < needLvl)
                return false;
            operationListMap.get(player.getUniqueId()).add(player1 -> player1.setLevel(player1.getLevel() - needLvl));
            return true;
        });

        regCheckFunc("perm", (recipeName, event) -> {
            String value = getManager().getRecipeFile().getConfig().getString(recipeName + ".perm", "");
            if (value.equals(""))
                return true;
            return event.getWhoClicked().hasPermission(value);
        });

        String messageKey;
        if (isVaultLoaded) {
            regCheckFunc("spendMoney", (recipeName, event) -> {
                String value = getManager().getRecipeFile().getConfig().getString(recipeName + ".spendMoney", "");
                if (value.equals(""))
                    return true;
                double needMoney;
                try {
                    needMoney = Double.parseDouble(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                Player player = (Player) event.getWhoClicked();
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
            regCheckFunc("spendPoints", (recipeName, event) -> {
                String value = getManager().getRecipeFile().getConfig().getString(recipeName + ".spendPoints", "");
                if (value.equals(""))
                    return true;
                int needPoints;
                try {
                    needPoints = Integer.parseInt(value);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                Player player = (Player) event.getWhoClicked();
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

    public void regCheckFunc(String key, BiFunction<String, CraftItemEvent, Boolean> checkFunc) {
        checkFuncMap.put(key, checkFunc);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void craftCheck(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }//检查点击者是否是玩家
        String recipeName = getManager().getRecipeName(event.getRecipe());
        UUID uuid = event.getWhoClicked().getUniqueId();
        operationListMap.put(uuid, new HashSet<>());
        for (String key : checkFuncMap.keySet()) {
            if (!checkFuncMap.get(key).apply(recipeName, event)) {
                event.setResult(Event.Result.DENY);
                break;
            }
        }
        if (event.getResult().equals(Event.Result.DENY)) {
            operationListMap.remove(uuid);
            return;
        }
        for (Consumer<Player> consumer : operationListMap.get(uuid)) {
            consumer.accept((Player) event.getWhoClicked());
        }
        operationListMap.remove(uuid);
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
