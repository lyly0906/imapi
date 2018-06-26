package cn.xyz.mianshi.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cn.xyz.mianshi.service.MPService;
import cn.xyz.mianshi.service.UserManager;

@Service
public class MPServiceImpl implements MPService {
	@Resource(name = "dsForRW")
	Datastore dsForRW;
	@Resource(name = "dsForTigase")
	Datastore dsForTig;
	@Autowired
	private UserManager userManager;

	String getLastBody(int sender, int receiver) {
		DBCollection dbCollection = dsForTig.getDB().getCollection("shiku_msgs");
		BasicDBObject q = new BasicDBObject();
		q.put("sender", sender);
		q.put("receiver", receiver);
		DBObject dbObj = dbCollection.findOne(q, new BasicDBObject("body", 1), new BasicDBObject("_id", 1));
		return null == dbObj ? null
				: JSON.parseObject(dbObj.get("body").toString().replaceAll("&quot;", "\"")).getString("content");
	}

	@Override
	public Object getMsgList(int userId, int pageIndex, int pageSize) {
		List<BasicDBObject> msgList = Lists.newArrayList();

		DBCollection dbCollection = dsForTig.getDB().getCollection("shiku_msgs");
		BasicDBObject key = new BasicDBObject("sender", "true");
		BasicDBObject cond = new BasicDBObject();
		cond.put("receiver", userId);
		cond.put("direction", 0);
		cond.put("status", 0);
		BasicDBObject initial = new BasicDBObject("count", 0);
		String reduce = "function(obj,prev){prev.count ++;}";
		DBObject result = dbCollection.group(key, cond, initial, reduce);

		BasicDBList dbObjList = null != result ? (BasicDBList) result : null;
		for (int i = 0; i < dbObjList.size(); i++) {
			try {
				BasicDBObject dbObj = (BasicDBObject) dbObjList.get(i);
				int sender = dbObj.getInt("sender");
				int receiver = userId;
				String nickname = userManager.getUser(sender).getNickname();
				int count = dbObj.getInt("count");

				dbObj.put("nickname", nickname);
				dbObj.put("count", count);
				dbObj.put("sender", sender);
				dbObj.put("receiver", receiver);
				dbObj.put("body", getLastBody(sender, receiver));

				msgList.add(dbObj);
			} catch (cn.xyz.commons.ex.ServiceException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return msgList;
	}

	@Override
	public Object getMsgList(int sender, int receiver, int pageIndex, int pageSize) {
		List<DBObject> msgList = Lists.newArrayList();
		BasicDBObject q = new BasicDBObject();
		q.put("sender", sender);
		q.put("receiver", receiver);
		q.put("direction", 0);
		q.put("status", 0);
		DBCollection dbCollection = dsForTig.getDB().getCollection("shiku_msgs");
		DBCursor cursor = dbCollection.find(q);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			dbObj.put("nickname", userManager.getUser(sender).getNickname());
			dbObj.put("content",
					JSON.parseObject(dbObj.get("body").toString().replaceAll("&quot;", "\"")).getString("content"));
			msgList.add(dbObj);
		}
		return msgList;
	}

}
