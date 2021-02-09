package com.newland.tianyan.face.cache;

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
     * @return 大于0 影响结果多于0个，判定成功
     */
    Integer delete(Long collectionId,Long id);

    /**
     * 批量删除目标
     *
     * @param idList 缓存idList
     * @return 大于0 影响结果多于0个，判定成功
     */
    Integer deleteBatch(Long collectionId,List<Long> idList);

    /**
     * 向缓存增加目标
     *
     * @param entity 插入目标（可以主动指定ID）
     * @return 缓存的主键ID/KEY值
     */
    Long add(T entity);

    /**
     * 批量添加，增加至批量池中
     *
     * @param entityList 插入目标（可以主动指定ID)
     * @return 缓存的主键ID/KEY值
     */
    List<Long> addBatch(List<T> entityList);

    Integer createCollection(Long collectionId);

    Integer deleteCollection(Long collectionId);
}
