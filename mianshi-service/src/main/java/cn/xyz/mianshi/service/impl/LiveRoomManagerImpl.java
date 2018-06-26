package cn.xyz.mianshi.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.vo.Black;
import cn.xyz.mianshi.vo.Gift;
import cn.xyz.mianshi.vo.Givegift;
import cn.xyz.mianshi.vo.LiveRoom;
import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.LiveRoom.LiveRoomMember;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

@Service
public class LiveRoomManagerImpl extends MongoRepository<LiveRoom, ObjectId> {

	@Autowired
	private UserManagerImpl userManager;
	
	//创建直播间
	public LiveRoom createLiveRoom(LiveRoom room){

		try {
			room.setNickName(userManager.getUser(room.getUserId()).getNickname());
			room.setCreateTime(DateUtil.currentTimeSeconds());
			room.setNotice(room.getNotice());
			room.setNumbers(1);
			room.setUrl(room.getUserId()+"_"+DateUtil.currentTimeSeconds());
			/*room.setJid(room.getJid());*/
			room.setJid(room.getJid());
			System.out.println(room.getJid());
			ObjectId id=(ObjectId) save(room).getId();
			
			LiveRoomMember member=new LiveRoomMember();
			member.setUserId(room.getUserId());
			member.setRoomId(id);
			member.setCreateTime(DateUtil.currentTimeSeconds());
			member.setNickName(userManager.getUser(room.getUserId()).getNickname());
			member.setType(1);
			saveEntity(member);
			
			room.setUrl(KSessionUtil.getConfig().getLiveUrl()+room.getUrl());
			System.out.println(room.getNotice());
			return room;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	//修改直播间信息
	public void updateLiveRoom(LiveRoom room){
		UpdateOperations<LiveRoom> ops=createUpdateOperations();
		if(!StringUtil.isEmpty(room.getName()))
			ops.set("name", room.getName());
		if(!StringUtil.isEmpty(room.getUrl()))
			ops.set("url", room.getUrl());
		if(!StringUtil.isEmpty(room.getNotice()))
			ops.set("notice", room.getNotice());
		ops.disableValidation();
		updateAttributeByOps(room.getRoomId(), ops);
		
	}
	
	//删除直播间
	public void deleteLiveRoom(ObjectId roomId){
		deleteById(roomId);
		//删除直播间中的成员
		Query<LiveRoomMember> query=dsForRW.createQuery(LiveRoomMember.class);
		query.filter("roomId", roomId);
		dsForRW.delete(query);
	}
	//查询所有房间
	public List<LiveRoom> findLiveRoomList(String name,String nickName,Integer userId,int pageIndex, int pageSize){
		Query<LiveRoom> query=createQuery();
		if(!StringUtil.isEmpty(name))
			query.filter("name",name);
		if(!StringUtil.isEmpty(nickName))
			query.filter("nickName",nickName);
		if(0!=userId)
			query.filter("userId",userId);
		query.order("-createTime");
		query.offset(pageIndex * pageSize);
		
		List<LiveRoom> roomList=query.limit(pageSize).asList();
		for (LiveRoom liveRoom : roomList) {
			if(!liveRoom.getUrl().contains("//")){
				liveRoom.setUrl(KSessionUtil.getConfig().getLiveUrl()+liveRoom.getUrl());
			}else if(DateUtil.currentTimeSeconds()-liveRoom.getCreateTime()>3600){
				roomList.remove(liveRoom);
			}	
		}
		return roomList;
	}
	
	//加入直播间
	public boolean enterIntoLiveRoom(Integer userId,ObjectId roomId){
		Query<LiveRoomMember> q=dsForRW.createQuery(LiveRoomMember.class);
		q.filter("roomId",roomId);
		q.filter("userId",userId);
		LiveRoomMember liveRoomMember=q.get();
		Query<Black> b=dsForRW.createQuery(Black.class);
		b.filter("roomId",roomId);
		b.filter("userId",userId);
		//成员是否在黑名单
		if(b.get()==null){
			Query<LiveRoom> r=dsForRW.createQuery(LiveRoom.class);
			LiveRoom liveRoom=r.filter("roomId", roomId).get();
			
			User user=userManager.getUser(userId);
			//房间是否存在改用户
			if(null!=liveRoomMember){
				UpdateOperations<LiveRoomMember> ops = dsForRW.createUpdateOperations(LiveRoomMember.class);
				ops.set("online", 1);
				dsForRW.update(q, ops);
			}else{
				LiveRoomMember member=new LiveRoomMember();
				member.setUserId(userId);
				member.setRoomId(roomId);
				member.setCreateTime(DateUtil.currentTimeSeconds());
				member.setNickName(userManager.getUser(userId).getNickname());
				member.setOnline(1);
				saveEntity(member);
				//修改直播间总人数
				UpdateOperations<LiveRoom> ops=createUpdateOperations();
				ops.inc("numbers", 1);
				updateAttributeByOps(roomId, ops);
			}
			MessageBean messageBean=new MessageBean();
			messageBean.setType(KXMPPServiceImpl.JOINLIVE);
			messageBean.setContent(liveRoom.getName());
			messageBean.setObjectId(liveRoom.getJid());
			messageBean.setFileName(liveRoom.getRoomId().toString());
			messageBean.setToUserId(user.getUserId()+"");
			messageBean.setToUserName(user.getNickname());
			try {
				List<Integer> users=findMembersUserIds(roomId);
				KXMPPServiceImpl.getInstance().send(users,messageBean.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
			return true;
		}else{
			return false;
		}
		/*DBObject value=new BasicDBObject(MongoOperator.INC, 1);
		updateAttributeByIdAndKey(roomId, "numbers", value);*/
		
		
		
	}
	//退出直播间
	public void exitLiveRoom(Integer userId,ObjectId roomId){
		//删除直播间成员
		Query<LiveRoomMember> q=dsForRW.createQuery(LiveRoomMember.class);
		q.filter("roomId",roomId);
		q.filter("userId",userId);
		LiveRoomMember liveRoomMember=q.get();
		
		User user=userManager.getUser(userId);
		LiveRoom liveRoom = get(roomId);
		if(null==liveRoomMember||liveRoomMember.getOnline()==0)
			return;
		
		if(liveRoomMember.getType()==3&&liveRoomMember.getState()!=1){
			dsForRW.delete(q);
			//修改直播间总人数
			UpdateOperations<LiveRoom> ops=createUpdateOperations();
			if(liveRoom.getNumbers()<=0){
				return;
			}else{
				ops.inc("numbers", -1);
				updateAttributeByOps(roomId, ops);
			}
		}else{
			if(liveRoomMember.getType()==1){
				UpdateOperations<LiveRoomMember> ops = dsForRW.createUpdateOperations(LiveRoomMember.class);
				ops.set("online", 0);
				dsForRW.update(q, ops);
				Query<Black> b=dsForRW.createQuery(Black.class);
				b.filter("roomId", roomId);
				dsForRW.delete(b);
			}else{
				UpdateOperations<LiveRoomMember> ops = dsForRW.createUpdateOperations(LiveRoomMember.class);
				ops.set("online", 0);
				dsForRW.update(q, ops);
			}
		}
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.DELETE_MEMBER);
		messageBean.setObjectId(liveRoom.getJid());
		messageBean.setFromUserId(user.getUserId()+"");
		messageBean.setFromUserName(user.getNickname());
		messageBean.setToUserId(user.getUserId()+"");
		messageBean.setToUserName(user.getNickname());
		try {
			
			List<Integer> users=findMembersUserIds(roomId);
			KXMPPServiceImpl.getInstance().send(users, messageBean.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	//踢出直播间
	public void kick(Integer userId,ObjectId roomId){
		//删除直播间成员
		Query<LiveRoomMember> q=dsForRW.createQuery(LiveRoomMember.class);
		q.filter("roomId",roomId);
		q.filter("userId", userId);
		dsForRW.delete(q);
		LiveRoom liveRoom = get(roomId);
		//修改直播间总人数
		UpdateOperations<LiveRoom> ops=createUpdateOperations();
		if(liveRoom.getNumbers()<=0){
			return;
		}else{
			ops.inc("numbers", -1);
			updateAttributeByOps(roomId, ops);
		}
		User touser=userManager.getUser(userId);
		/*if(touser==null){
			throw new ServiceException("用户不在该房间");
		}*/
		User user=userManager.getUser(ReqUtil.getUserId());
		
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.DELETE_MEMBER);
		messageBean.setObjectId(liveRoom.getJid());
		messageBean.setFromUserId(user.getUserId()+"");
		messageBean.setFromUserName(user.getNickname());
		messageBean.setToUserId(touser.getUserId()+"");
		messageBean.setToUserName(touser.getNickname());
		try {
			List<Integer> users=findMembersUserIds(roomId);
			users.add(userId);
			KXMPPServiceImpl.getInstance().send(users, messageBean.toString());
		} catch (Exception e) {	
			// TODO: handle exception
		}
		//添加到黑名单
		Black black=new Black();
		black.setRoomId(roomId);
		black.setUserId(userId);
		black.setTime(DateUtil.currentTimeSeconds());
		saveEntity(black);
	}

	//查询房间成员
	public List<LiveRoomMember> findLiveRoomMemberList(ObjectId roomId){
		Query<LiveRoomMember> query=dsForRW.createQuery(LiveRoomMember.class);
		
		if(null!=roomId)
			query.filter("roomId",roomId);
			query.filter("online", 1);
		return query.asList();
	}
	public List<Integer> findMembersUserIds(ObjectId roomId){
		List<Integer> userIds=null;
		BasicDBObject query=new BasicDBObject();
		if(null!=roomId)
			query.append("roomId",roomId);
			query.append("online", 1);
		 userIds = distinct("LiveRoomMember","userId", query);
		return userIds;
	}
	
	//获取单个成员
	public LiveRoomMember getLiveRoomMember(ObjectId roomId,Integer userId){
		Query<LiveRoomMember> q=dsForRW.createQuery(LiveRoomMember.class);
		q.filter("roomId", roomId);
		q.filter("userId", userId);
		LiveRoomMember liveRoomMember=q.get();
		return liveRoomMember;
		
	}
	
	//禁言/取消禁言
	public void shutup(int state,Integer userId,ObjectId roomId){
		Query<LiveRoomMember> q=dsForRW.createQuery(LiveRoomMember.class);
		q.filter("userId",userId);
		LiveRoom liveRoom=get(roomId);
		LiveRoomMember livemember=new LiveRoomMember();
		livemember=q.get();
		//修改状态
		UpdateOperations<LiveRoomMember> ops = dsForRW.createUpdateOperations(LiveRoomMember.class);
		ops.set("state", state);
		dsForRW.update(q, ops);
		//xmpp推送
		User user=userManager.getUser(userId);
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.GAG);
		if(state==1){
			messageBean.setContent(DateUtil.currentTimeSeconds());
		}else{
			messageBean.setContent(0);
		}
		messageBean.setObjectId(liveRoom.getJid());
		messageBean.setToUserId(livemember.getUserId()+"");
		messageBean.setToUserName(livemember.getNickName());
		try {
			List<Integer> userIdlist=findMembersUserIds(roomId);
			KXMPPServiceImpl.getInstance().send(userIdlist,messageBean.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	//添加礼物
	public void addGift(String name,String photo,double price,int type){
		Gift gift=new Gift();
		gift.setName(name);
		gift.setPhoto(photo);
		gift.setPrice(price);
		gift.setType(type);
		saveEntity(gift);
	}
	//删除礼物
	public void deleteGift(ObjectId giftId){
		Query<Gift> q=dsForRW.createQuery(Gift.class);
		q.filter("giftId", giftId);
		dsForRW.delete(q);
	}
	
	//查询所有的礼物
	public List<Gift> findAllgift(String name,int pageIndex,int pageSize){
		Query<Gift> query=dsForRW.createQuery(Gift.class);
		if(!name.equals("")){
			query.filter("name", name);
		}
		query.offset(pageSize*pageIndex);
		List<Gift> giftList=query.limit(pageSize).asList();
		return giftList;
	}
	
	//送礼物
	public ObjectId giveGift(Integer userId,ObjectId giftId,int count,Double price,ObjectId roomId){
		Query<User> q=dsForRW.createQuery(User.class);
		q.filter("userId",userId);
		User user=new User();
		user=q.get();
		Query<LiveRoom> query=dsForRW.createQuery(LiveRoom.class);
		query.filter("_id", roomId);
		
		LiveRoom liveRoom=new LiveRoom();
		liveRoom=query.get();
		if(user.getBalance()>=price*count){
			Givegift givegift=new Givegift();
			givegift.setUserId(userId);
			givegift.setGiftId(giftId);
			givegift.setCount(count);
			givegift.setPrice(price*count);
			givegift.setTime(DateUtil.currentTimeSeconds());
			saveEntity(givegift);
			//修改用户的余额
			userManager.rechargeUserMoeny(userId, price, 2);
			//xmpp推送消息
			MessageBean messageBean=new MessageBean();
			messageBean.setType(KXMPPServiceImpl.GIFT);
			messageBean.setFromUserId(userId.toString());
			messageBean.setFromUserName(user.getNickname());
			messageBean.setObjectId(liveRoom.getJid());
			messageBean.setContent(giftId.toString());
			try {
				List<Integer> userIdlist=findMembersUserIds(roomId);
				KXMPPServiceImpl.getInstance().send(userIdlist, messageBean.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
			return giftId;
		}else{
			throw new ServiceException("余额不足");
		}
	}
	
	//购买礼物的记录
	public List<Givegift> giftdeal(Integer userId,int pageIndex,int pageSize){
		Query<Givegift> query=dsForRW.createQuery(Givegift.class);
		query.filter("userId", userId);
		query.offset(pageSize*pageIndex);
		List<Givegift> givegiftList=query.limit(pageSize).asList();	
		return givegiftList;
	}
	//发送弹幕
	public ObjectId barrage(Integer userId,ObjectId roomId,String text){
		Query<User> q=dsForRW.createQuery(User.class);
		q.filter("userId",userId);
		User user=new User();
		user=q.get();
		LiveRoom liveRoom = get(roomId);
		int price=1;
		ObjectId barrageId=null;
		if(user.getBalance()>=1*price){
			Givegift givegift=new Givegift();
			givegift.setCount(1);
			givegift.setPrice(1.0);
			givegift.setUserId(userId);
			givegift.setTime(DateUtil.currentTimeSeconds());
			
			saveEntity(givegift);
			barrageId=givegift.getGiftId();
			//修改用户账户金额
			user.setBalance(user.getBalance()-1);
			saveEntity(user);
			//xmpp推送
			MessageBean messageBean=new MessageBean();
			messageBean.setType(KXMPPServiceImpl.BARRAGE);
			messageBean.setFromUserId(userId.toString());
			messageBean.setFromUserName(user.getNickname());
			messageBean.setObjectId(liveRoom.getJid());
			messageBean.setContent(text);
			try {
				List<Integer> userIdlist=findMembersUserIds(roomId);
				KXMPPServiceImpl.getInstance().send(userIdlist,messageBean.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
			return barrageId;
		}else{
		 throw new ServiceException("余额不足请充值");
		}
		
	}
	//设置/取消管理员
	public void setmanage(Integer userId,int type,ObjectId roomId){
		/*LiveRoomMember liveRoomMember=dsForRW.get(LiveRoomMember.class, userId);*/
		Query<LiveRoomMember> q=dsForRW.createQuery(LiveRoomMember.class);
		q.filter("userId", userId);
		LiveRoom liveRoom=get(roomId);
		UpdateOperations<LiveRoomMember> ops = dsForRW.createUpdateOperations(LiveRoomMember.class);
		ops.set("type", type);
		dsForRW.update(q, ops);
		//xmpp推送
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.SETADMIN);
		if(type==2){//1为设置管理员
			messageBean.setContent(1);
		}else{
			messageBean.setContent(0);
		}
		messageBean.setToUserName(q.get().getNickName());
		messageBean.setToUserId(q.get().getUserId().toString());
		messageBean.setObjectId(liveRoom.getJid());
		try {
			List<Integer> userIdlist=findMembersUserIds(roomId);
			KXMPPServiceImpl.getInstance().send(userIdlist,messageBean.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	//点赞
	public void addpraise(ObjectId roomId){
		LiveRoom liveRoom=get(roomId);
		//xmpp消息
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.LIVEPRAISE);
		messageBean.setObjectId(liveRoom.getJid());
		try {
			List<Integer> userIdlist=findMembersUserIds(roomId);
			KXMPPServiceImpl.getInstance().send(userIdlist,messageBean.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
