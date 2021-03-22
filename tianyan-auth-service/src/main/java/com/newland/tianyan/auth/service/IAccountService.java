package com.newland.tianyan.auth.service;

import com.newland.tianyan.auth.entity.Account;

/**
 * @author: RojiaHuang
 * @description:
 * @date: 2021/3/18
 */
public interface IAccountService {
    int insert(Account record);

    boolean resetPassword(String mailbox, String password);

    Account findOne(Account account);

    boolean checkExits(String column, String account);
}
