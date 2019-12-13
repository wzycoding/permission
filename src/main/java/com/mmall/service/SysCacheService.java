package com.mmall.service;

import com.google.common.base.Joiner;
import com.mmall.beans.CacheKeyConstants;
import com.mmall.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

/**
 * 系统缓存服务类
 * @author wzy
 * @version 1.0
 * @date 2019/12/13 15:57
 */
@Service
@Slf4j
public class SysCacheService {
    @Resource(name = "redisPool")
    private RedisPool redisPool;

    public void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix) {
        saveCache(toSavedValue, timeoutSeconds, prefix);
    }

    /**
     * 保存值到缓存
     * @param toSavedValue
     * @param timeoutSeconds
     * @param prefix
     * @param keys
     */
    public void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix, String... keys) {
        if (toSavedValue == null) {
            return;
        }
        ShardedJedis shardedJedis = null;
        try {
            String key = generateCachekey(CacheKeyConstants.USER_ACLS, keys);
            shardedJedis = redisPool.instance();
            shardedJedis.setex(key, timeoutSeconds, toSavedValue);
        } catch (Exception e) {
            log.error("save cache exception, prefix: {}, keys:{}", prefix.name(), JsonMapper.obj2String(keys));
        } finally {
            redisPool.safeClose(shardedJedis);
        }
    }

    /**
     * 根据key获取缓存值
     * @param prefix
     * @param keys
     * @return
     */
    public String getFromCache(CacheKeyConstants prefix, String... keys) {
        ShardedJedis shardedJedis = null;
        String cacheKey = generateCachekey(prefix, keys);
        try {
            shardedJedis = redisPool.instance();
            String value = shardedJedis.get(cacheKey);
            return value;
        } catch (Exception e) {
            log.error("get from cache exception, prefix: {}, keys: {}",  prefix.name(), JsonMapper.obj2String(keys));
            return null;
        } finally {
            redisPool.safeClose(shardedJedis);
        }
    }

    /**
     * 生成redis key
     * @param prefix
     * @param keys
     */
    public String generateCachekey(CacheKeyConstants prefix, String... keys) {
        String key = prefix.name();
        if (StringUtils.isNotBlank(key)) {
            key += "_" + Joiner.on("_").join(keys);
        }
        return key;
    }
}
