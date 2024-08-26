package com.photoclassificationsystem.utils;

import java.text.DecimalFormat;

public class FileSizeConvert {
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    /**
     * 将文件大小从字节（bytes）转换为兆字节（MB）。
     *
     * @param sizeInBytes 文件大小，单位为字节（bytes）。
     * @return 文件大小，单位为兆字节（MB），返回值为int类型。
     */
    public static String convertBytesToMB(long sizeInBytes) {
        float sizeMB = (float)(sizeInBytes * 1.0 / (1024 * 1024));
        return decimalFormat.format(sizeMB);
    }
}
