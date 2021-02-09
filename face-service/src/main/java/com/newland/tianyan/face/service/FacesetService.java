package com.newland.tianyan.face.service;


import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.face.domain.AppInfo;

/**
 * @Author: huangJunJie  2020-11-06 14:08
 */
public interface FacesetService {

    public PageInfo<AppInfo> getList(NLBackend.BackendAllRequest receive);

}
