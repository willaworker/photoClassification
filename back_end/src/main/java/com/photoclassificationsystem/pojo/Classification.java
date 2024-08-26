package com.photoclassificationsystem.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Classification {

    private int id;
    private String name;
    private List<ImageInfo> images;
}
