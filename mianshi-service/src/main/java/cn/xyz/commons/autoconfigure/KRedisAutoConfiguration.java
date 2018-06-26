package cn.xyz.commons.autoconfigure;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import cn.xyz.commons.autoconfigure.KApplicationProperties.RedisConfig;
import cn.xyz.commons.support.jedis.JedisTemplate;

@Configuration
public class KRedisAutoConfiguration {

	@Resource
	private RedisConfig properties;

	@Bean
	public JedisPoolConfig jdeisPoolConfig() {
		JedisPoolConfig config=new JedisPoolConfig();
			config.setMaxWaitMillis(3000000); 
			config.setMaxTotal(300);
			config.setMaxIdle(10); 
		  config.setTestOnBorrow(true); 
		return config;
	}

	@Bean
	public JedisPool jedisPool() {
		//return new JedisPool(jdeisPoolConfig(), properties.getHost(), properties.getPort());
		return new JedisPool(jdeisPoolConfig(), properties.getHost(), properties.getPort(),5000, null,properties.getDatabase(), null);
	}

	@Bean
	public JedisTemplate jedisTemplate() {
		return new JedisTemplate(jedisPool());
	}

}
