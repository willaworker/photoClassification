package com.photoclassificationsystem.controller;

import com.photoclassificationsystem.pojo.ImageInfo;
import com.photoclassificationsystem.pojo.FolderInfo;
import com.photoclassificationsystem.pojo.Result;
import com.photoclassificationsystem.service.ImageService;
import com.photoclassificationsystem.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/images")
@CrossOrigin
public class ImageController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private ResponseService responseService;

    //导入图片
    @PostMapping("/add")
    @CrossOrigin
    public Result addImage(@RequestParam("image") MultipartFile image,
                           @RequestParam("sort") String sort,
                           @RequestParam("uploadTime") String uploadTime) throws IOException {
        imageService.handleImage(image, sort, uploadTime);
        return Result.success();
    }


    //批量导入图片
    @PostMapping("/addBatch")
    @CrossOrigin
    public Result addImagesBatch(@RequestParam("images") MultipartFile[] images,
                                 @RequestParam("sort") List<String> sorts,
                                 @RequestParam("uploadTime") List<String> uploadTime) throws IOException {
        imageService.handleImagesBatch(images, sorts, uploadTime);
        return Result.success();
    }

    //删除图片
    @PostMapping ("/delete")
    public Result deleteImage(@RequestParam("timestamp") String timestamp) {
        System.out.println("Received timestamp: " + timestamp);
        imageService.deleteByTimestamp(timestamp);
        return Result.success();
    }

    @DeleteMapping("/deleteByUrl")
    @CrossOrigin
    public Result deleteImageByUrl(@RequestParam String Url) {
        imageService.deleteByUrl(Url);
        return Result.success();
    }

    // 获取文件夹及其子文件信息
    @GetMapping("/folderData")
    @CrossOrigin
    public List<FolderInfo> getFolderData() {
        try {
            String currentWorkingDirectory = System.getProperty("user.dir");
            String realFolderPath = currentWorkingDirectory + File.separator + "ImagesArchivedByDate";
            System.out.println("Real folder path: " + realFolderPath);
            return responseService.getFolderData(realFolderPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取文件夹数据时出错: " + e.getMessage());
        }
    }
}
