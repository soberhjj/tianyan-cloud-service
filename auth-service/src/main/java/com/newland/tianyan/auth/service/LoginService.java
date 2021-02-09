package com.newland.tianyan.auth.service;

import com.newland.tianyan.auth.entity.Account;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.utils.ProtobufUtils;
import com.newland.tianyan.common.utils.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Service
public class LoginService {

    private final AccountService accountService;
    private final AppInfoService appInfoService;
    private final PasswordEncoder encoder;

    @Autowired
    public LoginService(AccountService accountService, AppInfoService appInfoService, PasswordEncoder encoder) {
        this.accountService = accountService;
        this.appInfoService = appInfoService;
        this.encoder=encoder;
    }

    public boolean checkUnique(NLBackend.BackendAllRequest request) {
        Account condition = ProtobufUtils.parseTo(request, Account.class);

        Account account = accountService.findOne(condition);

        if (account == null) {
            // 如果不存在,正确返回
            return true;
        }
        /* bug exist
        if (!request.getMailbox().isEmpty()) {
            // 若参数存在 mailbox,说明是mailbox存在
            throw new EntityExistsException("mailbox exist!");
        } else {
            // 否则,是account存在
            throw new EntityExistsException("account exist!");
        }*/
        throw new EntityExistsException("exist!");
    }

    @Transactional
    public void register(NLBackend.BackendAllRequest receive) {
        Account account = ProtobufUtils.parseTo(receive, Account.class);
        // check account exist
        if (accountService.checkExits("account", account.getAccount())) {
            throw new EntityExistsException("account exist!");
        }
        // check mailbox exist
        if (accountService.checkExits("mailbox", account.getMailbox())) {
            throw new EntityExistsException("mailbox exist!");
        }
        account.setPassword(encoder.encode(account.getPassword()));
        // register user
        if (accountService.insert(account) != 0) {
            appInfoService.createTable(TableUtils.generateAppTableName(account.getAccount()));
        }
    }

    public void restPassword(NLBackend.BackendAllRequest request) {
        Account account = ProtobufUtils.parseTo(request, Account.class);
        //check mailbox
        if (accountService.checkExits("mailbox", account.getMailbox())) {
            accountService.ResetPassword(account.getMailbox(), encoder.encode(account.getPassword()));
        } else {
            throw new EntityExistsException("mailbox doesn't exist!");
        }
    }

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
            throw new EntityNotFoundException("user doesn't exist!");
        }
        return account;
    }

}
