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
import java.util.Scanner;

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
    }

    // 检查是否已有存储路径
    private void checkAndSetDirectory() {
        if (storageDirectory == null || storageDirectory.isEmpty()) {
            // 如果未设置路径，则提示用户通过命令行输入路径
            Scanner scanner = new Scanner(System.in);
            System.out.print("请输入要归档的目标路径: ");
            String inputPath = scanner.nextLine();

            File selectedDirectory = new File(inputPath);

            // 验证用户输入的路径是否有效
            if (selectedDirectory.exists() && selectedDirectory.isDirectory()) {
                storageDirectory = selectedDirectory.getAbsolutePath();
                saveDirectoryPath(storageDirectory); // 保存路径
            } else {
                throw new RuntimeException("无效路径");
            }
        }
    }

    // 保存文件到本地目录
    public String saveFileToLocal(MultipartFile file, ImageInfo imageInfo) throws IOException {
        checkAndSetDirectory();

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

        try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE)) {
            properties.store(outputStream, "File Storage Configuration");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("保存路径到配置文件时出错", e);
        }
    }

    // 从配置文件中读取路径
    private String loadDirectoryPath() {
        Properties properties = new Properties();

        try (FileInputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
            return properties.getProperty(DIRECTORY_KEY);
        } catch (IOException e) {
            System.out.println("无法加载配置文件，可能是首次运行。");
            return null;
        }
    }
}
