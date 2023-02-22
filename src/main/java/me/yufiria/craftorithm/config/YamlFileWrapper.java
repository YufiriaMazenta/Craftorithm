package me.yufiria.craftorithm.config;

import me.yufiria.craftorithm.Craftorithm;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class YamlFileWrapper {

    private final File configFile;
    private YamlConfiguration config;
    private final String path;

    public YamlFileWrapper(String path) {
        this(path, Craftorithm.getInstance());
    }
    
    public YamlFileWrapper(String path, Plugin plugin) {
        this.path = path;
        File dataFolder = plugin.getDataFolder();
        configFile = new File(dataFolder, path);
        createDefaultConfig();
    }

    public YamlFileWrapper(File file) {
        this.configFile = file;
        this.path = file.getPath();
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    //创建默认配置文件
    private void createDefaultConfig() {
        if (!configFile.exists()) {
            Craftorithm.getInstance().saveResource(path, false);
        }
        reloadConfig();
    }

    //获取配置文件实例
    public YamlConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    //重载配置文件
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    //保存配置文件
    public synchronized void saveConfig() {
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getPath() { return path; }

}
