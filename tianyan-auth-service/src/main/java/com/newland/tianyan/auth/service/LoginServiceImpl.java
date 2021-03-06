package com.newland.tianyan.auth.service;

import com.newland.tianyan.auth.entity.Account;
import com.newland.tianyan.auth.constant.BusinessErrorEnums;
import com.newland.tianyan.auth.constant.SystemErrorEnums;
import com.newland.tianyan.common.utils.JsonUtils;
import com.newland.tianyan.common.utils.message.NLBackend;
import com.newland.tianyan.common.utils.ProtobufUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityExistsException;

@Service
public class LoginServiceImpl {

    private final AccountServiceImpl accountServiceImpl;
    private final PasswordEncoder encoder;

    @Autowired
    public LoginServiceImpl(AccountServiceImpl accountServiceImpl, PasswordEncoder encoder) {
        this.accountServiceImpl = accountServiceImpl;
        this.encoder=encoder;
    }

    public boolean checkUnique(NLBackend.BackendAllRequest request) {
        Account condition = ProtobufUtils.parseTo(request, Account.class);

        Account account = accountServiceImpl.findOne(condition);

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
        if (accountServiceImpl.checkExits("account", account.getAccount())) {
            throw BusinessErrorEnums.ACCOUNT_NOT_FOUND.toException(account.getAccount());
        }
        // check mailbox exist
        if (accountServiceImpl.checkExits("mailbox", account.getMailbox())) {
            throw BusinessErrorEnums.MAIL_BOX_NOT_FOUND.toException(account.getMailbox());
        }
        account.setPassword(encoder.encode(account.getPassword()));
        // register account
        if (accountServiceImpl.insert(account) == 0) {
           throw SystemErrorEnums.DB_INSERT_ERROR.toException(JsonUtils.toJson(account));
        }
    }

    public void restPassword(NLBackend.BackendAllRequest request) {
        Account account = ProtobufUtils.parseTo(request, Account.class);
        //check mailbox
        if (accountServiceImpl.checkExits("mailbox", account.getMailbox())) {
            accountServiceImpl.resetPassword(account.getMailbox(), encoder.encode(account.getPassword()));
        } else {
            throw BusinessErrorEnums.MAIL_BOX_NOT_FOUND.toException(request.getMailbox());
        }
    }

    public Account getInfo(NLBackend.BackendAllRequest receive) {
        Account account = ProtobufUtils.parseTo(receive, Account.class);
        // check account exist
        if (!StringUtils.isEmpty(account.getAccount())) {
            account = accountServiceImpl.findOne(account);
        } else {
            // check mailbox exist
            if (!StringUtils.isEmpty(account.getMailbox())) {
                account = accountServiceImpl.findOne(account);
            }
        }
        if (account == null) {
            throw BusinessErrorEnums.ACCOUNT_NOT_FOUND.toException(receive.getAccount());
        }
        return account;
    }

}
