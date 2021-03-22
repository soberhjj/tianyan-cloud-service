package com.newland.tianyan.auth.service.impl;


import com.newland.tianyan.auth.dao.AccountMapper;
import com.newland.tianyan.auth.entity.Account;
import com.newland.tianyan.auth.service.IAccountService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
public class AccountServiceImpl implements UserDetailsService, IAccountService {

    private final AccountMapper accountMapper;
    private final PasswordEncoder encoder;

    public AccountServiceImpl(AccountMapper accountMapper, PasswordEncoder encoder) {
        this.accountMapper = accountMapper;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return accountMapper.findByAccount(s);
    }

    /**
     * 添加一个用户
     *
     * @param record 要添加的用户信息
     */
    @Override
    @Transactional
    public int insert(Account record) {
        return accountMapper.insertSelective(record);
    }

    /**
     * 重置密码
     *
     * @param mailbox  邮箱
     * @param password 新密码
     * @return 成功返回 true, 失败返回 false
     */
    @Override
    @Transactional
    public boolean resetPassword(String mailbox, String password) {
        return accountMapper.resetPassword(mailbox, password) != 0;
    }

    /**
     * 根据条件查询
     *
     * @param account
     * @return 查询到的结果
     */
    @Override
    public Account findOne(Account account) {
        return accountMapper.find(account);
    }

    /**
     * 查询指定列是否已经存在指定值,用于唯一性查询或检查
     *
     * @param column  要查的列
     * @param account 要查询的值
     * @return 存在返回 true, 不存在返回 false
     */
    @Override
    public boolean checkExits(String column, String account) {
        Example example = new Example(Account.class);
        example.createCriteria().andEqualTo(column, account);
        return accountMapper.selectByExample(example).size() != 0;
    }

}
