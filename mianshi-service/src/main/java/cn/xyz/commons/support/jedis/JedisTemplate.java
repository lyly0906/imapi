package cn.xyz.commons.support.jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xyz.commons.utils.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Pool;

public class JedisTemplate {

	private static Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

	private JedisPool jedisPool;

	public JedisTemplate(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	protected void close(Jedis jedis, boolean broken) {
		if (jedis != null) {
			try {
				if (broken) {
					jedisPool.returnBrokenResource(jedis);
				} else {
					jedisPool.returnResource(jedis);
				}
			} catch (Exception e) {
				logger.error("Error happen when return jedis to pool, try to close it directly.", e);
				JedisUtils.closeJedis(jedis);
			}
		}
	}

	public Long expire(final String key, final int seconds) {
		if(StringUtil.isEmpty(key)||"null".equals(key))
			return 0L;
		return execute(new JedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.expire(key, seconds);
			}
		});
	}

	public Long ttl(final String key) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.ttl(key);
			}
		});
	}

	public Long decr(final String key) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.decr(key);
			}
		});
	}

	public Boolean del(final String... keys) {
		return execute(new JedisCallback<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.del(keys) == 1 ? true : false;
			}
		});
	}

	public Boolean keyExists(String key) {
		return execute(new JedisCallback<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.exists(key);
			}
		});
	}

	public <T> T execute(JedisCallback<T> callback) {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			return callback.execute(jedis);
		} catch (JedisConnectionException e) {
			broken = true;
			throw e;
		} finally {
			close(jedis, broken);
		}
	}

	public void execute(JedisCallbackVoid callback) {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			callback.execute(jedis);
		} catch (JedisConnectionException e) {
			broken = true;
			throw e;
		} finally {
			close(jedis, broken);
		}
	}

	public void flushDB() {
		execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				jedis.flushDB();
			}
		});
	}

	public String get(final String key) {
		return execute(new JedisCallback<String>() {

			@Override
			public String execute(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	public Integer getAsInt(final String key) {
		String result = get(key);
		return result != null ? Integer.valueOf(result) : null;
	}

	public Long getAsLong(final String key) {
		String result = get(key);
		return result != null ? Long.valueOf(result) : null;
	}

	public Pool<Jedis> getJedisPool() {
		return jedisPool;
	}

	public Long hdel(final String key, final String... fieldsName) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.hdel(key, fieldsName);
			}
		});
	}

	public String hget(final String key, final String field) {
		return execute(new JedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.hget(key, field);
			}
		});
	}

	public Set<String> hkeys(final String key) {
		return execute(new JedisCallback<Set<String>>() {
			@Override
			public Set<String> execute(Jedis jedis) {
				return jedis.hkeys(key);
			}
		});
	}

	public List<String> hmget(final String key, final String[] fields) {
		return execute(new JedisCallback<List<String>>() {
			@Override
			public List<String> execute(Jedis jedis) {
				return jedis.hmget(key, fields);
			}
		});
	}

	public void hmset(final String key, final Map<String, String> map) {
		execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				jedis.hmset(key, map);
			}
		});
	}

	public void hset(final String key, final String field, final String value) {
		execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				jedis.hset(key, field, value);
			}
		});
	}

	public Long incr(final String key) {
		return execute(new JedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.incr(key);
			}
		});
	}

	public Long llen(final String key) {
		return execute(new JedisCallback<Long>() {

			@Override
			public Long execute(Jedis jedis) {
				return jedis.llen(key);
			}
		});
	}

	public void lpush(final String key, final String... values) {
		execute(new JedisCallbackVoid() {
			@Override
			public void execute(Jedis jedis) {
				jedis.lpush(key, values);
			}
		});
	}

	public Boolean lremAll(final String key, final String value) {
		return execute(new JedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				Long count = jedis.lrem(key, 0, value);
				return (count > 0);
			}
		});
	}

	public Boolean lremOne(final String key, final String value) {
		return execute(new JedisCallback<Boolean>() {
			@Override
			public Boolean execute(Jedis jedis) {
				Long count = jedis.lrem(key, 1, value);
				return (count == 1);
			}
		});
	}

	public String rpop(final String key) {
		return execute(new JedisCallback<String>() {

			@Override
			public String execute(Jedis jedis) {
				return jedis.rpop(key);
			}
		});
	}

	public void set(final String key, final String value) {
		if(StringUtil.isEmpty(key)||"null".equals(key))
			return;
		execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				jedis.set(key, value);
			}
		});
	}

	public void setex(final String key, final String value, final int seconds) {
		if(StringUtil.isEmpty(key)||"null".equals(key))
			return;
		execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				jedis.setex(key, seconds, value);
			}
		});
	}

	public Boolean setnx(final String key, final String value) {
		return execute(new JedisCallback<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.setnx(key, value) == 1 ? true : false;
			}
		});
	}

	public Boolean setnxex(final String key, final String value, final int seconds) {
		return execute(new JedisCallback<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				String result = jedis.set(key, value, "NX", "EX", seconds);
				return JedisUtils.isStatusOk(result);
			}
		});
	}

	public Boolean zadd(final String key, final String member, final double score) {
		return execute(new JedisCallback<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.zadd(key, score, member) == 1 ? true : false;
			}
		});
	}

	public Long zcard(final String key) {
		return execute(new JedisCallback<Long>() {

			@Override
			public Long execute(Jedis jedis) {
				return jedis.zcard(key);
			}
		});
	}

	public Boolean zrem(final String key, final String member) {
		return execute(new JedisCallback<Boolean>() {

			@Override
			public Boolean execute(Jedis jedis) {
				return jedis.zrem(key, member) == 1 ? true : false;
			}
		});
	}

	public Double zscore(final String key, final String member) {
		return execute(new JedisCallback<Double>() {

			@Override
			public Double execute(Jedis jedis) {
				return jedis.zscore(key, member);
			}
		});
	}
}