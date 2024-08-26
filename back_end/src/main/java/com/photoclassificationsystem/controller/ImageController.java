package com.photoclassificationsystem.controller;

import com.photoclassificationsystem.pojo.Result;
import com.photoclassificationsystem.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/images")
public class ImageController {
    @Autowired
    private ImageService imageService;

    //导入图片
    @PostMapping("/add")
    public Result addImage(MultipartFile image) throws IOException {
        imageService.handleImage(image);
        return Result.success();
    }

    //删除图片
    @DeleteMapping("/delete")
    public Result deleteImage(@RequestParam int id) {
        imageService.deleteById(id);
        return Result.success();
    }

}
