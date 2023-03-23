package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.config.YamlFileWrapper;
import com.github.yufiriamazenta.craftorithm.item.nbt.CompoundNbtTag;
import com.github.yufiriamazenta.craftorithm.item.nbt.IPluginNbtTag;
import com.github.yufiriamazenta.craftorithm.item.nbt.NbtHandler;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ItemUtil {

    private static final Map<String, String> getNbtCompoundFromItemMethodNameMap;
    private static final Map<String, String> setNbtCompound2ItemMethodNameMap;
    private static final Map<String, String> nmsItemClassNameMap;
    private static final Class<?> nmsItemClass;
    private static String cannotCraftLore;
    private static Pattern cannotCraftLorePattern;
    private static boolean cannotCraftLoreIsRegex;
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

        reloadCannotCraftLore();
    }

    public static void reloadCannotCraftLore() {
        cannotCraftLore = LangUtil.color(Craftorithm.getInstance().getConfig().getString("lore_cannot_craft", "lore_cannot_craft"));
        try {
            cannotCraftLorePattern = Pattern.compile(cannotCraftLore);
            cannotCraftLoreIsRegex = true;
        } catch (PatternSyntaxException e) {
            cannotCraftLoreIsRegex = false;
        }
    }

    /**
     * 将Bukkit物品转化为NMS物品
     * @param item 传入的物品
     * @return 转化完成的物品
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

    /**
     * 将NMS物品转化为Bukkit物品
     * @param nmsItem 传入的NMS物品
     * @return 转化完成的物品
     */
    public static ItemStack nms2BukkitItem(Object nmsItem) {
        if (!nmsItem.getClass().getName().contains("ItemStack"))
            return null;
        try {
            Class<?> craftItemClass = Class.forName("org.bukkit.craftbukkit." + NbtHandler.getNmsVersion() + ".inventory.CraftItemStack");
            Method asBukkitCopyMethod = craftItemClass.getMethod("asBukkitCopy", nmsItemClass);
            return (ItemStack) asBukkitCopyMethod.invoke(null, nmsItem);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            //提示版本不兼容
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取物品的全部NMS NBT标签，类型应为NBTCompound
     * @param item 传入的物品
     * @return 返回的NMS NBT标签
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

    /**
     * 将物品保存到配置文件
     * @param item 保存的物品
     * @param configFile 保存到的配置文件
     * @param key 物品保存的Key
     */
    public static void saveItem2Config(ItemStack item, YamlFileWrapper configFile, String key) {
        if (item == null)
            throw new IllegalArgumentException("Item can not be null");
        String material = item.getType().getKey().toString();
        configFile.getConfig().set(key + ".material", material);
        int amount = item.getAmount();
        configFile.getConfig().set(key + ".amount", amount);
        Object nmsNbt = ItemUtil.getNmsItemNbtTags(item);
        IPluginNbtTag<?> nbtTag = NbtHandler.nmsNbt2PluginNbtObj(nmsNbt);
        if (nbtTag != null)
            NbtHandler.setPluginNbt2Config(configFile.getConfig(), key + ".nbt", (CompoundNbtTag) nbtTag);
        configFile.saveConfig();
    }

    /**
     * 从指定的配置文件中读取物品
     * @param config 读取的配置文件
     * @param key 物品的key
     * @return 返回读取到的物品，可能会为null
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
        return loadItemTagsFromConfig(item, cfgSection);
    }

    /*
    从配置文件中读取NBT，存储到物品中，然后返回物品
     */
    public static ItemStack loadItemTagsFromConfig(ItemStack item, ConfigurationSection configSection) {
        if (configSection == null || configSection.getKeys(false).size() < 1)
            return item;
        CompoundNbtTag nbtTagCompound = new CompoundNbtTag((MemorySection) configSection);
        return setItemNmsTag(item, nbtTagCompound);
    }

    /*
    将插件NBT转换为NMS NBT并存储到物品中
     */
    public static ItemStack setItemNmsTag(ItemStack item, CompoundNbtTag nbtCompound) {
        Object nmsItem = bukkit2NmsItem(item);
        Object nmsNbtCompound = nbtCompound.toNmsNbt();

        try {
            if (setNbtCompound2ItemMethod == null) {
                String setNbtCompound2ItemMethodName = setNbtCompound2ItemMethodNameMap.getOrDefault(NbtHandler.getNmsVersion(), "c");
                setNbtCompound2ItemMethod = nmsItemClass.getMethod(setNbtCompound2ItemMethodName, nmsNbtCompound.getClass());
            }
            setNbtCompound2ItemMethod.invoke(nmsItem, nmsNbtCompound);
            return nms2BukkitItem(nmsItem);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return nms2BukkitItem(nmsItem);
    }

    /**
     * 检测物品是否含有不允许用于合成的lore
     * @param items 传入的物品数组
     * @return 是否包含所需字符串
     */
    public static boolean hasCannotCraftLore(ItemStack[] items) {
        boolean containsLore = false;

        for (ItemStack item : items) {
            if (item == null)
                continue;
            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                continue;
            List<String> lore = item.getItemMeta().getLore();
            if (lore == null)
                continue;
            for (String loreStr : lore) {
                if (!cannotCraftLoreIsRegex) {
                    if (loreStr.equals(cannotCraftLore)) {
                        containsLore = true;
                        break;
                    }
                } else {
                    Matcher matcher = cannotCraftLorePattern.matcher(loreStr);
                    if (matcher.find()) {
                        containsLore = true;
                        break;
                    }
                }
            }
            if (containsLore)
                break;
        }
        return containsLore;
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

    public static boolean checkItemIsAir(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }
}
