package pers.yufiria.craftorithm.ui.creator;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.Material;
import org.bukkit.Tag;

public class IngredientIcon extends Icon {

    private Tag<Material> tag;
    private boolean useTag;

    public IngredientIcon() {
        super(new IconDisplay(Material.AIR));
    }

    public Tag<Material> tag() {
        return tag;
    }

    public void setTag(Tag<Material> useTag) {
        this.tag = useTag;
    }

    public boolean isUseTag() {
        return useTag;
    }

    public void setUseTag(boolean tag) {
        useTag = tag;
    }
}
