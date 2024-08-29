package com.photoclassificationsystem.service;

import com.photoclassificationsystem.pojo.ImageInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    //导入图片
    void handleImage(MultipartFile image) throws IOException;

    //删除图片
    void deleteById(int id);

    //批量导入
    void handleImagesBatch(MultipartFile[] images) throws IOException;

}
