package com.newland.tianyan.face.service.cache;

import com.newland.tianyan.common.exception.BaseException;

import java.util.List;

/**
 * @author: RojiaHuang
 * @description: 统一缓存维护入口
 * @date: 2021/1/12
 */
public interface ICacheHelper<T> {

    /**
     * 删除目标
     *
     * @param id 缓存id
     */
    void delete(Long collectionId,Long id) throws BaseException;

    /**
     * 批量删除目标
     *
     * @param idList 缓存idList
     */
    void deleteBatch(Long collectionId,List<Long> idList) throws BaseException;

    /**
     * 向缓存增加目标
     *
     * @param entity 插入目标（可以主动指定ID）
     * @return 缓存的主键ID/KEY值
     */
    Long add(T entity) throws BaseException;

    /**
     * 批量添加，增加至批量池中
     *
     * @param entityList 插入目标（可以主动指定ID)
     * @return 缓存的主键ID/KEY值
     */
    List<Long> addBatch(List<T> entityList) throws BaseException;

    void createCollection(Long collectionId) throws BaseException;

    void deleteCollection(Long collectionId) throws BaseException;
}
