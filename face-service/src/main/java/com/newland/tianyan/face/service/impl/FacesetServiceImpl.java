package com.newland.tianyan.face.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.face.constant.StatusConstants;
import com.newland.tianyan.face.dao.AppInfoMapper;
import com.newland.tianyan.face.entity.AppInfo;
import com.newland.tianyan.face.service.FacesetService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @Author: huangJunJie  2020-11-06 14:10
 */
@Service
public class FacesetServiceImpl implements FacesetService {

    @Autowired
    private AppInfoMapper appInfoMapper;

    @Override
    public PageInfo<AppInfo> getList(NLBackend.BackendAllRequest receive) {
        AppInfo query = ProtobufUtils.parseTo(receive, AppInfo.class);
        return PageHelper.offsetPage(query.getStartIndex(), query.getLength())
                .doSelectPageInfo(
                        () -> {
                            Example example = new Example(AppInfo.class);
                            Example.Criteria criteria = example.createCriteria();

                            criteria.andEqualTo("account", query.getAccount());
                            criteria.andEqualTo("isDelete", StatusConstants.NOT_DELETE);
                            // dynamic sql
                            if (StringUtils.isNotBlank(query.getAppName())) {
                                criteria.andLike("appName", "%" + query.getAppName() + "%");
                            }
                            // execute select
                            appInfoMapper.selectByExample(example);
                        }
                );
    }
}
