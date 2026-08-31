package pers.yufiria.craftorithm.ui.custom;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import crypticlib.util.IOHelper;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.ui.BackableMenu;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

@LifecycleTaskConfig(schedules = {
    @LifecycleSchedule(phase = LifecyclePhase.ENABLE),
    @LifecycleSchedule(phase = LifecyclePhase.RELOAD, isAsync = true)
})
public enum CustomMenuManager implements LifecycleTask {

    INSTANCE;

    private final Map<String, Function<Player, Menu>> menuCreators = new ConcurrentHashMap<>();
    private File customMenuFolder;
    private final AtomicBoolean isReloading = new AtomicBoolean(false);

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
        Optional<Function<Player, Menu>> menuOpenerOpt = getMenuCreatorOpt(menuName);
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

    public Optional<Function<Player, Menu>> getMenuCreatorOpt(String name) {
        return Optional.ofNullable(menuCreators.get(name));
    }

    public @Unmodifiable Map<String, Function<Player, Menu>> menuCreators() {
        return Collections.unmodifiableMap(menuCreators);
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        if (lifeCycle == LifecyclePhase.ENABLE) {
            customMenuFolder = new File(((Plugin) plugin).getDataFolder(), "menus/custom");
        }
        reloadMenus();
    }

    public void reloadMenus() {
        if (isReloading.get()) {
            return;
        }
        isReloading.set(true);
        try {
            int loadedMenuNum = 0;
            //重载所有自定义页面
            menuCreators.clear();
            List<File> files = IOHelper.allYamlFiles(customMenuFolder);
            if (files.isEmpty()) {
                Craftorithm.instance().saveResource("menus/custom/example_recipe_list.yml", false);
                files.add(new File(customMenuFolder, "example_recipe_list.yml"));
            }
            for (File menuFile : files) {
                boolean result = loadMenuCreatorFromConfigFile(menuFile);
                if (result) {
                    loadedMenuNum ++;
                }
            }
            CrypticLib.info("Loaded " + loadedMenuNum + " menu(s)");
        } finally {
            isReloading.set(false);
        }
    }

    private boolean loadMenuCreatorFromConfigFile(File menuFile) {
        String filename = IOHelper.getRelativeFileName(customMenuFolder, menuFile);
        String menuName = filename.substring(0, filename.lastIndexOf('.'));
        try {
            BukkitConfigWrapper configWrapper = new BukkitConfigWrapper(menuFile);
            CustomMenuInfo menuInfo = new CustomMenuInfo(configWrapper.config());
            menuCreators.put(menuName, player -> {
                CustomMenu customMenu = new CustomMenu(player, menuInfo);
                customMenu.openMenu();
                return customMenu;
            });
            CrypticLib.info("Loaded menu: " + menuName);
            return true;
        } catch (Throwable throwable) {
            CrypticLib.info("&cLoad menu " + menuName + " failed");
            throwable.printStackTrace();
            return false;
        }
    }

    public enum OpenMenuResult {
        SUCCESS, NOT_EXIST_MENU, EXCEPTION, PLAYER_OFFLINE
    }

}
