package com.newland.tianyan.face.dao;


import com.newland.tianyan.face.domain.entity.FaceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: huangJunJie  2020-11-04 10:54
 */
@Mapper
@Component
public interface FaceMapper extends CommonMapper<FaceDO> {

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
    void insertBatch(List<FaceDO> list);
}
