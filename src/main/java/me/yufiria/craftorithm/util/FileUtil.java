package me.yufiria.craftorithm.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<File> getAllFiles(File folder) {
        List<File> fileList = new ArrayList<>();
        if (folder.isFile() || !folder.exists()) {
            return fileList;
        }
        File[] files = folder.listFiles();
        if (files == null)
            return fileList;
        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll(getAllFiles(file));
            } else {
                if (file.getName().endsWith(".yml"))
                    fileList.add(file);
            }
        }
        return fileList;
    }

    public static boolean createNewFile(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
