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

        String newFilePath = filePath;
        int count = 1;
        while (file.exists()) {
            String extension = "";  // 文件扩展名
            String baseName = newFilePath;  // 文件名基础部分，不包括扩展名

            int dotIndex = newFilePath.lastIndexOf(".");
            if (dotIndex != -1) {
                extension = newFilePath.substring(dotIndex);
                baseName = newFilePath.substring(0, dotIndex);
            }

            // 增加(1), (2) 等后缀
            newFilePath = baseName + "(" + count + ")" + extension;
            file = new File(newFilePath);
            count++;
        }

        return newFilePath;
    }
}
