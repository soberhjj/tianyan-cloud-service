package com.newland.tianyan.face.dao;


import com.newland.tianyan.face.domain.entity.AppInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


/**
 * @description: dao接口类
 **/
@Mapper
@Component
public interface AppInfoMapper extends CommonMapper<AppInfoDO> {

    int update(@Param("appInfo") AppInfoDO appInfoDO);

    int updateToDelete(@Param("isDelete") Byte isDelete, @Param("id") Long id);

    int groupNumberIncrease(@Param("appId") Long appId, @Param("count") int count);
}
