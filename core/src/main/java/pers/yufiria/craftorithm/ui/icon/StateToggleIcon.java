package pers.yufiria.craftorithm.ui.icon;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * 开关型图标
 * 图标文本中的 state 会替换为当前状态对应的文案, 开启时额外加上附魔光效,
 * 不用悬停也能看出状态; 点击切换状态并重绘整个页面。
 */
public class StateToggleIcon extends TranslatableIcon {

    private final Menu menu;
    private final BooleanSupplier stateGetter;
    private final Consumer<Boolean> stateSetter;
    private final String enabledText;
    private final String disabledText;

    /**
     * @param iconDisplay  图标外观
     * @param menu         图标所属页面, 切换状态后用它重绘
     * @param stateGetter  读取当前开关状态
     * @param stateSetter  写入开关状态
     * @param enabledText  开启时替换 state 的文案
     * @param disabledText 关闭时替换 state 的文案
     */
    public StateToggleIcon(
        @NotNull IconDisplay iconDisplay,
        @NotNull Menu menu,
        @NotNull BooleanSupplier stateGetter,
        @NotNull Consumer<Boolean> stateSetter,
        @NotNull String enabledText,
        @NotNull String disabledText
    ) {
        super(iconDisplay);
        this.menu = menu;
        this.stateGetter = stateGetter;
        this.stateSetter = stateSetter;
        this.enabledText = enabledText;
        this.disabledText = disabledText;
    }

    /**
     * 按菜单配置节构建开关图标, 省去调用方解析图标外观的步骤
     *
     * @param config 开关图标的配置
     */
    public static StateToggleIcon fromConfig(
        @NotNull ConfigurationSection config,
        @NotNull Menu menu,
        @NotNull BooleanSupplier stateGetter,
        @NotNull Consumer<Boolean> stateSetter,
        @NotNull String enabledText,
        @NotNull String disabledText
    ) {
        return new StateToggleIcon(
            TranslatableIconParser.INSTANCE.parseIconDisplay(config),
            menu,
            stateGetter,
            stateSetter,
            enabledText,
            disabledText
        );
    }

    @Override
    public ItemStack display() {
        boolean enabled = stateGetter.getAsBoolean();
        setTextReplaceMap(Map.of("<state>", enabled ? enabledText : disabledText));
        ItemStack display = super.display();
        if (!enabled) {
            return display;
        }
        //开启时加上附魔光效, 不用悬停也能看出状态
        ItemMeta itemMeta = display.getItemMeta();
        itemMeta.addEnchant(Enchantment.LURE, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        display.setItemMeta(itemMeta);
        return display;
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        stateSetter.accept(!stateGetter.getAsBoolean());
        menu.updateMenuIcons();
        return this;
    }

}
