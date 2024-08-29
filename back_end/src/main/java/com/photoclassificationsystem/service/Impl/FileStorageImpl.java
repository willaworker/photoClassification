package com.photoclassificationsystem.service.Impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import com.photoclassificationsystem.pojo.ImageInfo;
import org.springframework.web.multipart.MultipartFile;

import static com.photoclassificationsystem.utils.GetUniqueFilename.getUniqueFileName;

public class FileStorageImpl {

    private String storageDirectory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String CONFIG_FILE = "config.properties"; // 配置文件名称
    private static final String DIRECTORY_KEY = "storageDirectory"; // 存储路径的键值

    public FileStorageImpl() {
        // 在构造函数中尝试加载持久化的路径
        storageDirectory = loadDirectoryPath();
        // 检查路径是否为默认相对路径
        if ("ImagesArchivedByDate".equals(storageDirectory) || storageDirectory == null || storageDirectory.isEmpty()) {
            storageDirectory = initializeDefaultDirectory();
            saveDirectoryPath(storageDirectory); // 保存绝对路径到配置文件
        }
    }


    // 初始化默认存储目录
    private String initializeDefaultDirectory() {
        // 当前工作目录
        String currentWorkingDirectory = System.getProperty("user.dir");

        // 创建默认文件夹
        String defaultDirectory = currentWorkingDirectory + File.separator + "ImagesArchivedByDate";
        File directory = new File(defaultDirectory);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new RuntimeException("无法创建目录：" + defaultDirectory);
            }
        }

        return directory.getAbsolutePath();
    }

    // 保存文件到本地目录
    public String saveFileToLocal(MultipartFile file, ImageInfo imageInfo) throws IOException {

        // 根据 photoTime 生成日期子文件夹名称
        String dateFolder = "unknown_date";
        if (imageInfo.getPhotoTime() != null) {
            dateFolder = imageInfo.getPhotoTime().format(DATE_FORMATTER);
        }

        // 生成保存路径，包含日期子文件夹
        String filePath = storageDirectory + File.separator + dateFolder + File.separator + file.getOriginalFilename();

        // 检查文件是否已存在，并生成不冲突的文件名
        filePath = getUniqueFileName(filePath);

        // 保存文件
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());

        // 返回保存路径
        return filePath;
    }

    // 保存路径到配置文件
    private void saveDirectoryPath(String path) {
        Properties properties = new Properties();
        properties.setProperty(DIRECTORY_KEY, path);

        // 获取 resources 目录路径
        String resourcesDirectory = new File(System.getProperty("user.dir"), "/back_end/src/main/resources").getAbsolutePath();
        File configFile = new File(resourcesDirectory, CONFIG_FILE);

        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            properties.store(outputStream, "File Storage Configuration");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("保存路径到配置文件时出错", e);
        }
    }

    // 从配置文件中读取路径
    private String loadDirectoryPath() {
        Properties properties = new Properties();

        // 获取 resources 目录路径
        String resourcesDirectory = new File(System.getProperty("user.dir"), "back_end/src/main/resources").getAbsolutePath();
        File configFile = new File(resourcesDirectory, CONFIG_FILE);

        try (FileInputStream inputStream = new FileInputStream(configFile)) {
            properties.load(inputStream);
            return properties.getProperty(DIRECTORY_KEY);
        } catch (IOException e) {
            System.out.println("无法加载配置文件，可能是首次运行。");
            return null;
        }
    }
}
