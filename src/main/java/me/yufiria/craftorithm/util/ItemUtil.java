package me.yufiria.craftorithm.util;

import me.yufiria.craftorithm.api.nbt.IPluginNbtTag;
import me.yufiria.craftorithm.config.YamlFileWrapper;
import me.yufiria.craftorithm.item.nbt.CompoundNbtTag;
import me.yufiria.craftorithm.item.nbt.NbtHandler;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ItemUtil {

    private static final Map<String, String> getNbtCompoundFromItemMethodNameMap;
    private static final Map<String, String> setNbtCompound2ItemMethodNameMap;
    private static final Map<String, String> nmsItemClassNameMap;
    private static final Class<?> nmsItemClass;
    private static Method getNbtCompoundFromItemMethod = null;
    private static Method setNbtCompound2ItemMethod = null;

    static {
        getNbtCompoundFromItemMethodNameMap = new HashMap<>();
        loadGetNbtCompoundFromItemMethodNameMap();

        setNbtCompound2ItemMethodNameMap = new HashMap<>();
        loadSetNbtCompound2ItemMethodNameMap();

        nmsItemClassNameMap = new HashMap<>();
        loadNmsItemClassNameMap();

        Class<?> tmpClass = null;
        try {
            String nmsItemClassName = nmsItemClassNameMap.getOrDefault(NbtHandler.getNmsVersion(), "net.minecraft.world.item.ItemStack");
            tmpClass = Class.forName(nmsItemClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        nmsItemClass = tmpClass;
    }

    /*
    获取一个NMS ItemStack对象
     */
    public static Object bukkit2NmsItem(ItemStack item) {
        if (item == null) {
            return null;
        }
        try {
            Class<?> craftItemClass = Class.forName("org.bukkit.craftbukkit." + NbtHandler.getNmsVersion() + ".inventory.CraftItemStack");
            Method asNMSCopyMethod = craftItemClass.getMethod("asNMSCopy", ItemStack.class);
            return asNMSCopyMethod.invoke(null, item);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return null;
    }

    /*
    将NMS物品转化为Bukkit物品
     */
    public static ItemStack nms2BukkitItem(Object nmsItemObj) {
        if (!nmsItemObj.getClass().getName().contains("ItemStack"))
            return null;
        try {
            Class<?> craftItemClass = Class.forName("org.bukkit.craftbukkit." + NbtHandler.getNmsVersion() + ".inventory.CraftItemStack");
            Method asBukkitCopyMethod = craftItemClass.getMethod("asBukkitCopy", nmsItemClass);
            return (ItemStack) asBukkitCopyMethod.invoke(null, nmsItemObj);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return null;
    }

    /*
    获取物品的全部NMS NBT标签，类型应为NBTCompound
     */
    public static Object getNmsItemNbtTags(ItemStack item) {
        if (item == null)
            return null;
        Object nmsItem = bukkit2NmsItem(item);
        Class<?> nmsItemClass = nmsItem.getClass();
        try {
            if (getNbtCompoundFromItemMethod == null) {
                String getTagsMethodName = getNbtCompoundFromItemMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "u");
                getNbtCompoundFromItemMethod = nmsItemClass.getMethod(getTagsMethodName);
            }
            Object nmsNbtCompound = getNbtCompoundFromItemMethod.invoke(nmsItem);
            return nmsNbtCompound == null? new CompoundNbtTag().toNmsNbt(): nmsNbtCompound;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return null;
    }

    /*
    将物品保存至配置文件
     */
    public static void saveItem2Config(ItemStack item, YamlFileWrapper configFile, String path) {
        if (item == null)
            throw new IllegalArgumentException("Item can not be null");
        String material = item.getType().getKey().toString();
        configFile.getConfig().set(path + ".material", material);
        int amount = item.getAmount();
        configFile.getConfig().set(path + ".amount", amount);
        Object nmsNbt = ItemUtil.getNmsItemNbtTags(item);
        IPluginNbtTag<?> nbtTag = NbtHandler.nmsNbt2PluginNbtObj(nmsNbt);
        if (nbtTag != null)
            NbtHandler.setPluginNbt2Config(configFile.getConfig(), path + ".nbt", (CompoundNbtTag) nbtTag);
        configFile.saveConfig();
    }

    /*
    从指定的配置文件里以名字读取物品
     */
    public static ItemStack getItemFromConfig(YamlConfiguration config, String key) {
        String materialStr = config.getString(key + ".material", "AIR");
        Material material = Material.matchMaterial(materialStr);
        if (material == null) {
            //提示物品类型不存在
            throw new IllegalArgumentException("Material is null");
        }
        int amount = config.getInt(key + ".amount", 1);
        ItemStack item = new ItemStack(material, amount);
        ConfigurationSection cfgSection = config.getConfigurationSection(key + ".nbt");
        return loadNbt2Item(item, cfgSection);
    }

    /*
    从配置文件中读取NBT，存储到物品中，然后返回物品
     */
    public static ItemStack loadNbt2Item(ItemStack item, ConfigurationSection configSection) {
        //获取NBTCompound对象并将NBT存入Compound
        if (configSection == null || configSection.getKeys(false).size() < 1)
            return item;
        Object nmsItem = bukkit2NmsItem(item);
        Object nmsNbtCompoundObj = getNmsItemNbtTags(item);
        if (nmsNbtCompoundObj == null) {
            nmsNbtCompoundObj = new CompoundNbtTag().toNmsNbt();
        }
        NbtHandler.setNbt2NmsCompound(configSection, nmsNbtCompoundObj);

        //将NBTCompound设置回物品
        try {
            if (setNbtCompound2ItemMethod == null) {
                String setNbtCompound2ItemMethodName = setNbtCompound2ItemMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "c");
                setNbtCompound2ItemMethod = nmsItemClass.getMethod(setNbtCompound2ItemMethodName, nmsNbtCompoundObj.getClass());
            }
            setNbtCompound2ItemMethod.invoke(nmsItem, nmsNbtCompoundObj);
            return nms2BukkitItem(nmsItem);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nms2BukkitItem(nmsItem);
    }

    private static void loadGetNbtCompoundFromItemMethodNameMap() {
        getNbtCompoundFromItemMethodNameMap.put("v1_19_R2", "u");
        getNbtCompoundFromItemMethodNameMap.put("v1_19_R1", "u");
        getNbtCompoundFromItemMethodNameMap.put("v1_18_R2", "t");
        getNbtCompoundFromItemMethodNameMap.put("v1_18_R1", "s");
        getNbtCompoundFromItemMethodNameMap.put("v1_17_R1", "getTag");
        getNbtCompoundFromItemMethodNameMap.put("v1_16_R3", "getTag");
        getNbtCompoundFromItemMethodNameMap.put("v1_16_R2", "getTag");
        getNbtCompoundFromItemMethodNameMap.put("v1_16_R1", "getTag");
        getNbtCompoundFromItemMethodNameMap.put("v1_15_R1", "getTag");
        getNbtCompoundFromItemMethodNameMap.put("v1_14_R1", "getTag");
        getNbtCompoundFromItemMethodNameMap.put("v1_13_R2", "getTag");
        getNbtCompoundFromItemMethodNameMap.put("v1_13_R1", "getTag");
    }

    private static void loadSetNbtCompound2ItemMethodNameMap() {
        setNbtCompound2ItemMethodNameMap.put("v1_19_R2", "c");
        setNbtCompound2ItemMethodNameMap.put("v1_19_R1", "c");
        setNbtCompound2ItemMethodNameMap.put("v1_18_R2", "c");
        setNbtCompound2ItemMethodNameMap.put("v1_18_R1", "c");
        setNbtCompound2ItemMethodNameMap.put("v1_17_R1", "setTag");
        setNbtCompound2ItemMethodNameMap.put("v1_16_R3", "setTag");
        setNbtCompound2ItemMethodNameMap.put("v1_16_R2", "setTag");
        setNbtCompound2ItemMethodNameMap.put("v1_16_R1", "setTag");
        setNbtCompound2ItemMethodNameMap.put("v1_15_R1", "setTag");
        setNbtCompound2ItemMethodNameMap.put("v1_14_R1", "setTag");
        setNbtCompound2ItemMethodNameMap.put("v1_13_R2", "setTag");
        setNbtCompound2ItemMethodNameMap.put("v1_13_R1", "setTag");
    }

    private static void loadNmsItemClassNameMap() {
        nmsItemClassNameMap.put("v1_19_R2", "net.minecraft.world.item.ItemStack");
        nmsItemClassNameMap.put("v1_19_R1", "net.minecraft.world.item.ItemStack");
        nmsItemClassNameMap.put("v1_18_R2", "net.minecraft.world.item.ItemStack");
        nmsItemClassNameMap.put("v1_18_R1", "net.minecraft.world.item.ItemStack");
        nmsItemClassNameMap.put("v1_17_R1", "net.minecraft.world.item.ItemStack");
        nmsItemClassNameMap.put("v1_16_R3", "net.minecraft.server.v1_16_R3.ItemStack");
        nmsItemClassNameMap.put("v1_16_R2", "net.minecraft.server.v1_16_R2.ItemStack");
        nmsItemClassNameMap.put("v1_16_R1", "net.minecraft.server.v1_16_R1.ItemStack");
        nmsItemClassNameMap.put("v1_15_R1", "net.minecraft.server.v1_15_R1.ItemStack");
        nmsItemClassNameMap.put("v1_14_R1", "net.minecraft.server.v1_14_R1.ItemStack");
        nmsItemClassNameMap.put("v1_13_R2", "net.minecraft.server.v1_13_R2.ItemStack");
        nmsItemClassNameMap.put("v1_13_R1", "net.minecraft.server.v1_13_R1.ItemStack");
    }

}
