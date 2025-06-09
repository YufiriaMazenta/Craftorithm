package pers.yufiria.craftorithm.ui.custom;

import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import crypticlib.util.IOHelper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

@AutoTask(rules = {
    @TaskRule(lifeCycle = LifeCycle.ENABLE),
    @TaskRule(lifeCycle = LifeCycle.RELOAD)
})
public enum CustomMenuManager implements BukkitLifeCycleTask {

    INSTANCE;

    private final Map<String, Function<Player, Menu>> menuOpeners = new ConcurrentHashMap<>();
    private File customMenuFolder;

    /**
     * 为某玩家打开一个菜单
     * @param player 要打开菜单的玩家
     * @param menuName 要打开的菜单ID
     * @param callback 如果打开失败了,要做什么
     */
    public void openMenu(Player player, String menuName, @NotNull Consumer<OpenMenuResult> callback) {
        if (player == null || !player.isOnline()) {
            callback.accept(OpenMenuResult.PLAYER_OFFLINE);
            return;
        }
        Optional<Function<Player, Menu>> menuOpenerOpt = getMenuOpenerOpt(menuName);
        if (menuOpenerOpt.isEmpty()) {
            callback.accept(OpenMenuResult.NOT_EXIST_MENU);
            return;
        }
        Function<Player, Menu> menuOpener = menuOpenerOpt.get();
        try {
            Optional<Menu> openingMenuOpt = MenuHelper.getOpeningMenu(player);
            Menu openingMenu = openingMenuOpt.orElse(null);
            Menu willOpenMenu = menuOpener.apply(player);
            if (willOpenMenu instanceof BackableMenu backableMenu) {
                backableMenu.setParentMenu(openingMenu);
            }
            callback.accept(OpenMenuResult.SUCCESS);
        } catch (Throwable throwable) {
            callback.accept(OpenMenuResult.EXCEPTION);
            throwable.printStackTrace();
        }
    }

    public Optional<Function<Player, Menu>> getMenuOpenerOpt(String name) {
        return Optional.ofNullable(menuOpeners.get(name));
    }

    public @Unmodifiable Map<String, Function<Player, Menu>> menuOpeners() {
        return Collections.unmodifiableMap(menuOpeners);
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        if (lifeCycle == LifeCycle.ENABLE) {
            customMenuFolder = new File(plugin.getDataFolder(), "menus/custom");
        }
        reloadMenus();
    }

    public void reloadMenus() {
        //重载所有自定义页面
        menuOpeners.clear();
        List<File> files = IOHelper.allYamlFiles(customMenuFolder);
        for (File menuFile : files) {
            String filename = IOHelper.getRelativeFileName(customMenuFolder, menuFile);
            String menuName = filename.substring(0, filename.lastIndexOf('.'));
            try {
                BukkitConfigWrapper configWrapper = new BukkitConfigWrapper(menuFile);
                CustomMenuInfo menuInfo = new CustomMenuInfo(configWrapper.config());
                menuOpeners.put(menuName, player -> new CustomMenu(player, menuInfo).openMenu());
                IOHelper.info("Loaded menu: " + menuName);
            } catch (Throwable throwable) {
                IOHelper.info("&cLoad menu " + menuName + " failed");
                throwable.printStackTrace();
            }
        }
    }

    public enum OpenMenuResult {
        SUCCESS, NOT_EXIST_MENU, EXCEPTION, PLAYER_OFFLINE
    }

}
