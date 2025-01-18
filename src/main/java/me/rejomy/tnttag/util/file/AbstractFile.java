package me.rejomy.tnttag.util.file;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class AbstractFile {
    private FileUtil fileUtil = new FileUtil();
    private YamlConfiguration config;
    private File file;
    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public AbstractFile(File path, String name) {
        file = new File(path, name.replace(".yml", "") + ".yml");

        if(!path.exists()) {
            path.mkdirs();
        }
    }

    public void create() {
        fileUtil.create(file);
        load();
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(file);
    }

}
