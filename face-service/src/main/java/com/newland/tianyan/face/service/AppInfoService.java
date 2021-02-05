package com.newland.tianyan.face.service;

import com.github.pagehelper.PageInfo;
import com.newland.tianyan.face.domain.AppInfo;
import com.newland.tianyan.face.dto.app.*;


/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/2/4
 */
public interface AppInfoService {

    void insert(BackendAppCreateRequest receive);

    void delete(BackendAppDeleteRequest receive);

    void update(BackendAppUpdateRequest receive);

    AppInfo getInfo(BackendAppGetInfoRequest receive);

    PageInfo<AppInfo> getList(BackendAppGetListRequest receive);
}
