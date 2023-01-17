package top.oasismc.oasisrecipe.item;

import org.bukkit.inventory.ItemStack;
import top.oasismc.oasisrecipe.config.ConfigFile;
import top.oasismc.oasisrecipe.item.nbt.NbtHandler;
import top.oasismc.oasisrecipe.api.nbt.IPluginNbtTag;
import top.oasismc.oasisrecipe.item.nbt.CompoundNbtTag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ItemUtil {

    private static final Map<String, String> getTagsMethodNameMap;

    static {
        getTagsMethodNameMap = new HashMap<>();
        getTagsMethodNameMap.put("v1_19_R2", "u");
    }

    /*
    获取一个NMS ItemStack对象
     */
    public static Object getNmsItem(ItemStack item) {
        if (item == null) {
            return null;
        }
        Class<?> craftItemClass;
        try {
            craftItemClass = Class.forName("org.bukkit.craftbukkit." + NbtHandler.getNmsVersion() + ".inventory.CraftItemStack");
            Method asNMSCopyMethod = craftItemClass.getMethod("asNMSCopy", ItemStack.class);
            return asNMSCopyMethod.invoke(null, item);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return null;
    }

    /*
    获取物品的全部NMS版NBT标签
     */
    public static Object getNmsItemNbtTags(ItemStack item) {
        if (item == null)
            return null;
        Object nmsItem = getNmsItem(item);
        Class<?> nmsItemClass = nmsItem.getClass();
        Method getTagsMethod;
        try {
            String getTagsMethodName = getTagsMethodNameMap.get(NbtHandler.getNmsVersion());
            getTagsMethod = nmsItemClass.getMethod(getTagsMethodName);
            return getTagsMethod.invoke(nmsItem);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return null;
    }

    /*
    将物品保存至配置文件
     */
    public static void saveItem2Config(ItemStack item, ConfigFile configFile, String path) {
        Object nmsNbt = ItemUtil.getNmsItemNbtTags(item);
        IPluginNbtTag<?> nbtTag = NbtHandler.nmsNbt2PluginNbtObj(nmsNbt);
        if (nbtTag != null) {
            NbtHandler.setPluginNbt2Config(configFile.getConfig(), path, (CompoundNbtTag) nbtTag);
            configFile.saveConfig();
        }
    }

    public static ItemStack getItemFromConfig(String path) {
        //TODO
        return null;
    }

}
