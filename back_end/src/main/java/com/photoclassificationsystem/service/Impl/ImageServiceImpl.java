package com.photoclassificationsystem.service.Impl;


import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.photoclassificationsystem.mapper.ImageMapper;
import com.photoclassificationsystem.pojo.ImageInfo;
import com.photoclassificationsystem.service.ImageService;
import com.photoclassificationsystem.utils.FileSizeConvert;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    private static final DateTimeFormatter EXIF_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    @Autowired
    private ImageMapper imageMapper;

    // 引入FileStorageImpl
    private final FileStorageImpl fileStorage = new FileStorageImpl();

    //上传文件
    @Override
    public void handleImage(MultipartFile image, String sort) throws IOException {
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
        // 写入数据库
        imageMapper.insertImage(imageInfo);
    }

    //删除图片
    @Override
    public void deleteById(int id) {
        String fileUrl = imageMapper.getUrlById(id);
        System.out.println("URL: " + fileUrl);
        // 删除本地文件
        if (fileUrl != null) {
            File file = new File(fileUrl);
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
    }

    @Override
    public void deleteByUrl(String Url) {

    }

    //批量导入
    @Override
    public void handleImagesBatch(MultipartFile[] images, List<String> sorts) throws IOException {
        for (int i = 0; i < images.length; i++) {
            String sort = sorts.get(i);
            handleImage(images[i], sort);
        }
    }

    private void readFileAttributes(MultipartFile file,ImageInfo imageInfo) {

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