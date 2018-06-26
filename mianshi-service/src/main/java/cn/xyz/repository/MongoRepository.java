package cn.xyz.repository;

import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import redis.clients.jedis.JedisPool;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.support.jedis.JedisTemplate;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public abstract class MongoRepository {

	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Resource(name = "jedisPool")
	protected JedisPool jedisPool;
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Resource(name = "morphia")
	protected Morphia morphia;

	// @Resource(name = "shardedJedisPool")
	// protected ShardedJedisPool shardedJedisPool;

	protected BasicDBObject findAndModify(String name, DBObject query, DBObject update) {
		return (BasicDBObject) dsForRW.getDB().getCollection(name).findAndModify(query, update);
	}

	protected void insert(String name, DBObject... arr) {
		dsForRW.getDB().getCollection(name).insert(arr);
	}

	protected List<Object> selectId(String name, DBObject q) {
		List<Object> idList = Lists.newArrayList();

		DBCursor cursor = dsForRW.getDB().getCollection(name).find(q, new BasicDBObject("_id", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		return idList;
	}

	protected <T> DBCollection getCollection(Query<T> q) {
		DBCollection dbColl = q.getCollection();
		if (dbColl == null) {
			dbColl = dsForRW.getCollection(q.getEntityClass());
		}
		return dbColl;
	}

	protected List<Object> findAndUpdate(String name, DBObject q, DBObject ops, DBObject keys, Callback callback) {
		List<Object> idList = Lists.newArrayList();

		DBCollection dbColl = dsForRW.getDB().getCollection(name);
		DBCursor cursor = null == keys ? dbColl.find(q) : dbColl.find(q, keys);
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();

			callback.execute(dbObj);

			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		dbColl.update(q, ops, false, true);

		return idList;
	}

	protected <T> List<Object> findAndUpdate(Query<T> q, UpdateOperations<T> ops, DBObject keys, Callback callback) {
		List<Object> idList = Lists.newArrayList();

		DBCursor cursor = getCollection(q).find(q.getQueryObject(), keys);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();

			// 执行推送
			callback.execute(dbObj);

			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		// 执行批量更新
		dsForRW.update(q, ops);

		return idList;
	}

	public List<Object> handlerAndReturnId(String name, DBObject q, DBObject keys, Callback callback) {
		List<Object> idList = Lists.newArrayList();

		DBCursor cursor = dsForRW.getDB().getCollection(name).find(q, keys);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			callback.execute(dbObj);
			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		return idList;
	}

	protected List<Object> selectId(String name, QueryBuilder qb) {
		return selectId(name, qb.get());
	}

	protected List<Object> findAndDelete(String name, DBObject q) {
		List<Object> idList = selectId(name, q);

		dsForRW.getDB().getCollection(name).remove(q);

		return idList;
	}

}
