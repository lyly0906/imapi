package cn.xyz.mianshi.scheduleds;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cn.xyz.commons.constants.KKeyConstant;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.mianshi.vo.Msg;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Component
public class MsgListScheduled {

	private int pageSize = 50;

	@Resource(name = "jedisTemplate")
	private JedisTemplate jedisTemplate;

	@Resource(name = "dsForRW")
	protected Datastore dsForRW;

	public MsgListScheduled() {
		super();
	}

	private void syncHotList() {
		JedisCallbackVoid callback = jedis -> {
			for (String key : jedis.keys("msg.id.hot.list:*")) {

				long total = jedis.zcard(key);
				long pageCount = total / pageSize + 0 == total % pageSize ? 0 : 1;
				for (long i = 0; i < pageCount; i++) {
					Set<String> members = jedis.zrevrange(key, i * pageSize, i * pageSize + pageSize - 1);
					List<ObjectId> objIdList = Lists.newArrayList();
					for (String member : members) {
						objIdList.add(new ObjectId(member));
					}

					List<BasicDBObject> objList = Lists.newArrayList();
					try {
						DBObject ref = new BasicDBObject("_id", new BasicDBObject("$in", objIdList));
						DBCursor cursor = dsForRW.getCollection(Msg.class).find(ref)
								.sort(new BasicDBObject("count.total", -1));
						while (cursor.hasNext()) {
							BasicDBObject jo = (BasicDBObject) cursor.next();
							jo.put("msgId", jo.getString("_id"));
							jo.removeField("_id");
							objList.add(jo);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					String hmlKey = String.format(KKeyConstant.HotMsgListTemplate, key.substring(key.indexOf(':') + 1));
					jedis.hset(hmlKey, String.valueOf(i), JSON.toJSONString(objList));
				}

				// System.out.println("最热人才榜：" + key + "\t" + total + "\t" +
				// pageCount);
			}
		};
		jedisTemplate.execute(callback);
	}

	private void syncLatestList() {
		JedisCallbackVoid callback = jedis -> {
			for (String key : jedis.keys("msg.id.latest.list:*")) {
				long total = jedis.llen(key);
				long pageCount = total / pageSize + 0 == total % pageSize ? 0 : 1;
				for (long i = 0; i < pageCount; i++) {
					List<String> ids = jedis.lrange(key, i * pageSize, i * pageSize + pageSize - 1);
					List<ObjectId> objIdList = Lists.newArrayList();
					for (String id : ids) {
						objIdList.add(new ObjectId(id));
					}

					List<BasicDBObject> objList = Lists.newArrayList();
					try {
						DBObject ref = new BasicDBObject("_id", new BasicDBObject("$in", objIdList));
						DBCursor cursor = dsForRW.getCollection(Msg.class).find(ref).sort(new BasicDBObject("_id", -1));
						while (cursor.hasNext()) {
							BasicDBObject jo = (BasicDBObject) cursor.next();
							jo.put("msgId", jo.getString("_id"));
							jo.removeField("_id");
							objList.add(jo);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					String lmlKey = String.format(KKeyConstant.LatestMsgListTemplate,
							key.substring(key.indexOf(':') + 1));
					jedis.hset(lmlKey, String.valueOf(i), JSON.toJSONString(objList));
				}

				// System.out.println("最新人才榜：" + key + "\t" + total + "\t" +
				// pageCount);
			}
		};
		jedisTemplate.execute(callback);
	}

	//@Scheduled(cron = "0 0/1 * * * ?")
	public void execute() {
		long start = System.currentTimeMillis();

		syncHotList();
		syncLatestList();

		System.out.println("榜单刷新完毕，耗时" + (System.currentTimeMillis() - start) + "毫秒");
	}

}
