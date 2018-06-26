package cn.xyz.mianshi.service.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

import cn.xyz.commons.support.Callback;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.BeanUtils;
import cn.xyz.commons.utils.StringUtil;
import redis.clients.jedis.JedisPool;

public abstract class MongoRepository<T,ID extends Serializable> {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Resource(name = "jedisPool")
	protected JedisPool jedisPool;
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Resource(name = "morphia")
	protected Morphia morphia;
	protected Class<T> entityClass;// 实体类
	/*@Autowired
	protected AbstractRepository<T,ID> repository;*/
	
	public void updateAttributeByIdAndKey(ID id,String key,Object value) {
		Query<T> q = dsForRW.createQuery(getEntityClass()).field("_id").equal(id);
		UpdateOperations<T> ops = dsForRW.createUpdateOperations(getEntityClass());
		ops.set(key, value);
		dsForRW.update(q, ops);
	}
	public void updateAttributeByIdAndKey(Class<?> clazz,ID id,String key,Object value) {
		BasicDBObject query=new BasicDBObject("_id",id);
		BasicDBObject values=new BasicDBObject(key, value);
		dsForRW.getCollection(clazz).update(query, new BasicDBObject(MongoOperator.SET, values));
	}
	//修改
	public void updateAttributeByOps(ID id,UpdateOperations<T> ops) {
		Query<T> q = dsForRW.createQuery(getEntityClass()).field("_id").equal(id);
		dsForRW.update(q, ops);
	}
	
	
	
	public List<T> getEntityListsByKey(String key,Object value) {
		Query<T> q = dsForRW.createQuery(getEntityClass()).field(key).equal(value);
		return q.asList();
	}
	public List<T> getEntityListsByQuery(Query<T> q) {
		return q.asList();
	}
	public List<?> getEntityListsByKey(Class<?> clazz,String key,Object value,String sort) {
		Query<?> q = dsForRW.createQuery(clazz).field(key).equal(value);
		if(!StringUtil.isEmpty(sort))
			q.order(sort);
		return q.asList();
	}
	public List<?> getEntityListsByKey(Class<?> clazz,String key,Object value,String sort,int pageIndex,int pageSize) {
		Query<?> q = dsForRW.createQuery(clazz).field(key).equal(value);
		if(!StringUtil.isEmpty(sort))
			q.order(sort);
		return q.offset(pageIndex*pageSize).limit(pageSize).asList();
	}
	//将操作保存在数据库
	public Object saveEntity(Object entity){
		return dsForRW.save(entity);
	}
	public Object update(ID id,T entity){
		T dest = get(id);
		BeanUtils.copyProperties(entity, dest);
		return save(dest);
	} 
	public Object updateEntity(Class<?> clazz,ID id,Object entity){
		Object dest = dsForRW.get(clazz, id);
		BeanUtils.copyProperties(entity, dest);
		return dsForRW.save(dest);
	}
	
	public List<Object> findAndDelete(String name, DBObject q) {
		List<Object> idList = selectId(name, q);
		dsForRW.getDB().getCollection(name).remove(q);
		return idList;
	}
	//返回一个字段的集合
	public List distinct(String name,String key, DBObject q) {
		return dsForRW.getDB().getCollection(name).distinct(key,q);
	}
	
	public List distinct(String key, DBObject q) {
		return dsForRW.getCollection(getEntityClass()).distinct(key,q);
	}
	public BasicDBObject findAndModify(String name, DBObject query, DBObject update) {
		return (BasicDBObject) dsForRW.getDB().getCollection(name).findAndModify(query, update);
	}

	public <T> List<Object> findAndUpdate(Query<T> q, UpdateOperations<T> ops, DBObject keys, Callback callback) {
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

	public List<Object> findAndUpdate(String name, DBObject q, DBObject ops, DBObject keys, Callback callback) {
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

	public <T> DBCollection getCollection(Query<T> q) {
		DBCollection dbColl = q.getCollection();
		if (dbColl == null) {
			dbColl = dsForRW.getCollection(q.getEntityClass());
		}
		return dbColl;
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

	public void insert(String name, DBObject... arr) {
		dsForRW.getDB().getCollection(name).insert(arr);
	}

	public List<Object> selectId(String name, DBObject q) {
		List<Object> idList = Lists.newArrayList();

		DBCursor cursor = dsForRW.getDB().getCollection(name).find(q, new BasicDBObject("_id", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		return idList;
	}

	public List<Object> selectId(String name, QueryBuilder qb) {
		return selectId(name, qb.get());
	}
	
	public List<?> keysToIds(final List<Key<T>> keys) {
		final List<Object> ids = new ArrayList<Object>(keys.size() * 2);
		for (final Key<T> key : keys) {
			ids.add(key.getId());
		}
		return ids;
	}

	//
	public Query<T> createQuery() {
		return dsForRW.createQuery(getEntityClass());
	}

	//
	public UpdateOperations<T> createUpdateOperations() {
		return dsForRW.createUpdateOperations(getEntityClass());
	}

	
	
	public Class<T> getEntityClass() {
		if (null == entityClass)
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		return entityClass;
	}

	
	public Key<T> save(T entity) {
		return dsForRW.save(entity);
	}

	
	public Key<T> save(T entity, WriteConcern wc) {
		return dsForRW.save(entity, wc);
	}

	
	public UpdateResults updateFirst(Query<T> q, UpdateOperations<T> ops) {
		return dsForRW.updateFirst(q, ops);
	}

	
	public UpdateResults update(Query<T> q, UpdateOperations<T> ops) {
		return dsForRW.update(q, ops);
	}

	
	public WriteResult delete(T entity) {
		return dsForRW.delete(entity);
	}

	
	public WriteResult delete(T entity, WriteConcern wc) {
		return dsForRW.delete(entity, wc);
	}

	//通过id删除
	public WriteResult deleteById(ID id) {
		return dsForRW.delete(getEntityClass(), id);
	}

	
	public WriteResult deleteByQuery(Query<T> q) {
		return dsForRW.delete(q);
	}

	
	public T get(ID id) {
		return dsForRW.get(getEntityClass(), id);
	}
	public Object getEntityById(Class<?> clazz,ID id){
		return	dsForRW.get(clazz, id);
	}

	
	
	public List<ID> findIds() {
		return (List<ID>) keysToIds(dsForRW.find(getEntityClass()).asKeyList());
	}

	
	
	public List<ID> findIds(String key, Object value) {
		return (List<ID>) keysToIds(dsForRW.find(getEntityClass(), key, value).asKeyList());
	}

	
	
	public List<ID> findIds(Query<T> q) {
		return (List<ID>) keysToIds(q.asKeyList());
	}

	
	public Key<T> findOneId() {
		return findOneId(dsForRW.find(getEntityClass()));
	}

	
	public Key<T> findOneId(String key, Object value) {
		return findOneId(dsForRW.find(getEntityClass(), key, value));
	}

	
	public Key<T> findOneId(Query<T> q) {
		Iterator<Key<T>> keys = q.fetchKeys().iterator();
		return keys.hasNext() ? keys.next() : null;
	}

	
	public boolean exists(String key, Object value) {
		return exists(dsForRW.find(getEntityClass(), key, value));
	}

	
	public boolean exists(Query<T> q) {
		return dsForRW.getCount(q) > 0;
	}

	
	public long count() {
		return dsForRW.getCount(getEntityClass());
	}

	
	public long count(String key, Object value) {
		return count(dsForRW.find(getEntityClass(), key, value));
	}

	
	public long count(Query<T> q) {
		return dsForRW.getCount(q);
	}

	
	public T findOne(String key, Object value) {
		return dsForRW.find(getEntityClass(), key, value).get();
	}

	
	public T findOne(Query<T> q) {
		return q.get();
	}

	
	public QueryResults<T> find() {
		return createQuery();
	}

	
	public QueryResults<T> find(Query<T> q) {
		return q;
	}

	
	public void ensureIndexes() {
		dsForRW.ensureIndexes(getEntityClass());
	}

	
	public DBCollection getCollection() {
		return dsForRW.getCollection(getEntityClass());
	}

	
	public Datastore getDatastore() {
		return dsForRW;
	}

}
