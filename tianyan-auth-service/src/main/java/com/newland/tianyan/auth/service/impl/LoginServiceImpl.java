package com.newland.tianyan.auth.service.impl;

import com.newland.tianya.commons.base.utils.ProtobufUtils;
import com.newland.tianyan.auth.constant.ExceptionEnum;
import com.newland.tianyan.auth.entity.Account;
import com.newland.tianyan.auth.service.IAccountService;
import com.newland.tianyan.auth.service.ILoginService;
import com.newland.tianyan.common.utils.message.NLBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityExistsException;

@Service
public class LoginServiceImpl implements ILoginService {

    private final IAccountService accountService;
    private final PasswordEncoder encoder;

    @Autowired
    public LoginServiceImpl(IAccountService accountService, PasswordEncoder encoder) {
        this.accountService = accountService;
        this.encoder = encoder;
    }

    @Override
    public boolean checkUnique(NLBackend.BackendAllRequest request) {
        Account condition = ProtobufUtils.parseTo(request, Account.class);

        Account account = accountService.findOne(condition);

        if (account == null) {
            // 如果不存在,正确返回
            return true;
        }

        throw new EntityExistsException("exist!");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void register(NLBackend.BackendAllRequest receive) {
        Account account = ProtobufUtils.parseTo(receive, Account.class);
        // check account exist
        if (accountService.checkExits("account", account.getAccount())) {
            throw ExceptionEnum.ACCOUNT_NOT_FOUND.toException(account.getAccount());
        }
        // check mailbox exist
        if (accountService.checkExits("mailbox", account.getMailbox())) {
            throw ExceptionEnum.MAIL_BOX_NOT_FOUND.toException(account.getMailbox());
        }
        account.setPassword(encoder.encode(account.getPassword()));
        // register account
        accountService.insert(account);
    }

    @Override
    public void restPassword(NLBackend.BackendAllRequest request) {
        Account account = ProtobufUtils.parseTo(request, Account.class);
        //check mailbox
        if (accountService.checkExits("mailbox", account.getMailbox())) {
            accountService.resetPassword(account.getMailbox(), encoder.encode(account.getPassword()));
        } else {
            throw ExceptionEnum.MAIL_BOX_NOT_FOUND.toException(account.getMailbox());
        }
    }

    @Override
    public Account getInfo(NLBackend.BackendAllRequest receive) {
        Account account = ProtobufUtils.parseTo(receive, Account.class);
        // check account exist
        if (!StringUtils.isEmpty(account.getAccount())) {
            account = accountService.findOne(account);
        } else {
            // check mailbox exist
            if (!StringUtils.isEmpty(account.getMailbox())) {
                account = accountService.findOne(account);
            }
        }
        if (account == null) {
            throw ExceptionEnum.ACCOUNT_NOT_FOUND.toException(receive.getAccount());
        }
        return account;
    }

}
