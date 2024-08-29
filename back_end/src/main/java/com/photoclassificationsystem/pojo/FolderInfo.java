package com.photoclassificationsystem.pojo;

import lombok.Data;

import java.util.List;

@Data
public class FolderInfo {
    private String folderName;
    private List<ImageInfo> files;

    public FolderInfo(String folderName, List<ImageInfo> files) {
        this.folderName = folderName;
        this.files = files;
    }
}
