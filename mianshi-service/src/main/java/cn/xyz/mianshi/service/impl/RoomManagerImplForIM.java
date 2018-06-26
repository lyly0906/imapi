package cn.xyz.mianshi.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import cn.xyz.mianshi.vo.*;
import cn.xyz.repository.FriendsRepository;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cn.xyz.commons.support.Callback;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Room.Member;
import cn.xyz.mianshi.vo.Room.Notice;
import cn.xyz.mianshi.vo.Room.Share;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

@Service(RoomManager.BEAN_ID)
public class RoomManagerImplForIM implements RoomManager {

	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Autowired
	private UserManager userManager;
	@Autowired
	private FriendsRepository friendsRepository;
	int num=300000;
	int voide=350000;
	@Override
	public Room add(User user, Room example, List<Integer> memberUserIdList) {
		num=num++;
		if(num>349999){
			num=300000;
		}
		voide=voide++;
		if(voide>399999){
			voide=350000;
		}
		user.setNum(user.getNum()+1);
		Room entity = new Room();
		entity.setId(ObjectId.get());
		entity.setJid(example.getJid());
		entity.setName(example.getName());// 必须
		entity.setDesc(example.getDesc());// 必须
		entity.setShowRead(example.getShowRead());
		/*entity.setCall("0"+user.getUserId()+user.getNum());*/
		entity.setCall(String.valueOf(num));
		entity.setVideoMeetingNo(String.valueOf(voide));
		entity.setSubject("");
		entity.setCategory(0);
		entity.setTags(Lists.newArrayList());
		entity.setNotice(new Room.Notice());
		entity.setNotices(Lists.newArrayList());
		entity.setUserSize(0);
		// entity.setMaxUserSize(1000);
		entity.setMembers(Lists.newArrayList());
		entity.setCountryId(example.getCountryId());// 必须
		entity.setProvinceId(example.getProvinceId());// 必须
		entity.setCityId(example.getCityId());// 必须
		entity.setAreaId(example.getAreaId());// 必须
		entity.setLongitude(example.getLongitude());// 必须
		entity.setLatitude(example.getLatitude());// 必须
		entity.setUserId(user.getUserId());
		entity.setNickname(user.getNickname());
		entity.setCreateTime(DateUtil.currentTimeSeconds());
		entity.setModifier(user.getUserId());
		entity.setModifyTime(entity.getCreateTime());
		entity.setS(1);
		
		if (null == entity.getName())
			entity.setName("我的群组");
		if (null == entity.getDesc())
			entity.setDesc("");
		if (null == entity.getCountryId())
			entity.setCountryId(0);
		if (null == entity.getProvinceId())
			entity.setProvinceId(0);
		if (null == entity.getCityId())
			entity.setCityId(0);
		if (null == entity.getAreaId())
			entity.setAreaId(0);
		if (null == entity.getLongitude())
			entity.setLongitude(0d);
		if (null == entity.getLatitude())
			entity.setLatitude(0d);

		// 保存房间配置
		dsForTigase.save(entity);
		dsForTigase.save(user);
		
		// 创建者
		Member member = new Member();
		member.setActive(DateUtil.currentTimeSeconds());
		member.setCreateTime(member.getActive());
		member.setModifyTime(0L);
		member.setNickname(user.getNickname());
		member.setRole(1);
		member.setRoomId(entity.getId());
		member.setSub(1);
		member.setTalkTime(0L);
		member.setUserId(user.getUserId());
		// 初试成员列表
		List<Member> memberList = Lists.newArrayList(member);
		// 初试成员列表不为空
		if (null != memberUserIdList && !memberUserIdList.isEmpty()) {
			Long currentTimeSeconds = DateUtil.currentTimeSeconds();
			ObjectId roomId = entity.getId();
			for (int userId : memberUserIdList) {
				User _user = userManager.getUser(userId);
				// 成员
				Member _member = new Member();
				_member.setActive(currentTimeSeconds);
				_member.setCreateTime(currentTimeSeconds);
				_member.setModifyTime(0L);
				_member.setNickname(_user.getNickname());
				_member.setRole(3);
				_member.setRoomId(roomId);
				_member.setSub(1);
				_member.setTalkTime(0L);
				_member.setUserId(_user.getUserId());

				memberList.add(_member);
				//xmpp推送
				MessageBean messageBean=new MessageBean();
				messageBean.setType(KXMPPServiceImpl.NEW_MEMBER);
				messageBean.setFromUserId(user.getUserId().toString());
				messageBean.setFromUserName(user.getNickname());
				messageBean.setToUserId(_user.getUserId().toString());
				messageBean.setFileSize(entity.getShowRead());
				messageBean.setContent(entity.getName());
				messageBean.setToUserName(_user.getNickname());
				messageBean.setFileName(entity.getId().toString());
				messageBean.setObjectId(example.getJid());
				System.out.println("打印xmpp推送内容"+messageBean.toString());
				try {
					KXMPPServiceImpl.getInstance().send(_user.getUserId(), messageBean.toString());
					//群主把某人邀请进群的记录
					if(user.getUserId() != _user.getUserId()) {
						friendsRepository.saveNewFriends(new NewFriends(_user.getUserId(), user.getUserId(), user.getNickname(), 1, 907, 1, user.getNickname() + "把您邀请进" + example.getName()));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		// 保存成员列表
		dsForTigase.save(memberList);

		updateUserSize(entity.getId(), memberList.size());

		return entity;
	}

	@Override
	public void delete(User user, ObjectId roomId) {
		// IMPORTANT 1-3、删房间推送-已改
		
		Query<Room> query = dsForTigase.createQuery(Room.class).field("_id").equal(roomId);
		Room room =query.get();
		MessageBean messageBean = new MessageBean();
		messageBean.setFromUserId(user.getUserId() + "");
		messageBean.setFromUserName(user.getNickname());
		messageBean.setType(KXMPPServiceImpl.DELETE_ROOM);
		// messageBean.setObjectId(room.getId().toString());
		messageBean.setObjectId(room.getJid());
		messageBean.setContent(room.getName());

		try {
			KXMPPServiceImpl.getInstance().send(getMemberIdList(room, user.getUserId()), messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ThreadUtil.executeInThread(new Callback() {
			@Override
			public void execute(Object obj) {
				//删除群组 清除 群组成员
				Query<Member> merQuery = dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId);
				dsForTigase.delete(merQuery);
				dsForTigase.delete(query);
			}
		});
		
		
	}

	@Override
	public boolean update(User user, ObjectId roomId, String roomName, String notice, String desc,int showRead) {
		boolean result=true;
		BasicDBObject q = new BasicDBObject("_id", roomId);
		BasicDBObject o = new BasicDBObject();
		BasicDBObject values = new BasicDBObject();
		Room room = get(roomId);		
		if (!StringUtil.isEmpty(roomName)&&(!room.getName().equals(roomName)||exisname(roomName)==null)) {
			
			// o.put("$set", new BasicDBObject("name", roomName));
			values.put("name", roomName);

			// IMPORTANT 1-2、改房间名推送-已改
			MessageBean messageBean = new MessageBean();
			messageBean.setFromUserId(user.getUserId() + "");
			messageBean.setFromUserName(user.getNickname());
			messageBean.setType(KXMPPServiceImpl.CHANGE_ROOM_NAME);
			// messageBean.setObjectId(roomId.toString());
			messageBean.setObjectId(room.getJid());
			messageBean.setContent(roomName);
			try {
				KXMPPServiceImpl.getInstance().send(getMemberIdList(room, 0), messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			values.put("name", room.getName());
			result=StringUtil.isEmpty(roomName)==false?false:true;
		}
		if (!StringUtil.isEmpty(desc)) {
			// o.put("$set", new BasicDBObject("desc", desc));
			values.put("desc", desc);
		}
		if (!StringUtil.isEmpty(notice)) {
			BasicDBObject dbObj = new BasicDBObject();
			dbObj.put("roomId", roomId);
			dbObj.put("text", notice);
			dbObj.put("userId", user.getUserId());
			dbObj.put("nickname", user.getNickname());
			dbObj.put("time", DateUtil.currentTimeSeconds());

			// 更新最新公告
			// o.put("$set", new BasicDBObject("notice", dbObj));
			values.put("notice", dbObj);

			// 新增历史公告记录
			dsForTigase.getCollection(Room.Notice.class).save(dbObj);

			// IMPORTANT 1-5、改公告推送-已改
			MessageBean messageBean = new MessageBean();
			messageBean.setFromUserId(user.getUserId() + "");
			messageBean.setFromUserName(user.getNickname());
			messageBean.setType(KXMPPServiceImpl.NEW_NOTICE);
			// messageBean.setObjectId(roomId.toString());
			messageBean.setObjectId(room.getJid());
			messageBean.setContent(notice);
			try {
				KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0), messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(-1<showRead&&room.getShowRead()!=showRead){
			values.append("showRead", showRead);
			MessageBean messageBean=new MessageBean();
			messageBean.setType(KXMPPServiceImpl.SHOWREAD);
			messageBean.setFromUserId(user.getUserId().toString());
			messageBean.setFromUserName(user.getNickname());
			messageBean.setContent(String.valueOf(showRead));
			messageBean.setObjectId(room.getJid());
			try {
				KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0),messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		o.put("$set", values);
		dsForTigase.getCollection(Room.class).update(q, o);
		return result;
	}

	@Override
	public Room get(ObjectId roomId) {
		Room room = dsForTigase.createQuery(Room.class).field("_id").equal(roomId).get();

		if (null != room) {
			// Member member;
			List<Member> members = dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId).order("role").order("createTime").asList();
			List<Notice> notices = dsForTigase.createQuery(Room.Notice.class).field("roomId").equal(roomId).asList();

			// room.setMember(member);
			room.setMembers(members);
			room.setNotices(notices);
		}

		return room;
	}

	@Override
	public List<Room> selectList(int pageIndex, int pageSize, String roomName) {
		Query<Room> q = dsForTigase.createQuery(Room.class);
		if (!StringUtil.isEmpty(roomName)){
			//q.field("name").contains(roomName);
			q.or(q.criteria("name").containsIgnoreCase(roomName),
					q.criteria("desc").containsIgnoreCase(roomName));
		}
			
		List<Room> roomList = q.offset(pageIndex * pageSize).limit(pageSize).order("-_id").asList();
		return roomList;
	}

	@Override
	public Object selectHistoryList(int userId, int type) {
		List<Object> historyIdList = Lists.newArrayList();

		Query<Room.Member> q = dsForTigase.createQuery(Room.Member.class).field("userId").equal(userId);
		if (1 == type) {// 自己的房间
			q.filter("role =", 1);
		} else if (2 == type) {// 加入的房间
			q.filter("role !=", 1);
		}
		DBCursor cursor = dsForTigase.getCollection(Room.Member.class).find(q.getQueryObject(),
				new BasicDBObject("roomId", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			historyIdList.add(dbObj.get("roomId"));
		}

		if (historyIdList.isEmpty())
			return null;

		List<Room> historyList = dsForTigase.createQuery(Room.class).field("_id").in(historyIdList).order("-_id")
				.asList();
		historyList.forEach(room -> {
			Member member = dsForTigase.createQuery(Room.Member.class).field("roomId").equal(room.getId())
					.field("userId").equal(userId).get();
			room.setMember(member);
		});

		return historyList;
	}

	@Override
	public Object selectHistoryList(int userId, int type, int pageIndex, int pageSize) {
		List<Object> historyIdList = Lists.newArrayList();

		Query<Room.Member> q = dsForTigase.createQuery(Room.Member.class).field("userId").equal(userId);
		if (1 == type) {// 自己的房间
			q.filter("role =", 1);
		} else if (2 == type) {// 加入的房间
			q.filter("role !=", 1);
		}
		DBCursor cursor = dsForTigase.getCollection(Room.Member.class).find(q.getQueryObject(),
				new BasicDBObject("roomId", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			historyIdList.add(dbObj.get("roomId"));
		}

		if (historyIdList.isEmpty())
			return null;

		List<Room> historyList = dsForTigase.createQuery(Room.class).field("_id").in(historyIdList).order("-_id")
				.offset(pageIndex * pageSize).limit(pageSize).asList();
		historyList.forEach(room -> {
			Member member = dsForTigase.createQuery(Room.Member.class).field("roomId").equal(room.getId())
					.field("userId").equal(userId).get();
			room.setMember(member);
		});

		return historyList;
	}

	@Override
	public void deleteMember(User user, ObjectId roomId, int userId) {
		Room room = get(roomId);
		User toUser = userManager.getUser(userId);

		// IMPORTANT 1-4、删除成员推送-已改
		MessageBean messageBean = new MessageBean();
		messageBean.setFromUserId(user.getUserId() + "");
		messageBean.setFromUserName(user.getNickname());
		messageBean.setType(KXMPPServiceImpl.DELETE_MEMBER);
		// messageBean.setObjectId(roomId.toString());
		messageBean.setObjectId(room.getJid());
		messageBean.setToUserId(userId + "");
		messageBean.setToUserName(toUser.getNickname());
		try {
			KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, user.getUserId()), messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Query<Room.Member> q = dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId).field("userId")
				.equal(userId);
		dsForTigase.delete(q);
		//群主把某人踢出群的记录
		if(user.getUserId() != userId){
			friendsRepository.saveNewFriends(new NewFriends(userId,user.getUserId(),user.getNickname(),1,904,1,user.getNickname()+"把您踢出"+room.getName()));
		}

		updateUserSize(roomId, -1);
	}

	@Override
	public void updateMember(User user, ObjectId roomId, List<Integer> userIdList) {
		for (int userId : userIdList) {
			Member _member = new Member();
			_member.setUserId(userId);
			_member.setRole(3);
			updateMember(user, roomId, _member);
		}
	}

	@Override
	public void updateMember(User user, ObjectId roomId, Member member) {
		DBCollection dbCollection = dsForTigase.getCollection(Room.Member.class);
		DBObject q = new BasicDBObject().append("roomId", roomId).append("userId", member.getUserId());
		Room room = get(roomId);
		User toUser = userManager.getUser(member.getUserId());

		if (1 == dbCollection.count(q)) {
			BasicDBObject values = new BasicDBObject();
			if (null != member.getRole())
				values.append("role", member.getRole());
			if (null != member.getSub())
				values.append("sub", member.getSub());
			if (null != member.getTalkTime())
				values.append("talkTime", member.getTalkTime());
			if (!StringUtil.isEmpty(member.getNickname()))
				values.append("nickname", member.getNickname());
			values.append("modifyTime", DateUtil.currentTimeSeconds());
			values.append("call", room.getCall());

			// 更新成员信息
			dbCollection.update(q, new BasicDBObject("$set", values));

			if (!StringUtil.isEmpty(member.getNickname()) && !toUser.getNickname().equals(member.getNickname())) {
				// IMPORTANT 1-1、改昵称推送-已改
				MessageBean messageBean = new MessageBean();
				messageBean.setType(KXMPPServiceImpl.CHANGE_NICK_NAME);
				// messageBean.setObjectId(roomId.toString());
				messageBean.setObjectId(room.getJid());
				messageBean.setFromUserId(user.getUserId() + "");
				messageBean.setFromUserName(user.getNickname());
				messageBean.setToUserId(toUser.getUserId() + "");
				messageBean.setToUserName(toUser.getNickname());
				messageBean.setContent(member.getNickname());
				try {
					KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0), messageBean.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != member.getTalkTime()) {
				// IMPORTANT 1-6、禁言
				MessageBean messageBean = new MessageBean();
				messageBean.setType(KXMPPServiceImpl.GAG);
				// messageBean.setObjectId(roomId.toString());
				messageBean.setObjectId(room.getJid());
				messageBean.setFromUserId(user.getUserId() + "");
				messageBean.setFromUserName(user.getNickname());
				messageBean.setToUserId(toUser.getUserId() + "");
				messageBean.setToUserName(toUser.getNickname());
				messageBean.setContent(member.getTalkTime() + "");
				try {
					KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0), messageBean.toString());
					//群主把某人禁言群的记录
					if(user.getUserId() != toUser.getUserId()) {
						friendsRepository.saveNewFriends(new NewFriends(toUser.getUserId(), user.getUserId(), user.getNickname(), 1, 906, 1, user.getNickname() + "把您在" + room.getName() + "设置为禁言"));
					}


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			User _user = userManager.getUser(member.getUserId());
			Member _member = new Member();
			_member.setActive(DateUtil.currentTimeSeconds());
			_member.setCreateTime(_member.getActive());
			_member.setModifyTime(0L);
			_member.setNickname(_user.getNickname());
			_member.setRole(member.getRole());
			_member.setRoomId(roomId);
			_member.setSub(1);
			_member.setTalkTime(0L);
			_member.setUserId(_user.getUserId());

			dsForTigase.save(_member);
			
			updateUserSize(roomId, 1);

			// IMPORTANT 1-7、新增成员
			MessageBean messageBean = new MessageBean();
			messageBean.setType(KXMPPServiceImpl.NEW_MEMBER);
			// messageBean.setObjectId(roomId.toString());
			messageBean.setObjectId(room.getJid());
			messageBean.setFromUserId(user.getUserId() + "");
			messageBean.setFromUserName(user.getNickname());
			messageBean.setToUserId(toUser.getUserId() + "");
			messageBean.setToUserName(toUser.getNickname());
			messageBean.setFileSize(room.getShowRead());
			messageBean.setContent(room.getName());
			messageBean.setFileName(room.getId().toString());
			try {
				
				KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0), messageBean.toString());

				//群主把某人邀请进群的记录
				if(user.getUserId() != toUser.getUserId()) {
					friendsRepository.saveNewFriends(new NewFriends(toUser.getUserId(), user.getUserId(), user.getNickname(), 1, 907, 1, user.getNickname() + "把您邀请进" + room.getName()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Member getMember(ObjectId roomId, int userId) {
		return dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId).field("userId").equal(userId)
				.get();
	}

	@Override
	public List<Member> getMemberList(ObjectId roomId,String keyword) {
		List<Member>list=null;
		Query<Member> query = dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId);
		if(!StringUtil.isEmpty(keyword))
			query.field("nickname").containsIgnoreCase(keyword);
		list=query.order("role").order("createTime").asList();
		/*for (Member member : list) {
			System.out.println(member.getNickname());
		}*/
		return list;
	}
	public Object getMemberListByPage(ObjectId roomId,int pageIndex,int pageSize) {
		Query<Room.Member> q=dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId);
		List<Member> pageData=q.offset(pageIndex * pageSize).limit(pageSize).asList();
		for (Member member : pageData) {
			System.out.println(member.getNickname());
		}
		long total=q.countAll();
		return new PageVO(pageData, total, pageIndex, pageSize);
	}
	

	@Override
	public void join(int userId, ObjectId roomId, int type) {
		Member member = new Member();
		member.setUserId(userId);
		member.setRole(1 == type ? 1 : 3);
		updateMember(userManager.getUser(userId), roomId, member);
	}

	private void updateUserSize(ObjectId roomId, int userSize) {
		DBObject q = new BasicDBObject("_id", roomId);
		DBObject o = new BasicDBObject("$inc", new BasicDBObject("userSize", userSize));
		dsForTigase.getCollection(Room.class).update(q, o);
	}

	/**
	 * 获取房间成员列表
	 * 
	 * @param roomId
	 * @param userId
	 * @return
	 */
	private List<Integer> getMemberIdList(ObjectId roomId, int userId) {
		List<Integer> userIdList = Lists.newArrayList();

		Query<Room.Member> q = dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId);
		DBCursor cursor = dsForTigase.getCollection(Room.Member.class).find(q.getQueryObject(),
				new BasicDBObject("userId", 1));

		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			int _userId = dbObj.getInt("userId");
			//if (_userId != userId)
				userIdList.add(_userId);
		}

		return userIdList;
	}

	/**
	 * 获取房间成员列表
	 *
	 * @param roomId
	 * @param userId
	 * @return
	 */
	public String getRoomList(String jid) {

		Room room = dsForTigase.createQuery(Room.class).field("jid").equal(jid).get();

		if (null != room) {
			return room.getId().toString();
		}
		return null;
	}

	private List<Integer> getMemberIdList(Room room, int userId) {
		return getMemberIdList(room.getId(), userId);
	}

	@Override
	public void leave(int userId, ObjectId roomId) {

	}

	@Override
	public Room exisname(Object roomname) {
		Room room = dsForTigase.createQuery(Room.class).field("name").equal(roomname).get();
		if (null != room) {
			return room;
		}
		return null;
	}

	@Override
	public void delete(ObjectId roomId) {
		Room room = get(roomId);
		
		MessageBean messageBean = new MessageBean();
		messageBean.setType(KXMPPServiceImpl.DELETE_ROOM);
		// messageBean.setObjectId(room.getId().toString());
		messageBean.setObjectId(room.getJid());
		messageBean.setContent(room.getName());
		List<Integer>list=new ArrayList<>();
		for (Room.Member member: getMemberList(roomId,null)) {
			list.add(member.getUserId());
		}
		try {
			KXMPPServiceImpl.getInstance().send(list, messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ThreadUtil.executeInThread(new Callback() {
			@Override
			public void execute(Object obj) {
				//删除群组 清除 群组成员
				Query<Member> merQuery = dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId);
				dsForTigase.delete(merQuery);
				
			}
		});
		
	}
	//设置/取消管理员
	@Override
	public void setAdmin(ObjectId roomId, int touserId,int type,int userId) {
		Query<Room.Member> q=dsForTigase.createQuery(Room.Member.class).field("roomId").equal(roomId).field("userId").equal(touserId);
		UpdateOperations<Room.Member> ops=dsForTigase.createUpdateOperations(Room.Member.class);
		ops.set("role", type);
		dsForTigase.findAndModify(q, ops);
		Room room=get(roomId);
		User user = userManager.getUser(userId);
		//xmpp推送
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.SETADMIN);
		if(type==2){//1为设置管理员
			messageBean.setContent(1);
		}else{
			messageBean.setContent(0);
		}
		messageBean.setFromUserId(user.getUserId().toString());
		messageBean.setFromUserName(user.getNickname());
		messageBean.setToUserName(q.get().getNickname());
		messageBean.setToUserId(q.get().getUserId().toString());
		messageBean.setObjectId(room.getJid());
		try {
			List<Integer>list=new ArrayList<>();
			for (Room.Member member: getMemberList(roomId,null)) {
				list.add(member.getUserId());
			}
			KXMPPServiceImpl.getInstance().send(list,messageBean.toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//添加文件（群共享）
	@Override
	public Share Addshare(ObjectId roomId,long size, int type,int userId, String url,String name) {
		User user = userManager.getUser(userId);
		Share share=new Share();
		share.setRoomId(roomId);
		share.setTime(DateUtil.currentTimeSeconds());
		share.setNickname(user.getNickname());
		share.setUserId(userId);
		share.setSize(size);
		share.setUrl(url);
		share.setType(type);
		share.setName(name);
		dsForRW.save(share);
		Room room=get(roomId);
		//上传文件xmpp推送
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.FILEUPLOAD);
		messageBean.setContent(share.getShareId().toString());
		messageBean.setFileName(share.getName());
		messageBean.setObjectId(room.getJid());
		messageBean.setFromUserId(user.getUserId().toString());
		messageBean.setFromUserName(user.getNickname());
		try {
			
			KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0), messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return share;
	}
	
	//查询所有
	@Override
	public List<Share> findShare(ObjectId roomId, long time, int userId, int pageIndex, int pageSize) {
/*		List<Share> list=dsForTigase.createQuery(Room.Share.class).field("roomId").equal(roomId).asList();*/
		Query<Room.Share> q=dsForRW.createQuery(Room.Share.class).field("roomId").equal(roomId);
		if(time!=0L){
			q.filter("time", time);
		}else if(userId!=0){
			q.filter("userId", userId);
		}
		
		List<Share> list=new ArrayList<Share>();
		list=q.offset(pageSize*pageIndex).limit(pageSize).asList();
		
		return list;
	}
	
	//删除
	@Override
	public void deleteShare(ObjectId roomId, ObjectId shareId,int userId) {
		Query<Room.Share> q=dsForRW.createQuery(Room.Share.class).field("roomId").equal(roomId).field("shareId").equal(shareId);
		
		User user = userManager.getUser(userId);
		Room room=get(roomId);
		//删除XMpp推送
		MessageBean messageBean=new MessageBean();
		messageBean.setType(KXMPPServiceImpl.DELETEFILE);
		messageBean.setContent(q.get().getShareId());
		messageBean.setFileName(q.get().getName());
		messageBean.setObjectId(room.getJid());
		messageBean.setFromUserId(user.getUserId().toString());
		messageBean.setFromUserName(user.getNickname());
		try {
			KXMPPServiceImpl.getInstance().send(getMemberIdList(roomId, 0), messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		dsForRW.delete(q);
	}
	//获取单个文件
	@Override
	public Object getShare(ObjectId roomId, ObjectId shareId) {
		Share share=dsForRW.createQuery(Room.Share.class).field("roomId").equal(roomId).field("shareId").equal(shareId).get();
		return share;
	}

	@Override
	public String getCall(ObjectId roomId) {
		Room room = dsForTigase.createQuery(Room.class).field("_id").equal(roomId).get();
		return room.getCall();
	}
}
