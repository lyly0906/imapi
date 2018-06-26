package cn.xyz.mianshi.utils;

import java.util.Map;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.spring.SpringBeansUtils;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.example.KSession;
import cn.xyz.mianshi.vo.Config;
import cn.xyz.mianshi.vo.User;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public final class KSessionUtil {

	/**
	 * 根据用户Id获取access_token
	 */
	public static final String GET_ACCESS_TOKEN_BY_USER_ID = "login:%s:access_token";

	/**
	 * 根据access_token获取Session
	 */
	public static final String GET_SESSION_BY_ACCESS_TOKEN = "login:%s:session";
	
	public static final String GET_USER_BY_USERID = "user:%s:data";
	public static final String GET_USERID_BY_TELEPHONE = "user:userIds:%s";
	public static final String GET_CONFIG = "app:config";

	private static cn.xyz.commons.support.jedis.JedisTemplate jedisTemplate;

	static {
		jedisTemplate = SpringBeansUtils.getBean("jedisTemplate");
	}
	public static void setConfig(Config config) {
		jedisTemplate.set(GET_CONFIG, config.toString());
	}
	public static Config getConfig() {
		String config=jedisTemplate.get(GET_CONFIG);
		return StringUtil.isEmpty(config) ? null : JSON.parseObject(config, Config.class);
	}
	public static Map<String, Object> getAccessToken(String telephone,  long userId,String language) {
		String key = String.format(GET_ACCESS_TOKEN_BY_USER_ID, userId);
		String access_token = jedisTemplate.get(key);
		
		long expires_in = jedisTemplate.ttl(key);

		if (StringUtil.isEmpty(access_token)) {
			access_token = StringUtil.randomUUID();
			//expires_in = KConstants.Expire.DAY7;
			jedisTemplate.set(key, access_token);
			// 重新生成访问令牌
		}
		/*else {
			jedisTemplate.expire(key,  KConstants.Expire.DAY7);
			String session_key=String.format(GET_SESSION_BY_ACCESS_TOKEN, access_token);
			String session=jedisTemplate.get(session_key);
			if(StringUtil.isEmpty(session)){
				jedisTemplate.expire(session_key,  KConstants.Expire.DAY7);
			}else
			jedisTemplate.expire(session_key,  KConstants.Expire.DAY7);
		}*/
		//
		Map<String, Object> data = Maps.newHashMap();
		data.put("access_token", access_token);
		data.put("expires_in", expires_in);
		return data;
	}
	public static String setAccessTokenByCode(String access_token, String code) {
		jedisTemplate.set(code, access_token);
		/*jedisTemplate.expire(code, 3000);*/
		return "";
	}
	public static String getAccessTokenByCode(String code) {
		return jedisTemplate.get(code);
	}

	public static void setAccessToken(String access_token, KSession kSession) {
		jedisTemplate.execute(new JedisCallbackVoid() {
			
			@Override
			public void execute(Jedis jedis) {
				Pipeline pipe = jedis.pipelined();

				String key = String.format(GET_SESSION_BY_ACCESS_TOKEN, access_token);
				String value = kSession.toString();
				pipe.set(key, value);
				//pipe.expire(key, KConstants.Expire.DAY7);

				key = String.format(GET_ACCESS_TOKEN_BY_USER_ID, kSession.getUserId());
				value = access_token;
				pipe.set(key, value);
				//pipe.expire(key, KConstants.Expire.DAY7);

				pipe.sync();
			}
		});
	}


	public static String getAccess_token(long userId){
		String key = String.format(GET_ACCESS_TOKEN_BY_USER_ID,userId);
		return jedisTemplate.get(key);
	}
	
	
	/**
	 * User
	 * @param userId
	 * @return
	 */
	public static User getUserByUserId(Integer userId) {
		String key = String.format(GET_USER_BY_USERID, userId);
		String value = jedisTemplate.get(key);
		return StringUtil.isEmpty(value) ? null : JSON.parseObject(value, User.class);
	}
	public static void saveUserByUserId(Integer userId,User user) {
		String key = String.format(GET_USER_BY_USERID, userId);
		jedisTemplate.set(key, user.toString());
		jedisTemplate.expire(key, KConstants.Expire.DAY1);
	}
	public static void deleteUserByUserId(Integer userId) {
		String key = String.format(GET_USER_BY_USERID, userId);
		jedisTemplate.del(key);
	}
	
	/**
	 * UserId
	 * @param telephone
	 * @return
	 */
	public static Integer getUserIdByTelephone(String telephone) {
		String key = String.format(GET_USERID_BY_TELEPHONE, telephone);
		String value = jedisTemplate.get(key);
		return StringUtil.isEmpty(value) ? null : JSON.parseObject(value, Integer.class);
	}
	public static void saveUserIdByTelephone(String telephone,Integer userId) {
		String key = String.format(GET_USERID_BY_TELEPHONE, String.valueOf(userId));
		jedisTemplate.set(key, userId.toString());
		jedisTemplate.expire(key, KConstants.Expire.DAY1);
	}
	public static void deleteUserIdByTelephone(String telephone) {
		String key = String.format(GET_USERID_BY_TELEPHONE, telephone);
		jedisTemplate.del(key);
	}
}
