package com.xjh.myblog.common.service;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// 封装redis的操作
public interface RedisService {

    /**
     * 根据传入的匹配格式获取redis中存在的key值并存入set集合
     * @return 返回符合条件的set集合
     */
    Set<String> getKeys(String pattern);

    /**
     * 判断一个key是否存在
     * @param key redis的key值
     * @return 存在则返回true，不存在则返回false
     */
    boolean hasKey(String key);

    /**
     * 保存一个键值-对
     */
    void set(String key,Object value);

    /**
     * 保存一个键值-对,设置过期时间
     */
    void set(String key, Object value, Duration expire);

    /**
     * 删除一个key
     * @param key 待删除的key
     * @return 是否删除成功
     */
    boolean del(String key);

    /**
     * 批量删除key值
     * @param keys 待删除的key值列表
     * @return 返回被删除的key的数量
     */
    long del(Collection<String> keys);

    /**
     * 根据key获取value的值
     * @param key redis的key值
     * @param tClass 返回对象的类型
     * @return 返回指定类型的变量
     */
    <T> T get(String key, Class<T> tClass);

    /**
     * redis的自增命令，数值类型的值自动+1，如果不是数值则会报错，如果该key不存在，则会初始化其值为0并+1
     * @param key redis的Key值
     * @return 返回自增后的值，如果为null，则代表出错
     */
    Long incr(String key);

    /**
     * 为对应的key值设置过期时间
     * @param key 对应的key值
     * @param timeout 过期时间
     * @param unit 过期时间的单位
     * @return 返回是否成功
     */
    boolean expire(String key, Long timeout, TimeUnit unit);

    /**
     * 获取一个key的过期时间，单位是秒
     */
    Long getExpire(String key);

    /**
     * 分布式锁
     * 如果对应的key值为空，则设置值，并返回true，不如key值存在则不进行操作（即redis中的setnx命令）
     * @param key 对应的key值
     * @param value 想要设置的值
     * @param timeout 过期时间
     * @param unit 过期时间的单位
     * @return 返回是否成功设置了值
     */
    boolean setnx(String key,Object value,Long timeout, TimeUnit unit);

    /**
     * 向一个HyperLogLog集合中添加一个基数
     * @param key 对应HyperLogLog集合的key值
     * @param value 添加的值
     * @return 当值被成功添加后，返回1，否则返回0。 在管道/事务中使用时为 null。
     */
    void hyperLogLogAdd(String key,Object value);

    /**
     * 获取一个HyperLogLog集合中的基础
     * @param key 对应的key值
     * @return 如果key不存在则返回0，其余情况正常返回
     */
    Long hyperLogLogCount(String key);
}
