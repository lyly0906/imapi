package cn.xyz.repository.mongo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.support.jedis.JedisCallback;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.service.impl.MongoRepository;
import cn.xyz.mianshi.vo.Comment;
import cn.xyz.mianshi.vo.Msg;
import cn.xyz.mianshi.vo.Praise;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.MsgPraiseRepository;
import cn.xyz.repository.MsgRepository;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Service()
public class MsgPraiseRepositoryImpl extends MongoRepository<Object, ObjectId> implements MsgPraiseRepository {
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Autowired
	private UserManager userService;
	@Autowired
	private MsgRepository circleService;
	
	@Override
	public ObjectId add(int userId, ObjectId msgId) {
		User user = userService.getUser(userId);
		
		if (!exists(userId, msgId)) {
			Praise entity = new Praise(ObjectId.get(), msgId, user.getUserId(),
					user.getNickname(), DateUtil.currentTimeSeconds());

			// 缓存赞
			jedisTemplate.execute(new JedisCallbackVoid() {

				@Override
				public void execute(Jedis jedis) {
					String key = String.format("msg:%1$s:praise",
							msgId.toString());
					String strings = entity.toString();

					Pipeline pipe = jedis.pipelined();
					pipe.lpush(key, strings);// 插入最新赞
					pipe.expire(key, 43200);// 重置过期时间
					pipe.sync();
				}
			});
			// 持久化赞
			dsForRW.save(entity);
			// 更新消息：赞+1、活跃度+1
			circleService.update(msgId, Msg.Op.Praise, 1);
			
			ThreadUtil.executeInThread(new Callback() {
				@Override
				public void execute(Object obj) {
					push(userId, msgId);
				}
			});
			
			return entity.getPraiseId();
		}

		return null;
	}
	
	private void push(int userId,ObjectId msgId){
		// xmpp推送
		User user = userService.getUser(userId);
		Query<Msg> q=dsForRW.createQuery(Msg.class);
		Msg msg=q.filter("msgId", msgId).get();
		int type=msg.getBody().getType();
		String url=null;
		if(type==1){
			url=msg.getBody().getText();
		}else if(type==2){
			url=msg.getBody().getImages().get(0).getTUrl();
		}else if(type==3){
			url=msg.getBody().getAudios().get(0).getTUrl();
		}else if(type==4){
			url=msg.getBody().getImages().get(0).getTUrl();
		}
		String t=String.valueOf(type);
		String u=String.valueOf(msgId);
		String mm=u+","+t+","+url;
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.PRAISE);
		messageBean.setFromUserId(String.valueOf(userId));
		messageBean.setFromUserName(user.getNickname());
		messageBean.setContent("");
		messageBean.setObjectId(mm);
		
		try {
			List<Integer> praiseuserIdlist=new ArrayList<Integer>();
			DBObject d=new BasicDBObject("msgId",msgId);
			praiseuserIdlist=distinct("s_praise", "userId", d);
			
			List<Integer> userIdlist=new ArrayList<Integer>();
			userIdlist=distinct("s_comment","userId", d);
			
			userIdlist.addAll(praiseuserIdlist);
			
			userIdlist.add(msg.getUserId());
			
			HashSet<Integer> hs=new HashSet<Integer>(userIdlist);
			List<Integer> list=new ArrayList<Integer>(hs);
			
			//移出集合中当前操作人
			for (int i = 0; i < list.size(); i++) {   
			       if (list.get(i).equals(userId)) {   
			    	   list.remove(i);   
			       }   
			    } 
			KXMPPServiceImpl.getInstance().send("10002",DigestUtils.md5Hex("10002"),list, messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean delete(int userId, ObjectId msgId) {
		// 删除评论
		Query<Praise> q = dsForRW.createQuery(Praise.class).field("msgId")
				.equal(msgId).field("userId").equal(userId);
		Praise praise = dsForRW.findAndDelete(q);
		// 刷新缓存
		jedisTemplate.execute(new JedisCallbackVoid() {

			@Override
			public void execute(Jedis jedis) {
				String key = String.format("msg:%1$s:praise", msgId.toString());
				String value = praise.toString();
				jedis.lrem(key, 0, value);
			}
		});
		// 更新消息：赞-1、活跃度-1
		circleService.update(msgId, Msg.Op.Praise, -1);

		return true;
	}

	@Override
	public List<Praise> find(ObjectId msgId, ObjectId praiseId, int pageIndex,
			int pageSize) {
		String key = String.format("msg:%1$s:praise", msgId.toString());
		boolean exists = jedisTemplate.keyExists(key);
		// 赞没有缓存、加载所有赞到缓存
		if (!exists) {
			List<Praise> praiseList = dsForRW.find(Praise.class).field("msgId")
					.equal(msgId).order("-_id").asList();

			jedisTemplate.execute(new JedisCallbackVoid() {

				@Override
				public void execute(Jedis jedis) {
					Pipeline pipe = jedis.pipelined();
					for (Praise praise : praiseList) {
						String string = praise.toString();
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
			List<Praise> praiseList = dsForRW.find(Praise.class).field("msgId")
					.equal(msgId).order("-_id").offset(pageIndex * pageSize)
					.limit(pageSize).asList();

			return praiseList;
		} else {
			try {
				List<Praise> praiseList = Lists.newArrayList();
				for (String text : textList) {
					// JSON.parseObject(text, Praise.class)
					Praise praise = new ObjectMapper().readValue(text,
							Praise.class);
					praiseList.add(praise);
				}
				return praiseList;
			} catch (Exception e) {
				throw new ServiceException("赞缓存解析失败");
			}
		}
	}

	@Override
	public boolean exists(int userId, ObjectId msgId) {
		Query<Praise> q = dsForRW.createQuery(Praise.class).field("msgId")
				.equal(msgId).field("userId").equal(userId);
		
		long count = q.countAll();
		return 0 != count;
	}

}
