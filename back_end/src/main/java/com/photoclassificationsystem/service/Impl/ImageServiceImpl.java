package com.photoclassificationsystem.service.Impl;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.photoclassificationsystem.mapper.ImageMapper;
import com.photoclassificationsystem.pojo.ImageInfo;
import com.photoclassificationsystem.service.ImageService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private static final DateTimeFormatter EXIF_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    @Autowired
    private ImageMapper imageMapper;

    // 引入FileStorageImpl
    private final FileStorageImpl fileStorage = new FileStorageImpl();

    //上传文件
    @Override
    public void handleImage(MultipartFile image, String sort, String uploadTimeVue, String device, String place) throws IOException {
        ImageInfo imageInfo = new ImageInfo();
        readFileAttributes(image,imageInfo);
        imageInfo.setCategory(sort);
        // System.out.println("sort"+sort);
        // 使用FileStorageImpl保存文件到本地
        String image_URL = fileStorage.saveFileToLocal(image,imageInfo);

        File file = new File(image_URL);
        URI fileUri = file.toURI();
        String url = fileUri.toString(); // 将路径转换为URL格式
        if (url.startsWith("file:/")) {
            url = url.substring(6); // 从索引 6 开始截取字符串
        }
        url = url.replace("/", "\\");
        System.out.println("数据库里的URL: " + url);
        imageInfo.setUrl(url);
        imageInfo.setUploadTimeVue(String.valueOf((uploadTimeVue)));

        String deviceString = device.replaceAll("^\"|\"$", "");
        String placeString = place.replaceAll("^\"|\"$", "");
        imageInfo.setDevice(deviceString);
        imageInfo.setPlace(placeString);

        // 写入数据库
        imageMapper.insertImage(imageInfo);
    }

    //删除图片
    @Override
    public void deleteById(int id) throws UnsupportedEncodingException {
        String fileUrl = imageMapper.getUrlById(id);
        // 解码文件路径中的 %20 等特殊字符
        String decodedFileUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);
        // 删除本地文件
        if (decodedFileUrl != null) {
            File file = new File(decodedFileUrl);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("本地文件删除成功: " + fileUrl);
                } else {
                    System.out.println("本地文件删除失败: " + fileUrl);
                }
            } else {
                System.out.println("文件不存在: " + fileUrl);
            }
        }
        // 删除数据库对应数据
        imageMapper.deleteImageById(id);
        // 遍历ImagesArchivedByDate文件夹，删除空文件夹
        String currentWorkingDirectory = System.getProperty("user.dir");
        String storageDirectory = currentWorkingDirectory + File.separator + "ImagesArchivedByDate";
        deleteEmptyDirectories(new File(storageDirectory));

        // 可选：在删除操作之间添加短暂的延迟
        try {
            Thread.sleep(50);  // 50 毫秒的延迟
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void deleteEmptyDirectories(File directory) {
        if (directory != null && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归删除子目录中的空文件夹
                        deleteEmptyDirectories(file);
                        File[] subFiles = file.listFiles();
                        if (subFiles != null && subFiles.length == 0) {
                            if (file.delete()) {
                                System.out.println("空文件夹删除成功: " + file.getPath());
                            } else {
                                System.out.println("空文件夹删除失败: " + file.getPath());
                            }
                        }
                    }
                }
            }
        }
    }



    @Override
    public void deleteByTimestamp(String timestamp) throws UnsupportedEncodingException {
        int targetId = imageMapper.getIdByTimestamp(timestamp);
        deleteById(targetId);
    }
    public void deleteByUrl(String Url) {

    }

    //批量导入
    @Override
    public void handleImagesBatch(MultipartFile[] images, List<String> sorts, List<String> uploadTime, List<String> device, List<String> place) throws IOException {
        for (int i = 0; i < images.length; i++) {
            String sort = sorts.get(i);
            String eachUploadTimeVue = uploadTime.get(i);
            String eachDevice = device.get(i);
            String eachPlace = place.get(i);
            handleImage(images[i], sort, eachUploadTimeVue, eachDevice, eachPlace);
        }
    }

    @Override
    public String processRawFile(String folderName, String fileNameOfUrl) throws IOException {
        String rawDirectoryPath = System.getProperty("user.dir")
                + File.separator + "ImagesArchivedByDate" + File.separator + "raw";
        // 创建 "raw" 目录，如果它不存在
        File rawDirectory = new File(rawDirectoryPath);
        if (!rawDirectory.exists()) {
            if (!rawDirectory.mkdirs()) {
                throw new IOException("Failed to create directory: " + rawDirectoryPath);
            }
        }

        // 定义要处理的文件的路径
        String targetPath = System.getProperty("user.dir")
                + File.separator + "ImagesArchivedByDate" + File.separator + folderName + File.separator + fileNameOfUrl;

        // 将 targetPath 指定的文件转换为 InputStream
        File inputFile = new File(targetPath);
        if (!inputFile.exists()) {
            throw new IOException("The specified file does not exist: " + targetPath);
        }

        try (InputStream inputStream = new FileInputStream(inputFile);
             ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream)) {

            // 使用 ImageIO 读取 RAW/DNG 文件
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
            if (!readers.hasNext()) {
                throw new IOException("No suitable ImageReader found for RAW/DNG file");
            }

            ImageReader reader = readers.next();
            reader.setInput(imageInputStream);
            BufferedImage bufferedImage = reader.read(0);

            // 生成唯一的文件名并保存文件到 "raw" 目录
            String fileName = UUID.randomUUID().toString() + ".jpg";
            File outputFile = new File(rawDirectoryPath + File.separator + fileName);
            ImageIO.write(bufferedImage, "jpeg", outputFile);

            // 返回生成的图像 URL
            return "http://localhost:8080/raw/" + fileName;
        }
    }

    private void readFileAttributes(MultipartFile file,ImageInfo imageInfo) {

        // 注意:
        // device和place被覆盖

        // 文件名称
        String fileName = file.getOriginalFilename();
        // 文件扩展名
        String extension = FilenameUtils.getExtension(fileName);
        // 文件大小（字节）
        long size = file.getSize();

        imageInfo.setName(fileName); //获取文件名
        imageInfo.setSize(String.valueOf(size)); //获取文件大小
        imageInfo.setUploadTime(LocalDateTime.now()); //获取上传文件的时间
        // 获取文件格式（扩展名）
        if (fileName != null && fileName.contains(".")) {
            String formatType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            imageInfo.setFormatType(formatType);
        } else {
            imageInfo.setFormatType("unknown"); // 如果无法提取格式，设置为unknown
        }

        try (InputStream inputStream = file.getInputStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            // 读取EXIF子目录信息
            ExifSubIFDDirectory exifDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (exifDirectory != null) {
                String exifDateStr = exifDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                LocalDateTime photoTime = LocalDateTime.parse(exifDateStr, EXIF_DATE_FORMAT);
                imageInfo.setPhotoTime(photoTime);
                System.out.println(imageInfo.getPhotoTime());
            }
            else{
                Date date = new Date();
                imageInfo.setPhotoTime(null);
            }

            // 读取GPS信息（未实现）
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
                double latitude = gpsDirectory.getGeoLocation().getLatitude();
                double longitude = gpsDirectory.getGeoLocation().getLongitude();
                //System.out.println("拍摄地点: 纬度 " + latitude + ", 经度 " + longitude);
            }

            // 读取拍摄者信息
            ExifIFD0Directory ifd0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (ifd0Directory != null) {
                String artist = ifd0Directory.getString(ExifIFD0Directory.TAG_MAKE);
                imageInfo.setDevice(artist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}