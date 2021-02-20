package com.newland.tianyan.auth.dao;



import com.newland.tianyan.auth.entity.Account;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@org.apache.ibatis.annotations.Mapper
@Component
public interface AccountMapper extends Mapper<Account> {

    /**
     * 重置密码
     */
    @Update("UPDATE cloud_management_account SET PASSWORD = #{password} WHERE mailbox = #{mailbox}")
    int resetPassword(@Param("mailbox") String mailbox, @Param("password") String password);

    /**
     * 查询
     */
    Account find(@Param("account") Account account);

    /**
     * 查询
     */
    Account findByAccount(@Param("account") String account);
}
