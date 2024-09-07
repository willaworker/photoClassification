package com.photoclassificationsystem.mapper;

import com.photoclassificationsystem.pojo.ImageInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageMapper {
    @Select("SELECT * FROM Images WHERE id = #{id}")
    ImageInfo getImageById(int id);

    @Select("SELECT url FROM Images WHERE id = #{id}")
    String getUrlById(int id);

    //上传图片
    @Insert("INSERT INTO Images(url, name, photo_time, upload_time, size, formatType, place, device, category, uploadTimeVue) " +
            "VALUES(#{url}, #{name}, #{photoTime}, #{uploadTime}, #{size}, #{formatType}, #{place}, #{device}, #{category}, #{uploadTimeVue})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertImage(ImageInfo imageInfo);

    //删除图片
    @Delete("DELETE FROM Images WHERE id = #{id}")
    void deleteImageById(int id);

    @Delete("DELETE FROM Images WHERE uploadTimeVue = #{uploadTimeVue}")
    void deleteImageByTimestamp(String uploadTimeVue);

    @Delete("DELETE FROM Images WHERE url = #{url}")
    int deleteImageByUrl(String url);

    @Select("SELECT * FROM Images ORDER BY ${order}")
    List<ImageInfo> getImagesByOrder(@Param("order") String order);

    @Select("SELECT * FROM Images WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<ImageInfo> searchImages(String keyword);

    @Select("SELECT id FROM Images WHERE url = #{url}")
    int getIdByUrl(String url);

    @Select("SELECT name FROM Images WHERE id = #{id}")
    String getNameById(int id);

    @Select("SELECT photo_time FROM Images WHERE id = #{id}")
    String getPhotoTimeById(int id);

    @Select("SELECT upload_time FROM Images WHERE id = #{id}")
    String getUploadTimeById(int id);

    @Select("SELECT size FROM Images WHERE id = #{id}")
    String getSizeById(int id);

    @Select("SELECT place FROM Images WHERE id = #{id}")
    String getPlaceById(int id);

    @Select("SELECT device FROM Images WHERE id = #{id}")
    String getDeviceById(int id);

    @Select("SELECT formatType FROM Images WHERE id = #{id}")
    String getFormatTypeById(int id);

    @Select("SELECT category FROM Images WHERE id = #{id}")
    String getCategoryById(int id);
    @Select("SELECT uploadTimeVue FROM Images WHERE id = #{id}")
    String getUploadTimeVueById(int id);

    @Select("SELECT id FROM Images WHERE uploadTimeVue = #{uploadTimeVue}")
    int getIdByTimestamp(String uploadTimeVue);

    @Delete("DELETE FROM Images")
    void deleteImageAll();
}
