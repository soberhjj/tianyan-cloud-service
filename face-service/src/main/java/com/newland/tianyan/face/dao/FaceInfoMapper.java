package com.newland.tianyan.face.dao;

import com.newland.tianyan.face.domain.FaceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @Author: huangJunJie  2020-11-04 10:54
 */
@Mapper
@Component
public interface FaceInfoMapper extends CommonMapper<FaceInfo> {

    List<FaceInfo> selectByGroupId(HashMap<String, Object> map);

    /**
     * 插入face并且face.id被赋予自增Id
     */
    void insertGetId(FaceInfo face);

    /**
     * @param userId 用户标识号
     * @return face主键Id
     */
    List<Long> selectIdByUserId(@Param("userId") String userId);

    /**
     * @param groupId 用户标识号
     * @return face主键Id
     */
    List<Long> selectIdByGroupId(@Param("groupId") String groupId);

    /**
     * 批量插入
     */
    void insertBatch(@Param("faceList") List<FaceInfo> faceList);
}
