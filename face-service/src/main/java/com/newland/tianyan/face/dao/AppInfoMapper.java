package com.newland.tianyan.face.dao;

import com.newland.tianyan.face.domain.AppInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface AppInfoMapper extends CommonMapper<AppInfo> {

    int update(@Param("appInfo") AppInfo appInfo);

    int updateToDelete(@Param("isDelete") Byte isDelete, @Param("id") Long id);

    int groupNumberIncrease(@Param("appId") Long appId, @Param("count") int count);

}
