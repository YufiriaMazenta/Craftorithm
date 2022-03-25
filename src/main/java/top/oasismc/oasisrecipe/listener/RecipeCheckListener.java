package top.oasismc.oasisrecipe.listener;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.item.ItemUtil;

import java.util.Set;

import static top.oasismc.oasisrecipe.OasisRecipe.color;
import static top.oasismc.oasisrecipe.OasisRecipe.info;
import static top.oasismc.oasisrecipe.recipe.RecipeManager.getManager;


public class RecipeCheckListener implements Listener {

    private final boolean isVaultLoaded;
    private final boolean isPlayerPointsLoaded;
    private Economy economy;
    private PlayerPoints playerPoints;

    private static final RecipeCheckListener listener;

    static {
        listener = new RecipeCheckListener();
    }

    private RecipeCheckListener() {
        isVaultLoaded = loadVault();
        String messageKey;
        if (isVaultLoaded) {
            messageKey = "messages.load.vaultSuccess";
        } else {
            messageKey = "messages.load.vaultFailed";
        }
        info(color(OasisRecipe.getPlugin().getConfig().getString(messageKey, messageKey)));
        isPlayerPointsLoaded = loadPlayerPoints();
        if (isPlayerPointsLoaded) {
            messageKey = "messages.load.pointsSuccess";
        } else {
            messageKey = "messages.load.pointsFailed";
        }
        info(color(OasisRecipe.getPlugin().getConfig().getString(messageKey, messageKey)));
    }

    public static RecipeCheckListener getListener() {
        return listener;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void craftCheck(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }//检查点击者是否是玩家
        Player player = (Player) event.getWhoClicked();
        String recipeName = getManager().getRecipeName(event.getRecipe());
        if (conditionNotSatisfied("perm", recipeName, event, player))
            return;
        if (conditionNotSatisfied("exp", recipeName, event, player))
            return;
        if (conditionNotSatisfied("vault", recipeName, event, player))
            return;
        conditionNotSatisfied("points", recipeName, event, player);
    }

    private boolean conditionNotSatisfied(String checkType, String recipeName, CraftItemEvent event, Player player) {
        if (!OasisRecipe.getPlugin().getConfig().getBoolean("recipeCheck." + checkType, true)) {
            return false;
        }
        //检查是否开启检测

        String node = "";
        switch (checkType) {
            case "vault":
                if (!isVaultLoaded) return false;
                node = "spendMoney";
                break;
            case "points":
                if (!isPlayerPointsLoaded) return false;
                node = "spendPoints";
                break;
            case "exp":
                node = "spendLvl";
                break;
            case "perm":
                node = "perm";
                break;
        }//检查软依赖是否加载,生成对应的节点名
        if (node.equals("")) {
            return true;
        }//假设节点不存在,则返回

        int intValue = 0;
        String value = getManager().getRecipeFile().getConfig().getString(recipeName + "." + node, "");
        //获取节点值
        if (value.equals("")) {
            return false;
        }//若获取到对应节点值为空,则返回
        if (!checkType.equals("perm")) {
            double tmpValue = Double.parseDouble(value);
            intValue = (int) tmpValue;
            intValue *= event.getRecipe().getResult().getAmount();
        }//若类型不为perm,计算总共需要的值,否则直接将字符串作为值

        int playerValue = 0;
        switch (checkType) {
            case "exp":
                playerValue = player.getLevel();
                break;
            case "vault":
                playerValue = (int) economy.getBalance(player);
                break;
            case "points":
                playerValue = playerPoints.getAPI().look(player.getUniqueId());
                break;
        }//获取玩家拥有的值

        boolean canCraft;
        if (!checkType.equals("perm")) {
            canCraft = !(playerValue < intValue);
        } else {
            canCraft = player.hasPermission(value);
        }//判断玩家能否合成,当类型为perm时,检测权限;若为其他则检测值

        if (!canCraft) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            sendTitle((Player) event.getWhoClicked(), "messages.cannotCraft.title", "messages.cannotCraft.subtitle", recipeName);
            return true;
        }//若不能合成则关闭合成界面并提醒

        switch (checkType) {
            case "exp":
                player.setLevel(playerValue - intValue);
                break;
            case "vault":
                economy.withdrawPlayer(player, intValue);
                break;
            case "points":
                playerPoints.getAPI().take(player.getUniqueId(), intValue);
                break;
        }
        return false;
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

    private void sendTitle(Player player, String titleKey, String subTitleKey, String recipeName) {
        YamlConfiguration config = (YamlConfiguration) getManager().getRecipeFile().getConfig();

        String title = color(OasisRecipe.getPlugin().getConfig().getString(titleKey, titleKey));
        String result = getManager().getRecipeFile().getConfig().getString(recipeName + ".result", "");
        result = result.substring(result.indexOf(':') + 1);
        String itemName = (ItemUtil.getResultFile().getConfig().getString(result + ".name", ""));
        if (itemName.equals("")) {
            ItemStack item = ItemUtil.getItemFromConfig(result);
            if (item.getItemMeta() != null)
                itemName = item.getItemMeta().getLocalizedName();
        }
        title = title.replace("%item%", color(itemName));

        String subTitle = color(OasisRecipe.getPlugin().getConfig().getString(subTitleKey, subTitleKey));
        player.sendTitle(title, subTitle, 10, 70, 20);
    }

}
