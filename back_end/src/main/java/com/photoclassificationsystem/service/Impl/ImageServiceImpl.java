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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class ImageServiceImpl implements ImageService {

    private static final DateTimeFormatter EXIF_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    @Autowired
    private ImageMapper imageMapper;

    //上传文件
    @Override
    public void handleImage(MultipartFile image) throws IOException {
        ImageInfo imageInfo = new ImageInfo();
        readFileAttributes(image,imageInfo);

        //转存至本地路径（后续将路径设为变量方便自定义）
        String image_URL = "F:\\360MoveData\\Users\\Acer\\Desktop\\test\\" + imageInfo.getName();
        image.transferTo(new File(image_URL));
        imageInfo.setUrl(image_URL);
        //写入数据库
        imageMapper.insertImage(imageInfo);
    }

    //删除图片
    @Override
    public void deleteById(int id) {
        imageMapper.deleteImageById(id);
    }

    //批量导入
    @Override
    public void handleImagesBatch(MultipartFile[] images) throws IOException {
        for (MultipartFile image : images) {
            handleImage(image); // 复用单个图片处理逻辑
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
        imageInfo.setSize(FileSizeConvert.convertBytesToMB(size)); //获取文件大小
        imageInfo.setUploadTime(LocalDateTime.now()); //获取上传文件的时间

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
                String artist = ifd0Directory.getString(ExifIFD0Directory.TAG_ARTIST);
                imageInfo.setDevice(artist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}