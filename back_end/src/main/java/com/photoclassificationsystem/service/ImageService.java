package com.photoclassificationsystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    //导入图片
    void handleImage(MultipartFile image, String sort) throws IOException;

    //删除图片
    void deleteById(int id);

    void deleteByUrl(String Url);

    //批量导入
    void handleImagesBatch(MultipartFile[] images, List<String> sorts) throws IOException;

}
