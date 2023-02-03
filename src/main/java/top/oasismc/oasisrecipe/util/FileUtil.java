package top.oasismc.oasisrecipe.util;

import java.io.File;
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
                fileList.add(file);
            }
        }
        return fileList;
    }

}
