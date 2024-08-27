package com.photoclassificationsystem.mapper;

import com.photoclassificationsystem.pojo.ImageInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageMapper {
    @Select("SELECT * FROM Images WHERE id = #{id}")
    ImageInfo getImageById(int id);

    //上传图片
    @Insert("INSERT INTO Images(url, name, photo_time, upload_time, size, formatType, place, device, category) " +
            "VALUES(#{url}, #{name}, #{photoTime}, #{uploadTime}, #{size}, #{formatType}, #{place}, #{device}, #{category})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertImage(ImageInfo imageInfo);

    //删除图片
    @Delete("DELETE FROM Images WHERE id = #{id}")
    void deleteImageById(int id);

    @Delete("DELETE FROM Images WHERE url = #{url}")
    int deleteImageByUrl(String url);

    @Select("SELECT * FROM Images ORDER BY ${order}")
    List<ImageInfo> getImagesByOrder(@Param("order") String order);

    @Select("SELECT * FROM Images WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<ImageInfo> searchImages(String keyword);


}
