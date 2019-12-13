package com.mmall.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

/**
 * 获取redis连接
 * @author wzy
 * @version 1.0
 * @date 2019/12/13 15:48
 */
@Service("redisPool")
@Slf4j
public class RedisPool {
    @Resource(name = "shardedJedisPool")
    private ShardedJedisPool shardedJedisPool;

    /**
     * 获取redis连接实例
     * @return
     */
    public ShardedJedis instance() {
        return shardedJedisPool.getResource();
    }

    /**
     * 安全关闭redis连接
     * @param shardedJedis redis连接
     */
    public void safeClose(ShardedJedis shardedJedis) {
        try {
            if (shardedJedis != null) {
                shardedJedis.close();
            }
        }catch (Exception e) {
            log.error("return redis resource exception", e);
        }
    }
}
