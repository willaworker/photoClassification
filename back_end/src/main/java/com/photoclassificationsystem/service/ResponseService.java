package com.photoclassificationsystem.service;

import com.photoclassificationsystem.pojo.FolderInfo;
import com.photoclassificationsystem.pojo.ImageInfo;

import java.util.List;

public interface ResponseService {
    List<FolderInfo> getFolderData(String folderPath);
}
