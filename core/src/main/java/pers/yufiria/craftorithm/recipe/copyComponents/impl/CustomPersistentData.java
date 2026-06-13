package pers.yufiria.craftorithm.recipe.copyComponents.impl;

import crypticlib.util.MapHelper;
import crypticlib.util.ReflectionHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

import java.util.Map;

/**
 * 用于自定义从PersistentDataContainer里复制内容的规则
 */
public class CustomPersistentData implements CopyComponentsRule {

    private final NamespacedKey persistentDataKey;
    private final PersistentDataType persistentDataType;
    public static final String RULE_NAME = "custom_persistent_data";

    public CustomPersistentData(String persistentDataKeyStr) {
        if (persistentDataKeyStr == null || persistentDataKeyStr.isEmpty()) {
            this.persistentDataKey = null;
            this.persistentDataType = null;
        } else {
            Map<String, String> map = MapHelper.keyValueText2Map(persistentDataKeyStr);
            this.persistentDataKey = NamespacedKey.fromString(map.get("key"));
            this.persistentDataType = parseDataType(map.get("type"));
        }
    }

    @Override
    public String ruleName() {
        return RULE_NAME;
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        PersistentDataContainer basePersistentDataContainer = baseMeta.getPersistentDataContainer();
        PersistentDataContainer resultPersistentDataContainer = resultMeta.getPersistentDataContainer();
        if (persistentDataKey != null) {
            if (basePersistentDataContainer.has(persistentDataKey, persistentDataType)) {
                Object object = basePersistentDataContainer.get(persistentDataKey, persistentDataType);
                resultPersistentDataContainer.set(persistentDataKey, persistentDataType, object);
            }
        } else {
            basePersistentDataContainer.copyTo(resultPersistentDataContainer, true);
        }
        return resultMeta;
    }

    private PersistentDataType<?, ?> parseDataType(final String typeStr) {
        String upperTypeStr = typeStr.toUpperCase();
        Class<PersistentDataType> dataTypeClass = PersistentDataType.class;
        Object dataType = ReflectionHelper.getFieldObj(ReflectionHelper.getField(dataTypeClass, upperTypeStr), null);
        return (PersistentDataType<?, ?>) dataType;
    }

}
