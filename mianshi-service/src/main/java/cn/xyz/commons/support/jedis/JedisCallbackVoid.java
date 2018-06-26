package cn.xyz.commons.support.jedis;

import redis.clients.jedis.Jedis;

public interface JedisCallbackVoid {
	void execute(Jedis jedis);
}
