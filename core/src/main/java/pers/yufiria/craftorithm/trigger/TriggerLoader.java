package pers.yufiria.craftorithm.trigger;

import crypticlib.chat.BukkitMsgSender;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.script.ScriptEngine;
import crypticlib.script.compile.CompiledScript;
import crypticlib.util.IOHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 触发器 YAML 加载器
 * 负责从 triggers 文件夹解析触发器配置（含新旧 conditions 格式兼容）
 */
public final class TriggerLoader {

    /**
     * 加载 triggers 文件夹下的所有触发器
     */
    static List<Trigger> loadAll(File triggerFolder) {
        List<Trigger> parsedTriggers = new ArrayList<>();
        if (triggerFolder.exists()) {
            for (File file : IOHelper.allYamlFiles(triggerFolder)) {
                parsedTriggers.addAll(parseTriggersFromConfigFile(file));
            }
        } else {
            triggerFolder.mkdirs();
        }
        return parsedTriggers;
    }

    private static List<Trigger> parseTriggersFromConfigFile(File file) {
        List<Trigger> parsedTriggers = new ArrayList<>();
        String fileName = file.getName();
        // 去掉扩展名作为文件标识
        String fileKey = fileName.contains(".")
            ? fileName.substring(0, fileName.lastIndexOf('.'))
            : fileName;

        BukkitConfigWrapper wrapper = new BukkitConfigWrapper(file);
        YamlConfiguration config = wrapper.config();

        for (String localId : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(localId);
            if (section == null) continue;

            try {
                String fullId = fileKey + ":" + localId;
                Trigger trigger = parseTrigger(fullId, section);

                if (trigger == null) continue;

                parsedTriggers.add(trigger);
                if (trigger.isEnabled()) {
                    BukkitMsgSender.INSTANCE.info(
                        "Loaded trigger '" + localId + "' in " + fileName
                    );
                }
            } catch (Throwable throwable) {
                BukkitMsgSender.INSTANCE.info(
                    "&cFailed to load trigger '" + localId + "' in " + fileName
                );
                throwable.printStackTrace();
            }
        }
        return parsedTriggers;
    }

    /**
     * 解析单个触发器
     *
     * YAML 结构:
     *   type: 'crafting'
     *   recipes: [...]
     *   conditions: [条件脚本]    ← 正向逻辑，成立=放行
     *   actions: [动作脚本]
     */
    private static @Nullable Trigger parseTrigger(String fullId, ConfigurationSection section) {
        String typeKey = section.getString("type");
        if (typeKey == null) {
            BukkitMsgSender.INSTANCE.info("&eTrigger '" + fullId + "' missing 'type' field");
            return null;
        }

        if (TriggerManager.INSTANCE.getTriggerType(typeKey) == null) {
            BukkitMsgSender.INSTANCE.info("&eUnknown trigger type '" + typeKey + "' in " + fullId);
            return null;
        }

        List<NamespacedKey> recipeKeys = section.getStringList("recipes").stream().map(NamespacedKey::fromString).toList();

        // 编译 conditions 脚本
        // 旧格式（列表，默认 && 连接）:
        //   conditions:
        //     - 'level() >= 10'
        //     - 'perm("vip")'
        // 新格式（对象，支持 mode 字段）:
        //   conditions:
        //     mode: 'block'
        //     body:
        //       - 'if level() >= 10'
        //       - '  return true'
        //       - 'endif'
        CompiledScript conditionScript = null;
        ConfigurationSection condSection = section.getConfigurationSection("conditions");
        if (condSection != null) {
            // 新格式：对象模式
            conditionScript = compileConditions(
                fullId,
                condSection.getString("mode", "and"),
                condSection.getStringList("body")
            );
        } else {
            // 旧格式：列表模式（默认 && 连接）
            conditionScript = compileConditions(fullId, "and", section.getStringList("conditions"));
        }

        // 编译 actions 脚本
        List<String> actSources = section.getStringList("actions");
        String actSource = String.join("\n", actSources);
        CompiledScript actionScript = ScriptEngine.INSTANCE.compile(fullId + "_act", actSource);

        int priority = section.getInt("priority", 0);
        boolean enable;
        if (section.isBoolean("enable")) {
            enable = section.getBoolean("enable", true);
        } else if (section.isBoolean("enabled")) {
            //用于兼容旧版配置
            enable = section.getBoolean("enabled", true);
        } else {
            enable = true;
        }
        long cooldown = (long) (section.getDouble("cooldown", 0) * 1000);
        boolean perPlayer = section.getBoolean("per_player", true);

        return new Trigger(
            fullId, typeKey, recipeKeys, conditionScript, actionScript,
            priority, enable, cooldown, perPlayer
        );
    }

    /**
     * 编译条件脚本
     * mode 为 script 时按脚本块拼接，否则各条件用 && 连接（正向逻辑，成立=放行）
     */
    private static @Nullable CompiledScript compileConditions(String fullId, String mode, List<String> condSources) {
        if (condSources.isEmpty()) return null;
        String joined = "script".equals(mode)
            ? String.join("\n", condSources)
            : condSources.size() == 1
              ? condSources.getFirst()
              : condSources.stream().map(c -> "(" + c + ")").collect(Collectors.joining(" && "));
        return ScriptEngine.INSTANCE.compile(fullId + "_cond", joined);
    }

}
