package me.rejomy.tnttag.util.file;

import me.rejomy.tnttag.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public void create(File file) {
        try {
            file.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void save(File file, YamlConfiguration config) {
        try {
            config.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
