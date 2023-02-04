package top.oasismc.oasisrecipe.script.action;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import top.oasismc.oasisrecipe.OasisRecipe;
import top.oasismc.oasisrecipe.api.script.IRecipeStatement;
import top.oasismc.oasisrecipe.script.action.impl.*;
import top.oasismc.oasisrecipe.util.MsgUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ActionDispatcher {

    INSTANCE;
    private final Map<String, IRecipeStatement<?>> actionMap;
    private Economy economy;
    private PlayerPoints playerPoints;
    private boolean economyLoaded, pointsLoaded;

    ActionDispatcher() {
        actionMap = new ConcurrentHashMap<>();
        loadHookPlugins();
        regDefActions();
    }

    private void regDefActions() {
        regAction(RunCmdAction.INSTANCE);
        regAction(ConsoleCmdAction.INSTANCE);
        regAction(TakeLvlAction.INSTANCE);
        regAction(CloseAction.INSTANCE);
        if (economyLoaded) {
            regAction(TakeMoneyAction.INSTANCE);
        }
        if (pointsLoaded) {
            regAction(TakePointsAction.INSTANCE);
        }
    }

    public boolean regAction(IRecipeStatement<?> action, boolean force) {
        if (actionMap.containsKey(action.getStatement())) {
            if (!force)
                throw new IllegalArgumentException("Action " + action + " already registered");
        }
        actionMap.put(action.getStatement(), action);
        return true;
    }

    public boolean regAction(IRecipeStatement<?> action) {
        return regAction(action, false);
    }

    public Map<String, IRecipeStatement<?>> getActionMap() {
        return Collections.unmodifiableMap(actionMap);
    }

    public void dispatchActions(List<String> actions, Player player) {
        for (String action : actions) {
            dispatchAction(action, player);
        }
    }

    public Object dispatchAction(String actionLine, Player player) {
        int index = actionLine.indexOf(" ");
        String action = actionLine.substring(0, index);
        actionLine = actionLine.substring(index + 1);
        if (!actionMap.containsKey(action)) {
            throw new IllegalArgumentException("Action " + action + " does not exist");
        }
        return actionMap.get(action).exec(actionLine, player);
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


    public Economy getEconomy() {
        return economy;
    }

    public PlayerPoints getPlayerPoints() {
        return playerPoints;
    }

    private void loadHookPlugins() {
        economyLoaded = loadVault();
        String messageKey = "messages.load.vaultSuccess";
        if (!economyLoaded)
            messageKey = "messages.load.vaultFailed";
        MsgUtil.info(OasisRecipe.getInstance().getConfig().getString(messageKey, messageKey));
        pointsLoaded = loadPlayerPoints();
        messageKey = "messages.load.pointsSuccess";
        if (!pointsLoaded)
            messageKey = "messages.load.pointsFailed";
        MsgUtil.info(OasisRecipe.getInstance().getConfig().getString(messageKey, messageKey));
    }

}
