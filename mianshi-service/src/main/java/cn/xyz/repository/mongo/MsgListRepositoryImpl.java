package cn.xyz.repository.mongo;

import java.util.Set;

import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import cn.xyz.commons.constants.KKeyConstant;
import cn.xyz.commons.support.jedis.JedisCallback;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.repository.MongoRepository;
import cn.xyz.repository.MsgListRepository;

@Service
public class MsgListRepositoryImpl extends MongoRepository implements MsgListRepository {

	@Override
	public void addToHotList(int cityId, int userId, String messageId, double score) {
		String hmilKey = String.format(KKeyConstant.HotMsgIdListTemplate, cityId);
		String uhmiKey = String.format(KKeyConstant.UserHotMsgIdTemplate, cityId);

		JedisCallbackVoid callback = jedis -> {
			// 消息不存在
			if (null == jedis.zrank(hmilKey, messageId)) {
				String userHotMsgId = jedis.hget(uhmiKey, String.valueOf(userId));
				userHotMsgId = null == userHotMsgId ? "" : userHotMsgId;
				//
				if (null == jedis.zrank(hmilKey, userHotMsgId)) {
					long length = jedis.zcard(hmilKey);
					if (length >= 200) {
						Set<String> members = jedis.zrangeByScore(hmilKey, 0, score);
						if (members.size() > 0) {
							Pipeline pipeline = jedis.pipelined();
							pipeline.zrem(hmilKey, members.toArray()[0].toString());
							pipeline.zadd(hmilKey, score, messageId);
							pipeline.hset(uhmiKey, String.valueOf(userId), messageId);
							pipeline.sync();
						}
					} else {
						Pipeline pipeline = jedis.pipelined();
						pipeline.zadd(hmilKey, score, messageId);
						pipeline.hset(uhmiKey, String.valueOf(userId), messageId);
						pipeline.sync();
					}
				} else {
					double hScore = jedis.zscore(hmilKey, userHotMsgId);
					if (score >= hScore) {
						Pipeline pipeline = jedis.pipelined();
						pipeline.zrem(hmilKey, userHotMsgId);
						pipeline.zadd(hmilKey, score, messageId);
						pipeline.hset(uhmiKey, String.valueOf(userId), messageId);
						pipeline.sync();
					}
				}
			} else {
				jedis.zadd(hmilKey, score, messageId);
			}
		};
		jedisTemplate.execute(callback);

	}

	@Override
	public void addToLatestList(int cityId, int userId, String messageId) {
		String newMsgId = messageId;
		String latestMsgId = getLatestId(cityId, userId);

		String lmilKey = String.format(KKeyConstant.LatestMsgIdListTemplate, cityId);
		String ulmiKey = String.format(KKeyConstant.UserLatestMsgIdTemplate, cityId);

		JedisCallbackVoid callback = jedis -> {
			long count = jedis.lrem(lmilKey, 0, (null == latestMsgId ? "" : latestMsgId));
			// 未发表商务圈或最后一条商务圈不在最新人才榜
			if (0 == count) {
				long length = jedis.llen(lmilKey);
				if (length >= 200) {
					jedis.rpop(lmilKey);
				}
			}
			Pipeline pipeline = jedis.pipelined();
			pipeline.lpush(lmilKey, newMsgId);
			pipeline.hset(ulmiKey, String.valueOf(userId), newMsgId);

			pipeline.sync();
		};
		jedisTemplate.execute(callback);
	}

	@Override
	public String getHotId(int cityId, Object userId) {
		return null;
	}

	@Override
	public Object getHotList(int cityId, int pageIndex, int pageSize) {
		String key = String.format(KKeyConstant.HotMsgListTemplate, cityId);
		return com.mongodb.util.JSON.parse(jedisTemplate.execute(new JedisCallback<String>() {

			@Override
			public String execute(Jedis jedis) {
				return jedis.hget(key, String.valueOf(pageIndex));
			}
		}));
	}

	@Override
	public String getLatestId(int cityId, Object userId) {
		String key = String.format(KKeyConstant.UserLatestMsgIdTemplate, cityId);
		JedisCallback<String> callback = jedis -> {
			return jedis.hget(key, String.valueOf(userId));
		};
		return jedisTemplate.execute(callback);
	}

	@Override
	public Object getLatestList(int cityId, int pageIndex, int pageSize) {
		String key = String.format(KKeyConstant.LatestMsgListTemplate, cityId);
		return com.mongodb.util.JSON.parse(jedisTemplate.execute(new JedisCallback<String>() {

			@Override
			public String execute(Jedis jedis) {
				return jedis.hget(key, String.valueOf(pageIndex));
			}
		}));
	}

}
