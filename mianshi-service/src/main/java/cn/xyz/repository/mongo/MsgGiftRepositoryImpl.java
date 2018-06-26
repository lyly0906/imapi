package cn.xyz.repository.mongo;

import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.jedis.JedisCallback;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.example.AddGiftParam;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Givegift;
import cn.xyz.mianshi.vo.Msg;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.MsgGiftRepository;
import cn.xyz.repository.MsgRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Service
public class MsgGiftRepositoryImpl implements MsgGiftRepository {
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Autowired
	private UserManager userService;
	
	@Autowired
	private MsgRepository circleService;

	@Override
	public List<ObjectId> add(Integer userId, ObjectId msgId,
			List<AddGiftParam> paramList) {
		User user = userService.getUser(userId);

		List<ObjectId> giftIdList = Lists.newArrayList();
		List<Givegift> entities = Lists.newArrayList();
		int activeValue = 0;

		for (AddGiftParam param : paramList) {
			Double price =1.0;
			/* goodsService.getGiftGoods(param.getGoodsId())
					.getPrice();*/
			activeValue += price * param.getCount();

			Givegift gift = new Givegift(param.getCount(),ObjectId.get(), param.getGoodsId(), msgId,
					user.getNickname(),price,DateUtil.currentTimeSeconds(), user.getUserId());

			giftIdList.add(gift.getGiftId());
			entities.add(gift);
		}

		// 缓存礼物
		jedisTemplate.execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				String key = String.format("msg:%1$s:gift", msgId.toString());
				Pipeline pipe = jedis.pipelined();

				for (Givegift gift : entities) {
					String string = gift.toString();
					pipe.lpush(key, string);
				}
				pipe.ltrim(key, 0, 500);
				pipe.expire(key, 43200);
				pipe.sync();
			}
		});

		dsForRW.save(entities);
		circleService.update(msgId, Msg.Op.Gift, activeValue);

		return giftIdList;
	}

	@Override
	public List<DBObject> findByGift(ObjectId msgId) {
		List<DBObject> objList = Lists.newArrayList();

		StringBuffer sbMap = new StringBuffer();
		sbMap.append(" function() { ");
		sbMap.append(" 	emit({ ");
		sbMap.append(" 		id : this.id ");
		sbMap.append(" 	}, { ");
		sbMap.append(" 		count : this.count ");
		sbMap.append(" 	}); ");
		sbMap.append(" } ");

		StringBuffer sbReduce = new StringBuffer();
		sbReduce.append(" function (key, values) { ");
		sbReduce.append(" 	var total = 0; ");
		sbReduce.append(" 	for (var i = 0; i < values.length; i++) { ");
		sbReduce.append(" 		total += values[i].count; ");
		sbReduce.append(" 	} ");
		sbReduce.append(" 	return total; ");
		sbReduce.append(" } ");

		DBCollection inputCollection = dsForRW.getDB().getCollection("gift");
		String map = sbMap.toString();
		String reduce = sbReduce.toString();
		DBObject query = new BasicDBObject("msgId", msgId);

		DBCursor cursor = inputCollection
				.mapReduce(map, reduce, "resultCollection", query)
				.getOutputCollection().find();

		while (cursor.hasNext()) {
			DBObject tObj = cursor.next();

			DBObject dbObj = (BasicDBObject) tObj.get("_id");
			dbObj.put("count", tObj.get("value"));

			objList.add(dbObj);
		}

		return objList;
	}

	@Override
	public List<DBObject> findByUser(ObjectId msgId) {
		List<DBObject> objList = Lists.newArrayList();

		StringBuffer sbMap = new StringBuffer();
		sbMap.append(" function() { ");
		sbMap.append(" 	emit({ ");
		sbMap.append(" 		userId : this.userId, ");
		sbMap.append(" 		nickname : this.nickname ");
		sbMap.append(" 	}, { ");
		sbMap.append(" 		price : this.price, ");
		sbMap.append(" 		count : this.count ");
		sbMap.append(" 	}); ");
		sbMap.append(" } ");

		StringBuffer sbReduce = new StringBuffer();
		sbReduce.append(" function (key, values) { ");
		sbReduce.append(" 	var result = 0; ");
		sbReduce.append(" 	for (var i = 0; i < values.length; i++) { ");
		sbReduce.append(" 		result += values[i].price * values[i].count; ");
		sbReduce.append(" 	} ");
		sbReduce.append(" 	return result; ");
		sbReduce.append(" } ");

		DBCollection inputCollection = dsForRW.getDB().getCollection("gift");
		String map = sbMap.toString();
		String reduce = sbReduce.toString();
		DBObject query = new BasicDBObject("msgId", msgId);

		DBCursor cursor = inputCollection
				.mapReduce(map, reduce, "resultCollection", query)
				.getOutputCollection().find();

		while (cursor.hasNext()) {
			DBObject tObj = cursor.next();

			DBObject dbObj = (BasicDBObject) tObj.get("_id");
			dbObj.put("money", tObj.get("value"));

			objList.add(dbObj);
		}

		return objList;
	}

	@Override
	public List<Givegift> find(ObjectId msgId, ObjectId giftId, int pageIndex,
			int pageSize) {
		String key = String.format("msg:%1$s:gift", msgId.toString());
		boolean exists = jedisTemplate.keyExists(key);
		// 赞没有缓存、加载所有赞到缓存
		if (!exists) {
			List<Givegift> giftList = dsForRW.find(Givegift.class).field("msgId")
					.equal(msgId).order("-_id").limit(500).asList();

			jedisTemplate.execute(new JedisCallbackVoid() {

				@Override
				public void execute(Jedis jedis) {
					Pipeline pipe = jedis.pipelined();
					for (Givegift gift : giftList) {
						String string = gift.toString();
						pipe.lpush(key, string);
					}
					pipe.expire(key, 43200);// 重置过期时间
					pipe.sync();
				}
			});
		}

		List<String> textList = jedisTemplate
				.execute(new JedisCallback<List<String>>() {

					@Override
					public List<String> execute(Jedis jedis) {
						long start = pageIndex * pageSize;
						long end = pageIndex * pageSize + pageSize - 1;

						return jedis.lrange(key, start, end);
					}

				});

		// 缓存未命中、超出缓存范围
		if (0 == textList.size()) {
			List<Givegift> giftList = dsForRW.find(Givegift.class).field("msgId")
					.equal(msgId).order("-_id").offset(pageIndex * pageSize)
					.limit(pageSize).asList();

			return giftList;
		} else {
			try {
				List<Givegift> giftList = Lists.newArrayList();
				for (String text : textList) {
					// JSON.parseObject(text, Gift.class)
					Givegift gift = new ObjectMapper().readValue(text, Givegift.class);
					giftList.add(gift);
				}
				return giftList;
			} catch (Exception e) {
				throw new ServiceException("赞缓存解析失败");
			}
		}
	}

}
