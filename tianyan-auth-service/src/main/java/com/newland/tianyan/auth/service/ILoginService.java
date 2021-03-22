package com.newland.tianyan.auth.service;

import com.newland.tianya.commons.base.model.proto.NLBackend;
import com.newland.tianyan.auth.entity.Account;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/18
 */
public interface ILoginService {

    boolean checkUnique(NLBackend.BackendAllRequest request);

    void register(NLBackend.BackendAllRequest receive);

    void restPassword(NLBackend.BackendAllRequest request);

    Account getInfo(NLBackend.BackendAllRequest receive);
}
