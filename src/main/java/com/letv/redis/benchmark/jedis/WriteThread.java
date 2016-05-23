package com.letv.redis.benchmark.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import com.letv.redis.benchmark.common.StringGenerator;

public class WriteThread extends Thread {

    protected JedisPool pool;
    protected JedisCluster jedisCluster;
    protected CyclicBarrier barrier;
    private Map<String, Long> costMapPerThread;

    public WriteThread(JedisPool pool, CyclicBarrier barrier) {
        this.pool = pool;
        this.barrier = barrier;
    }

    public WriteThread(JedisCluster jedisCluster, CyclicBarrier barrier) {
        this.jedisCluster = jedisCluster;
        this.barrier = barrier;
    }

    public void run() {

        try {
            barrier.await();

            if (Cli.enableCluster) {
                this.setCostMapPerThread(setKey(jedisCluster, Cli.repeatCount, Cli.key_bytes, Cli.value_bytes));
            } else {
                this.setCostMapPerThread(setKey(pool, Cli.repeatCount, Cli.key_bytes, Cli.value_bytes));
            }

            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Long> setKey(JedisPool pool,
                                     int repeats, int key_bytes, int value_bytes) {

        Jedis jedis = pool.getResource();

        long avgSetCostPerThread = 0;
        long maxSetCostPerThread = Long.MIN_VALUE;
        long minSetCostPerThread = Long.MAX_VALUE;
        long sumSetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();

        for (int i = 1; i <= repeats; i++) {
           // String key = "redis-check-noc-" + i;
            String key = StringGenerator.RandomString(key_bytes);
        	long startTime = System.nanoTime();
            String value =StringGenerator.RandomString(value_bytes);
            jedis.set(key,value);
            long estimatedTime = System.nanoTime() - startTime;

            sumSetCostPerThread = sumSetCostPerThread + estimatedTime;
            avgSetCostPerThread = sumSetCostPerThread / i;
            maxSetCostPerThread = maxSetCostPerThread > estimatedTime ? maxSetCostPerThread
                    : estimatedTime;
            minSetCostPerThread = minSetCostPerThread < estimatedTime ? minSetCostPerThread
                    : estimatedTime;

        }

        if (jedis != null) {
            pool.returnResource(jedis);
        }

        costMapPerThread.put("avgSetCostPerThread", avgSetCostPerThread);
        costMapPerThread.put("maxSetCostPerThread", maxSetCostPerThread);
        costMapPerThread.put("minSetCostPerThread", minSetCostPerThread);

        return costMapPerThread;
    }

    private Map<String, Long> setKey(JedisCluster jedisCluster,
    								int repeats, int key_bytes, int value_bytes) {

        long avgSetCostPerThread = 0;
        long maxSetCostPerThread = Long.MIN_VALUE;
        long minSetCostPerThread = Long.MAX_VALUE;
        long sumSetCostPerThread = 0;
        Map<String, Long> costMapPerThread = new HashMap<>();

        for (int i = 1; i <= repeats; i++) {
        	String key = StringGenerator.RandomString(key_bytes);
          	long startTime = System.nanoTime();
            String value =StringGenerator.RandomString(value_bytes);
            jedisCluster.set(key,value);
            long estimatedTime = System.nanoTime() - startTime;

            sumSetCostPerThread = sumSetCostPerThread + estimatedTime;
            avgSetCostPerThread = sumSetCostPerThread / i;
            maxSetCostPerThread = maxSetCostPerThread > estimatedTime ? maxSetCostPerThread
                    : estimatedTime;
            minSetCostPerThread = minSetCostPerThread < estimatedTime ? minSetCostPerThread
                    : estimatedTime;

        }

        costMapPerThread.put("avgSetCostPerThread", avgSetCostPerThread);
        costMapPerThread.put("maxSetCostPerThread", maxSetCostPerThread);
        costMapPerThread.put("minSetCostPerThread", minSetCostPerThread);

        return costMapPerThread;
    }

    public Map<String, Long> getCostMapPerThread() {
        return costMapPerThread;
    }

    public void setCostMapPerThread(Map<String, Long> costMapPerThread) {
        this.costMapPerThread = costMapPerThread;
    }

}
