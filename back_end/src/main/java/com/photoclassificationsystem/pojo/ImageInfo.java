package com.photoclassificationsystem.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ImageInfo {
    private int id;
    private String url;
    private String name;
    private LocalDateTime photoTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;
    private String size;
    private String place;
    private String device;
}
