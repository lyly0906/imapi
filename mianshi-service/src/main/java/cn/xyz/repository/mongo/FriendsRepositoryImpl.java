package cn.xyz.repository.mongo;

import java.util.List;

import cn.xyz.mianshi.vo.NewFriends;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Service;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.repository.FriendsRepository;
import cn.xyz.repository.MongoRepository;

import com.google.common.collect.Lists;

@Service
public class FriendsRepositoryImpl extends MongoRepository implements FriendsRepository {



	@Override
	public Fans deleteFans(int userId, int toUserId) {
		Query<Fans> q = dsForRW.createQuery(Fans.class).field("userId").equal(userId).field("toUserId").equal(toUserId);

		return dsForRW.findAndDelete(q);
	}
	@Override
	public void deleteFans(int userId) {
		Query<Fans> q = dsForRW.createQuery(Fans.class);
		q.or(q.criteria("userId").equal(userId),q.criteria("toUserId").equal(userId));
		dsForRW.delete(q);
	}

	@Override
	public void deleteFriends(int userId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class);
		q.or(q.criteria("userId").equal(userId),q.criteria("toUserId").equal(userId));
		dsForRW.delete(q);
	}
	

	@Override
	public Friends deleteFriends(int userId, int toUserId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId).field("toUserId").equal(toUserId);

		return dsForRW.findAndDelete(q);
	}

	@Override
	public void deleteNewFriends(int userId) {
		Query<NewFriends> q = dsForRW.createQuery(NewFriends.class).field("userId").equal(userId);
		dsForRW.delete(q);
	}

	

	@Override
	public Friends getFriends(int userId, int toUserId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class);
		q.field("userId").equal(userId);
		q.field("toUserId").equal(toUserId);
		Friends friends = q.get();
		Query<Friends> p = dsForRW.createQuery(Friends.class);
		p.field("userId").equal(toUserId);
		p.field("toUserId").equal(userId);
		Friends tofriends=p.get();
		if(null==friends)
			return friends;
		else if(null==tofriends)
			friends.setIsBeenBlack(0);
		else
			friends.setIsBeenBlack(tofriends.getBlacklist());
		return friends;
	}

	@Override
	public List<Friends> queryBlacklist(int userId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId).field("blacklist").equal(1);

		return q.asList();
	}

	@Override
	public List<Fans> getFansList(int userId) {
		Query<Fans> q = dsForRW.createQuery(Fans.class).field("userId").equal(userId);

		return q.asList();
	}

	@Override
	public List<Integer> queryFansId(int userId) {
		Query<Fans> q = dsForRW.createQuery(Fans.class).retrievedFields(true, "toUserId").field("userId").equal(userId);
		q.filter("status !=", 0);

		List<Integer> result = Lists.newArrayList();
		List<Fans> fList = q.asList();

		fList.forEach(fans -> {
			result.add(fans.getToUserId());
		});

		return result;
	}

	@Override
	public List<Friends> queryFollow(int userId,int status) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId);
		if(0<status)
			q.filter("status",status);
		q.filter("status !=", 0);
		// q.or(q.criteria("status").equal(Friends.Status.Attention),
		// q.criteria("status").equal(Friends.Status.Friends));

		return q.asList();

	}

	@Override
	public List<Integer> queryFollowId(int userId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).retrievedFields(true, "toUserId").field("userId").equal(userId);
		q.filter("status !=", 0);
		
		List<Integer> result = Lists.newArrayList();
		List<Friends> fList = q.asList();

		fList.forEach(friends -> {
			result.add(friends.getToUserId());
		});

		return result;
	}

	@Override
	public List<Friends> queryFriends(int userId) {
		Query<Friends> q = dsForRW.createQuery(Friends.class);
		q.field("userId").equal(userId);
		q.field("status").equal(Friends.Status.Friends);
		return q.asList();
	}
	
	@Override
	public List<Friends> friendsOrBlackList(int userId,String type) {
		Query<Friends> q = dsForRW.createQuery(Friends.class);
		q.field("userId").equal(userId);
		if("friendList".equals(type)){  //返回好友和单向关注的用户列表
			q.filter("status !=",Friends.Status.Stranger); //返回非陌生人列表(好友和单向关注)
			q.filter("blacklist !=", Friends.Blacklist.Yes); //排除加入黑名单的用户
		}else if("blackList".equals(type)){ //返回黑名单的用户列表
			q.field("blacklist").equal(Friends.Blacklist.Yes); 
		}
		return q.asList();
	}


	@Override
	public Object saveFans(Fans fans) {
		return dsForRW.save(fans).getId();
	}


	@Override
	public Object saveFriends(Friends friends) {
		return dsForRW.save(friends);
	}

	@Override
	public Object saveNewFriends(NewFriends friends) {
		return dsForRW.save(friends);
	}

	@Override
	public Friends updateFriends(Friends friends) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(friends.getUserId()).field("toUserId").equal(friends.getToUserId());

		UpdateOperations<Friends> ops = dsForRW.createUpdateOperations(Friends.class);
		ops.set("modifyTime", DateUtil.currentTimeSeconds());
		if (null != friends.getBlacklist())
			ops.set("blacklist", friends.getBlacklist());
		if (null != friends.getStatus())
			ops.set("status", friends.getStatus());
		if (!StringUtil.isEmpty(friends.getToNickname()))
			ops.set("toNickname", friends.getToNickname());
		if (!StringUtil.isEmpty(friends.getRemarkName()))
			ops.set("remarkName", friends.getRemarkName());

		return dsForRW.findAndModify(q, ops);
		// try {
		// // DBObject query = new BasicDBObject(2);
		// // query.put("userId", friends.getUserId());
		// // query.put("toUserId", friends.getToUserId());
		//
		// DBObject set = new BasicDBObject();
		// if (null != friends.getBlacklist())
		// set.put("blacklist", friends.getBlacklist());
		// if (null != friends.getStatus())
		// set.put("status", friends.getStatus());
		// if (!StringUtils.isEmpty(friends.getRemarkName()))
		// set.put("remarkName", friends.getRemarkName());
		// set.put("modifyTime", Dates.currentTimeSeconds());
		//
		// DBObject update = new BasicDBObject("$set", set);
		//
		// dsForRW.getDB().getCollection("u_friends")
		// .findAndModify(query, update);
		//
		// return true;
		// } catch (Exception e) {
		// logger.error("更新好友关系失败", e);
		// }
		//
		// return false;
	}

	@Override
	public NewFriends updateNewFriends(NewFriends friends) {
		Query<NewFriends> q = dsForRW.createQuery(NewFriends.class).field("userId").equal(friends.getUserId()).field("toUserId").equal(friends.getToUserId());

		UpdateOperations<NewFriends> ops = dsForRW.createUpdateOperations(NewFriends.class);


		ops.set("modifyTime", DateUtil.currentTimeSeconds());
		if (friends.getType() != 0)
			ops.set("type", friends.getType());


		NewFriends x = dsForRW.findAndModify(q, ops);

		// 删除其他502的记录
		if(friends.getType() == 508) {
			Query<NewFriends> qs = dsForRW.createQuery(NewFriends.class).field("userId").equal(friends.getUserId()).field("toUserId").equal(friends.getToUserId()).field("type").equal(502);
			dsForRW.delete(qs);
			Query<NewFriends> qs1 = dsForRW.createQuery(NewFriends.class).field("toUserId").equal(friends.getUserId()).field("userId").equal(friends.getToUserId()).field("type").equal(502);
			dsForRW.delete(qs1);
		}
		return x;
		// try {
		// // DBObject query = new BasicDBObject(2);
		// // query.put("userId", friends.getUserId());
		// // query.put("toUserId", friends.getToUserId());
		//
		// DBObject set = new BasicDBObject();
		// if (null != friends.getBlacklist())
		// set.put("blacklist", friends.getBlacklist());
		// if (null != friends.getStatus())
		// set.put("status", friends.getStatus());
		// if (!StringUtils.isEmpty(friends.getRemarkName()))
		// set.put("remarkName", friends.getRemarkName());
		// set.put("modifyTime", Dates.currentTimeSeconds());
		//
		// DBObject update = new BasicDBObject("$set", set);
		//
		// dsForRW.getDB().getCollection("u_friends")
		// .findAndModify(query, update);
		//
		// return true;
		// } catch (Exception e) {
		// logger.error("更新好友关系失败", e);
		// }
		//
		// return false;
	}


	

}
