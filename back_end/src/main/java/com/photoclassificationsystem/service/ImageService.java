package com.photoclassificationsystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface ImageService {

    //导入图片
    void handleImage(MultipartFile image, String sort, String lastModified, String device, String place) throws IOException;

    //删除图片
    void deleteById(int id) throws UnsupportedEncodingException;
    void deleteByTimestamp(String Timestamp) throws UnsupportedEncodingException;

    void deleteByUrl(String Url);

    //批量导入
    void handleImagesBatch(MultipartFile[] images, List<String> sorts, List<String> uploadTime, List<String> device, List<String> place) throws IOException;

    String processRawFile(String folderName, String fileNameOfUrl) throws IOException;
}
