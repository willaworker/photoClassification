package com.photoclassificationsystem.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//废弃
public class DateFormatConvert {
    /**
     * 将Date对象格式化为 "yyyy-MM-dd" 格式的字符串。
     *
     * @param date 输入的Date对象。
     * @return 转换后的日期字符串，格式为 "yyyy-MM-dd"。
     */
    public static Date convertDateToDateWithoutTime(Date date) throws ParseException {
        // 目标日期格式
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 将 Date 对象格式化为目标格式的字符串
        String formattedDate = targetFormat.format(date);
        // 将格式化后的字符串转回 Date 对象
        Date targetDate = targetFormat.parse(formattedDate);
        return targetDate;
    }
}
