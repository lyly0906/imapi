package cn.xyz.repository.mongo;

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

import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.repository.BaseRepository;
import redis.clients.jedis.JedisPool;

import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class BaseRepositoryImpl<T, K> implements BaseRepository<T, K> {

	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Resource(name = "jedisPool")
	protected JedisPool jedisPool;
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Resource(name = "morphia")
	protected Morphia morphia;
	protected Class<T> type;

	@SuppressWarnings("unchecked")
	public BaseRepositoryImpl() {
		type = ((Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	/**
	 * Converts from a List<Key> to their id values
	 */
	protected List<?> keysToIds(final List<Key<T>> keys) {
		final List<Object> ids = new ArrayList<Object>(keys.size() * 2);
		for (final Key<T> key : keys) {
			ids.add(key.getId());
		}
		return ids;
	}

	/**
	 * The underlying collection for this DAO
	 */
	public DBCollection getCollection() {
		return dsForRW.getCollection(type);
	}

	public Query<T> createQuery() {
		return dsForRW.createQuery(type);
	}

	public UpdateOperations<T> createUpdateOperations() {
		return dsForRW.createUpdateOperations(type);
	}

	public Class<T> getEntityClass() {
		return type;
	}

	public Key<T> save(final T entity) {
		return dsForRW.save(entity);
	}

	public Key<T> save(final T entity, final WriteConcern wc) {
		return dsForRW.save(entity, wc);
	}

	public UpdateResults updateFirst(final Query<T> q,
			final UpdateOperations<T> ops) {
		return dsForRW.updateFirst(q, ops);
	}

	public UpdateResults update(final Query<T> q, final UpdateOperations<T> ops) {
		return dsForRW.update(q, ops);
	}

	public WriteResult delete(final T entity) {
		return dsForRW.delete(entity);
	}

	public WriteResult delete(final T entity, final WriteConcern wc) {
		return dsForRW.delete(entity, wc);
	}

	public WriteResult deleteById(final K id) {
		return dsForRW.delete(type, id);
	}

	public WriteResult deleteByQuery(final Query<T> q) {
		return dsForRW.delete(q);
	}

	public T get(final K id) {
		return dsForRW.get(type, id);
	}

	@SuppressWarnings("unchecked")
	public List<K> findIds() {
		return (List<K>) keysToIds(dsForRW.find(type).asKeyList());
	}

	@SuppressWarnings("unchecked")
	public List<K> findIds(final Query<T> q) {
		return (List<K>) keysToIds(q.asKeyList());
	}

	@SuppressWarnings("unchecked")
	public List<K> findIds(final String key, final Object value) {
		return (List<K>) keysToIds(dsForRW.find(type, key, value).asKeyList());
	}

	public Key<T> findOneId() {
		return findOneId(dsForRW.find(type));
	}

	public Key<T> findOneId(final String key, final Object value) {
		return findOneId(dsForRW.find(type, key, value));
	}

	public Key<T> findOneId(final Query<T> query) {
		Iterator<Key<T>> keys = query.fetchKeys().iterator();
		return keys.hasNext() ? keys.next() : null;
	}

	public boolean exists(final String key, final Object value) {
		return exists(dsForRW.find(type, key, value));
	}

	public boolean exists(final Query<T> q) {
		return dsForRW.getCount(q) > 0;
	}

	public long count() {
		return dsForRW.getCount(type);
	}

	public long count(final String key, final Object value) {
		return count(dsForRW.find(type, key, value));
	}

	public long count(final Query<T> q) {
		return dsForRW.getCount(q);
	}

	public T findOne(final String key, final Object value) {
		return dsForRW.find(type, key, value).get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mongodb.morphia.DAO#findOne(org.mongodb.morphia.query.Query)
	 */
	public T findOne(final Query<T> q) {
		return q.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mongodb.morphia.DAO#find()
	 */
	public QueryResults<T> find() {
		return createQuery();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mongodb.morphia.DAO#find(org.mongodb.morphia.query.Query)
	 */
	public QueryResults<T> find(final Query<T> q) {
		return q;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mongodb.morphia.DAO#getDatastore()
	 */
	public Datastore getDatastore() {
		return dsForRW;
	}

	public void ensureIndexes() {
		dsForRW.ensureIndexes(type);
	}
}
