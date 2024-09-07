package com.photoclassificationsystem.service.Impl;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class StorageCleanupServiceImpl {
    public void deleteStorageDirectoryContents() {
        String currentWorkingDirectory = System.getProperty("user.dir");
        String storageDirectory = currentWorkingDirectory + File.separator + "ImagesArchivedByDate";

        File directory = new File(storageDirectory);
        if (directory.exists() && directory.isDirectory()) {
            deleteDirectoryContents(directory);
            System.out.println("已删除 " + storageDirectory + " 文件夹中的所有内容");
        } else {
            System.out.println(storageDirectory + " 文件夹不存在或不是一个目录");
        }
    }

    private void deleteDirectoryContents(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归删除子文件夹中的内容
                    deleteDirectoryContents(file);
                }
                // 删除文件或空的子文件夹
                if (file.delete()) {
                    System.out.println("删除成功: " + file.getPath());
                } else {
                    System.out.println("删除失败: " + file.getPath());
                }
            }
        }
    }
}
