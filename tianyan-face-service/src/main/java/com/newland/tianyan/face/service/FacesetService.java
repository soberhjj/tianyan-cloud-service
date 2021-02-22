package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.entity.AppInfoDO;

/**
 * @Author: huangJunJie  2020-11-06 14:08
 */
public interface FacesetService {

    public PageInfo<AppInfoDO> getList(NLBackend.BackendAllRequest receive);

}
