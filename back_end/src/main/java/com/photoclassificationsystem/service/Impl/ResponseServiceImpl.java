package com.photoclassificationsystem.service.Impl;

import com.photoclassificationsystem.mapper.ImageMapper;
import com.photoclassificationsystem.pojo.FolderInfo;
import com.photoclassificationsystem.pojo.ImageInfo;
import com.photoclassificationsystem.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResponseServiceImpl implements ResponseService {

    @Autowired
    private ImageMapper imageMapper;

    @Override
    public List<FolderInfo> getFolderData(String folderPath) {
        List<FolderInfo> folderInfoList = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get(folderPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!dir.equals(Paths.get(folderPath))) { // 忽略根目录
                        FolderInfo folderInfo = new FolderInfo(dir.getFileName().toString(), new ArrayList<>());

                        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                String fileUrl = file.toUri().toString();
                                if (fileUrl.startsWith("file:///")) {
                                    fileUrl = fileUrl.substring(8); // 从索引 8 开始截取字符串
                                }
                                ImageInfo imageInfo = new ImageInfo();

                                String[] pathParts = fileUrl.split("/");
                                String fileName = pathParts[pathParts.length - 1];
                                // 设置文件名到imageInfo
                                imageInfo.setNameOfUrl(fileName);

                                // 将所有的 / 替换为 \
                                fileUrl = fileUrl.replace("/", "\\");
                                System.out.println(fileUrl);

                                imageInfo.setUrl(fileUrl);

                                int imageId = imageMapper.getIdByUrl(fileUrl);
                                // 提取文件名部分

                                imageInfo.setName(imageMapper.getNameById(imageId));
                                imageInfo.setSize(imageMapper.getSizeById(imageId));
                                imageInfo.setPlace(imageMapper.getPlaceById(imageId));
                                imageInfo.setDevice(imageMapper.getDeviceById(imageId));
                                imageInfo.setFormatType(imageMapper.getFormatTypeById(imageId));
                                imageInfo.setCategory(imageMapper.getCategoryById(imageId));

                                // 解析并格式化 photoTimeString
                                String photoTimeString = imageMapper.getPhotoTimeById(imageId);
                                if (photoTimeString != null) {
                                    // 使用DateTimeFormatter解析字符串为LocalDateTime
                                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                                    LocalDateTime photoTime = LocalDateTime.parse(photoTimeString, inputFormatter);

                                    // 格式化为yyyy/MM/dd格式的字符串
                                    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                                    imageInfo.setFormattedPhotoTime(photoTime.format(outputFormatter));
                                }

                                // 设置 photoTime 和 uploadTime 为 null
                                imageInfo.setPhotoTime(null);
                                imageInfo.setUploadTime(null);

                                folderInfo.getFiles().add(imageInfo);

                                return FileVisitResult.CONTINUE;
                            }
                        });

                        folderInfoList.add(folderInfo);

                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("读取文件夹数据时出错", e);
        }

        return folderInfoList;
    }
}
