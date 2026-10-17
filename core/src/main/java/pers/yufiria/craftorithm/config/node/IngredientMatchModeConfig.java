package pers.yufiria.craftorithm.config.node;

import crypticlib.config.node.BukkitConfigNode;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.IngredientMatchMode;

import java.util.List;
import java.util.Locale;

/**
 * 材料匹配模式的配置节点, 在配置加载时将字符串解析为{@link IngredientMatchMode}
 * 非法的值会走配置节点的类型不匹配警告并回退默认值
 */
public class IngredientMatchModeConfig extends BukkitConfigNode<IngredientMatchMode> {

    public IngredientMatchModeConfig(@NotNull String key, @NotNull IngredientMatchMode def, @NotNull List<String> defComments) {
        super(key, def, defComments);
    }

    @Override
    protected IngredientMatchMode readValue(@NotNull ConfigurationSection config) {
        String raw = config.getString(key);
        if ("all".equalsIgnoreCase(raw)) {
            return IngredientMatchMode.ALL;
        }
        if ("first".equalsIgnoreCase(raw)) {
            return IngredientMatchMode.FIRST;
        }
        return null;
    }

    @Override
    protected void writeValue(IngredientMatchMode value) {
        //写回小写的枚举名, 与配置注释中的可选值保持一致
        configContainer().configWrapper().set(key, value.name().toLowerCase(Locale.ROOT));
    }

}
