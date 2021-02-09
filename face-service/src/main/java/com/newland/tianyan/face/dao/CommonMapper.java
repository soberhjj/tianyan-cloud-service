package com.newland.tianyan.face.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface CommonMapper<T> extends Mapper<T>, MySqlMapper<T> {

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @return 查询到的数量
     */
    @Select("SELECT COUNT(*) FROM information_schema.TABLES WHERE table_name = #{tableName}")
    int isTableExits(@Param("tableName") String tableName);

    /**
     * 判断指定表的指定指定字段是否已经存在某个值
     *
     * @param table  表名
     * @param column 列名
     * @param value  值
     * @return 存在返回 true, 不存在返回 false
     */
    @Select("SELECT COUNT(*) FROM ${table} WHERE ${column} = #{value}")
    Integer checkExits(@Param("table") String table, @Param("column") String column, @Param("value") Object value);

    @Select("SELECT COUNT(*) FROM ${table} WHERE ${column1} = #{value1} AND ${column2} = #{value2}")
    Integer checkExitsByTwoColumn(@Param("table") String table, @Param("column1") String column, @Param("value1") Object value, @Param("column2") String column2, @Param("value2") Object value2);

    @Select("SELECT COUNT(*) FROM ${table} WHERE ${column1} = #{value1} AND ${column2} = #{value2} AND ${column3} = #{value3}")
    Integer checkExitsByThreeColumn(@Param("table") String table, @Param("column1") String column, @Param("value1") Object value, @Param("column2") String column2, @Param("value2") Object value2, @Param("column3") String column3, @Param("value3") Object value3);

    /**
     * 删除指定表
     *
     * @param table 表名
     */
    @Delete("DROP TABLE IF EXISTS ${table}")
    void dropTable(@Param("table") String table);
}
