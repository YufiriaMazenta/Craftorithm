package pers.yufiria.craftorithm.ui.creator.tag;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tag选择菜单中的Tag图标
 */
public class TagIcon extends Icon {

    private @Nullable Tag<Material> tag;

    public TagIcon(@NotNull IconDisplay iconDisplay) {
        super(iconDisplay);
    }

    /**
     * 获取此图标对应的Material Tag
     */
    public @Nullable Tag<Material> tag() {
        return tag;
    }

    /**
     * 设置此图标对应的Material Tag
     */
    public TagIcon setTag(@Nullable Tag<Material> tag) {
        this.tag = tag;
        return this;
    }

}
