package com.photoclassificationsystem.utils;

import java.io.File;

public class GetUniqueFilename {
    /**
     * 检查文件名是否冲突，如果冲突则生成新的文件名
     *
     * @param filePath 文件路径
     * @return 不冲突的文件路径
     */
    public static String getUniqueFileName(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return filePath;
        }

        String extension = "";  // 文件扩展名
        String baseName = filePath;  // 文件名基础部分，不包括扩展名

        int dotIndex = filePath.lastIndexOf(".");
        if (dotIndex != -1) {
            extension = filePath.substring(dotIndex);
            baseName = filePath.substring(0, dotIndex);
        }

        int count = 1;
        String newFilePath = baseName + "(" + count + ")" + extension;
        file = new File(newFilePath);

        while (file.exists()) {
            count++;
            newFilePath = baseName + "(" + count + ")" + extension;
            file = new File(newFilePath);
        }

        return newFilePath;
    }
}
