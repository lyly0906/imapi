package com.shiku.mianshi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.NewFriends;
import cn.xyz.repository.FriendsRepository;
import com.shiku.mianshi.ListUtils;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.DES;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.BaiduPushService;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

/**
 * Tigase支持接口
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/tigase")
public class TigaseController extends AbstractController {

	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;
	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Autowired
	private FriendsManager friendsManager;
	@Autowired
	private UserManagerImpl userManager;
	@Autowired
	private FriendsRepository friendsRepository;
	@Autowired
	private UserManager userService;

	//单聊聊天记录
	@RequestMapping("/shiku_msgs")
	public JSONMessage getMsgs(@RequestParam int receiver, @RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime, @RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "20") int pageSize,@RequestParam(defaultValue = "100") int maxType) {
		int sender = ReqUtil.getUserId();
		DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
		BasicDBObject query11 = new BasicDBObject();
		query11.put("sender", sender);
		BasicDBObject query12 = new BasicDBObject();
		query12.put("receiver", receiver);


		List<BasicDBObject> orQueryList1 = new ArrayList<BasicDBObject>();
		orQueryList1.add(query11);
		orQueryList1.add(query12);

		BasicDBObject orQuery1 = new BasicDBObject("$and", orQueryList1);


		BasicDBObject query21 = new BasicDBObject();
		query21.put("receiver", sender);
		BasicDBObject query22 = new BasicDBObject();
		query22.put("sender", receiver);

		List<BasicDBObject> orQueryList2 = new ArrayList<BasicDBObject>();
		orQueryList2.add(query21);
		orQueryList2.add(query22);

		BasicDBObject orQuery2 = new BasicDBObject("$and", orQueryList2);

		//q.put("sender", sender);
		//q.put("receiver", sender);
		BasicDBObject q = new BasicDBObject();
		//System.out.println(sender+"|"+receiver);
		if(maxType>0)
			q.put("contentType",new BasicDBObject(MongoOperator.LT, maxType));
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));///待改

		List<BasicDBObject> orQueryCombinationList = new ArrayList<BasicDBObject>();


		orQueryCombinationList.add(orQuery1);
		orQueryCombinationList.add(orQuery2);

		java.util.List<DBObject> list = Lists.newArrayList();

		BasicDBObject finalQuery = new BasicDBObject("$or",
				orQueryCombinationList);

		List<BasicDBObject> orQueryCombinationList1 = new ArrayList<BasicDBObject>();

		orQueryCombinationList1.add(finalQuery);
		orQueryCombinationList1.add(q);

		BasicDBObject finalQuery1 = new BasicDBObject("$and",
				orQueryCombinationList1);
        System.out.println("finalQuery1:"+finalQuery1.toString());
		DBCursor cursor = dbCollection.find(finalQuery1).sort(new BasicDBObject("timeSend", -1)).sort(new BasicDBObject("ts", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		//-----------------------------------------------------
//		q.put("sender",receiver);
//		q.put("receiver",sender);
//		if (0 != startTime)
//			q.put("ts", new BasicDBObject("$gte", startTime));
//		if (0 != endTime)
//			q.put("ts", new BasicDBObject("$lte", endTime));
//
//		java.util.List<DBObject> list1 = Lists.newArrayList();
//
//		DBCursor cursor1 = dbCollection.find(q).sort(new BasicDBObject("ts", -1)).skip(pageIndex * pageSize)
//				.limit(pageSize);
//		while (cursor1.hasNext()) {
//			list.add(cursor1.next());
//		}
//		//----------------------------------------------------------
//		//String s=receiver+","+startTime+","+endTime;
//		//Collections.reverse(list);//倒序*/
//
//		ListUtils.sort(list,false,"timeSend");
		//System.out.println(list);
		return JSONMessage.success("", list);
		
	}
	//漫游
	/*@RequestMapping("/shiku_history")
	public JSONMessage getHistory(){
		int from=ReqUtil.getUserId();
		DBCollection dbCollection=dsForTigase.getDB().getCollection("shiku_history");
		BasicDBObject q=new BasicDBObject();
		q.put("form",from);
		
		return null;
	}*/
	//群组聊天记录
	@RequestMapping("/shiku_muc_msgs")
	public JSONMessage getMucMsgs(@RequestParam String roomId, @RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime, @RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "20") int pageSize,@RequestParam(defaultValue = "100") int maxType) {

		DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_muc_msgs");
		BasicDBObject q = new BasicDBObject();
		q.put("room_jid_id", roomId);
		if(maxType>0)
			q.put("contentType",new BasicDBObject(MongoOperator.LT, maxType));
		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));

		java.util.List<DBObject> list = Lists.newArrayList();

		DBCursor cursor = dbCollection.find(q).sort(new BasicDBObject("timeSend", -1)).sort(new BasicDBObject("ts", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		/*Collections.reverse(list);//倒序*/	
		return JSONMessage.success("", list);
	}

	@RequestMapping(value = "/notify")
	public JSONMessage notify(@RequestParam Long from, @RequestParam Long to, @RequestParam String body,
			@RequestParam(defaultValue="0") long ts) {
		try {
			String c = new String(body.getBytes("iso8859-1"),"utf-8");
			System.out.println(c);
			//判断用户是否开启消息免打扰
			User user=dsForRW.createQuery(User.class).filter("_id", to).get();
			if(user.getOfflineNoPushMsg()==1){
				return null;
			}
			//判断用户是否对好友设置了消息免打扰
			Friends friends=dsForRW.createQuery(Friends.class).field("userId").equal(to).field("toUserId").equal(from).get();
			if(friends!=null&&friends.getOfflineNoPushMsg()==1){
				return null;
			}
			
			String key1 = String.format("user:%s:channelId", to);
			String key2 = String.format("user:%s:deviceId", to);
			String channelId = jedisTemplate.get(key1);
			String deviceId = jedisTemplate.get(key2);
			if (null != deviceId && null != channelId) {
				String key3 = String.format("channelId:%s", channelId);
				String toUserId = jedisTemplate.get(key3);
				// channeId没有设置对应的userId或者对应的userId等于消息接收方
				if (!StringUtils.hasText(toUserId) || Integer.parseInt(toUserId) == to) {
					int messageType = 0;
					String text = "";
					String fromUserName = "";
					try {
						JSONObject jsonObj = JSON
								.parseObject(body);
						messageType = jsonObj.getInteger("type");
						fromUserName = jsonObj.getString("fromUserName");
						
						if (1 == messageType) {
							text = jsonObj.getString("content");
							if (StringUtils.hasText(text)) {
								if (text.length() > 20)
									text = text.substring(0, 20) + "...";
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					try {
						if(0==ts)
							ts=System.currentTimeMillis();
						BaiduPushService.PushMessage msg = new BaiduPushService.PushMessage(from, fromUserName, to, text,
								messageType, ts);
						String appId=userManager.get(new Integer(to.toString())).getAppId();
						BaiduPushService.pushSingle(Integer.parseInt(deviceId), channelId, msg,appId);
					} catch (NumberFormatException e) {
						jedisTemplate.del(key2);
					}
					
					return JSONMessage.success();
				} else {
					return JSONMessage.success();
				}
			} else {
				System.out.println("离线推送：未发现匹配的与用户匹配的推送通道Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.failure("推送失败");
	}
	@RequestMapping(value = "/baiduPush")
	public JSONMessage baiduPush(@RequestParam Integer from, @RequestParam Integer to,@RequestParam(defaultValue="1") int type, @RequestParam String content) {
		try {
			System.out.println("百度推送");
			//String c = new String(body.getBytes("iso8859-1"),"utf-8");
			String key1 = String.format("user:%s:channelId", to);
			String key2 = String.format("user:%s:deviceId", to);
			String channelId = jedisTemplate.get(key1);

			String deviceId = jedisTemplate.get(key2);
			System.out.println(channelId+"|"+deviceId);
			if (null != deviceId && null != channelId) {
				String key3 = String.format("channelId:%s", channelId);
				String toUserId = jedisTemplate.get(key3);
				System.out.println(key3+"|"+toUserId);
				// channeId没有设置对应的userId或者对应的userId等于消息接收方
				if (!StringUtils.hasText(toUserId) || Integer.parseInt(toUserId) == to) {
					System.out.println(channelId);
					String fromUserName = "";
					fromUserName=userManager.getNickName(from);
					
					
					BaiduPushService.PushMessage msg = new BaiduPushService.PushMessage();
					msg.setTime(System.currentTimeMillis());
					msg.setUserId(from);
					msg.setToUserId(to);
					msg.setTitle(fromUserName);
					msg.setType(type);
					msg.setDescription(fromUserName+":" +content);
					
					String appId=userManager.get(new Integer(to.toString())).getAppId();
					BaiduPushService.pushSingle(Integer.parseInt(deviceId), channelId, msg,appId);
					return JSONMessage.success();
				} else {
					return JSONMessage.success();
				}
			} else {
				System.out.println("离线推送：未发现匹配的与用户匹配的推送通道Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.failure("推送失败");
	}
	
	@RequestMapping(value = "/push")
	public JSONMessage push(@RequestParam String text, @RequestParam String body) {
		System.out.println("push");
		List<Integer> userIdList = JSON.parseArray(text, Integer.class);
		try {
			//String c = new String(body.getBytes("iso8859-1"),"utf-8");
			KXMPPServiceImpl.getInstance().send(userIdList, body);
			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.failure("推送失败");
		// {userId:%1$s,toUserIdList:%2$s,body:'%3$s'}
	}
	
	@RequestMapping(value = "/OnlineState")
	public JSONMessage OnlineState(@RequestParam long userId, @RequestParam int OnlineState) {		
		List<Fans> data = friendsManager.getFansList((int)userId);
		List<Integer>userlist=new ArrayList<>();
		MessageBean messageBean = new MessageBean();
		messageBean.setFromUserId(Long.toString(userId));
		for (Fans fans : data) {
			userlist.add(fans.getToUserId());
		}
		if (OnlineState==0) {
			messageBean.setType(KXMPPServiceImpl.OFFLINE);
		}else{
			messageBean.setType(KXMPPServiceImpl.ONLINE);
		}
		try {
			KXMPPServiceImpl.getInstance().send(userlist, messageBean.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.failure("推送失败");
	}
	//加密
	@RequestMapping(value = "/encrypt")
	public JSONMessage encrypt(@RequestParam String text, @RequestParam String key) {		
		
		Map<String,String> map=Maps.newConcurrentMap();
		try {
			text=DES.encryptDES(text, key);
			map.put("text", text);
			return JSONMessage.success(null, map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JSONMessage.failure("推送失败");
	}
	//解密
	@RequestMapping(value = "/decrypt")
	public JSONMessage decrypt(@RequestParam String text, @RequestParam String key) {		
		
		Map<String,String> map=Maps.newConcurrentMap();
		String content=null;
		try {
			content=DES.decryptDES(text, key);
			map.put("text", content);
			return JSONMessage.success(null, map);
		}catch (StringIndexOutOfBoundsException e) {
			//没有加密的 消息
			map.put("text", text);
			return JSONMessage.success(null, map);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JSONMessage.failure("推送失败");
	}

	//	获取消息接口(阅后即焚)
	//type 1 单聊  2 群聊
	@RequestMapping("/getMessage")
	public JSONMessage getMessage(@RequestParam(defaultValue="1") int type,@RequestParam String messageId) throws Exception{
		DBCollection dbCollection=null;
		if(type==1)
			 dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
		else 
			 dbCollection = dsForTigase.getDB().getCollection("shiku_muc_msgs");
			
			BasicDBObject query = new BasicDBObject();
			query.put("messageId",messageId); 
			Object data=dbCollection.findOne(query);
		
		return JSONMessage.success(null, data);
		
	}
	
	//	删除消息接口
	@RequestMapping("/deleteMsg")
	//type 1 单聊  2 群聊
	//delete 1  删除属于自己的消息记录 2：撤回 删除 整条消息记录
	public JSONMessage deleteMsg(@RequestParam(defaultValue="1") int type,@RequestParam(defaultValue="1") int delete,@RequestParam String messageId) throws Exception{
		int sender = ReqUtil.getUserId();
		DBCursor cursor = null;
		DBCollection dbCollection=null;
		try{
			if(type==1)
				 dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
			else 
				 dbCollection = dsForTigase.getDB().getCollection("shiku_muc_msgs");
			BasicDBObject query = new BasicDBObject();
			
			
			query.put("messageId",new BasicDBObject(MongoOperator.IN, messageId.split(","))); 
			if(2==delete)
				query.put("sender", sender);
			cursor = dbCollection.find(query);
			if(cursor.size()>0){
				
				BasicDBObject dbObj = (BasicDBObject) cursor.next();
				//解析消息体
				
				Map<String,Object> body = JSON.parseObject(dbObj.getString("body").replace("&quot;", "\""), Map.class);
				int contentType = (int) body.get("type"); 
				dbCollection.remove(query); //将消息记录中的数据删除	
				
					/**
					Type = 1,//文本
				    Type = 2,//图片
				    Type = 3,//语音
				    Type=4, //位置
				    Type=5,//动画
				    Type=6,//视频
				    Type=7,//音频
				    Type=8,//名片
				    Type=9, //文件
				    Type=10, //提醒
					 */
					if(contentType==2 || contentType==3 || contentType==5 || contentType==6 || contentType==7 || contentType==9){
						String paths = (String) body.get("content");
						//调用删除方法将文件从服务器删除
						ConstantUtil.deleteFile(paths);
					}
				}
				
			} catch (Exception e){
				e.printStackTrace();
			}finally {
				if(cursor != null) cursor.close(); 
			}
		
		return JSONMessage.success();
		
	}
	
	
//	//修改消息的已读状态
//	public void modifyIsRead(String messageId) {
//		BasicDBObject dbObj = new BasicDBObject(9);
//		dbObj.put("messageId", messageId);
//	
//		BasicDBObject msgObj = (BasicDBObject) db.getCollection(MSGS_COLLECTION).findOne(dbObj);
//		Map<String,Object> msgBody = JSON.parseObject(msgObj.getString("body").replace("&quot;", "\""), Map.class);
//		msgBody.put("isRead",true);
//		String body = JSON.toJSON(msgBody).toString();
//		db.getCollection(MSGS_COLLECTION).update(dbObj, new BasicDBObject("body", body));
//	}
//	
	
	
	//修改消息的已读状态
	@RequestMapping("/changeRead")
	public JSONMessage changeRead(@RequestParam String messageId) throws Exception{
		
		try{
			DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
		
			BasicDBObject query = new BasicDBObject();
			query.put("messageId", messageId);
			
			BasicDBObject dbObj = (BasicDBObject) dbCollection.findOne(query);
			String body=null;
			if(null==dbObj)
				return JSONMessage.success();
			else {
				body=dbObj.getString("body");
				if(null==body)
					return JSONMessage.success();
			}
			//解析消息体
			Map<String,Object> msgBody = JSON.parseObject(body.replace("&quot;", "\""), Map.class);
			System.out.println(msgBody);
			msgBody.put("isRead",true); 
			body = JSON.toJSON(msgBody).toString();
			System.out.println(body);
			dbCollection.update(query, new BasicDBObject(MongoOperator.SET,new BasicDBObject("body", body)));
				
			} catch (Exception e){
				e.printStackTrace();
			}
		
		return JSONMessage.success();
		
	}

	//添加单聊记录
	@RequestMapping(value="/addMsg", method = RequestMethod.POST)
	public JSONMessage addMsg(@RequestParam String body, @RequestParam String message, @RequestParam int receiver, @RequestParam String receiver_jid, @RequestParam int sender, @RequestParam String sender_jid, @RequestParam String messageId, @RequestParam String context) throws Exception{

		try{
			DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
			BasicDBObject dbObj = new BasicDBObject();
			dbObj.put("body", body);
			dbObj.put("direction", 1);
			dbObj.put("message", message);
			dbObj.put("receiver", receiver);
			dbObj.put("receiver_jid", receiver_jid);
			dbObj.put("sender", sender);
			dbObj.put("sender_jid", sender_jid);
			dbObj.put("ts", System.currentTimeMillis());
			dbObj.put("type", 1);
			dbObj.put("contentType", 1);
			dbObj.put("messageId", messageId);
			dbObj.put("timeSend", System.currentTimeMillis());
			dbObj.put("context", context);


			dbCollection.insert(dbObj);

			User user = userService.getUser(sender);

			Map<?, ?> params = JSON.parseObject(body, Map.class);
			if(params.get("type").toString().equals("502")){  // 开启好友验证，加关注的回复逻辑
				friendsRepository.saveNewFriends(new NewFriends(receiver,sender,user.getNickname(),1,502,1,user.getNickname()+"给您发送的回复："+ params.get("content").toString()));
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		return JSONMessage.success();

	}

	//修改消息的已读状态
	@RequestMapping(value="/addMucMsg", method = RequestMethod.POST)
	public JSONMessage addMucMsg(@RequestParam String room_id, @RequestParam String room_jid, @RequestParam int sender, @RequestParam String sender_jid, @RequestParam String nickname, @RequestParam String body, @RequestParam String message, @RequestParam String messageId, @RequestParam String context) throws Exception{

		try{
			DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_muc_msgs");
			BasicDBObject dbObj = new BasicDBObject();
			dbObj.put("room_id", room_id);
			dbObj.put("room_jid_id", room_id);
			dbObj.put("room_jid", room_jid);
			dbObj.put("message", message);
			dbObj.put("body", body);
			dbObj.put("sender", sender);
			dbObj.put("sender_jid", sender_jid);
			dbObj.put("nickname", nickname);
			dbObj.put("ts", System.currentTimeMillis());
			dbObj.put("type", 1);
			dbObj.put("public_event", 1);
			dbObj.put("event_type", 1);
			dbObj.put("contentType", 1);
			dbObj.put("messageId", messageId);
			dbObj.put("timeSend", System.currentTimeMillis());
			dbObj.put("context", context);


			dbCollection.insert(dbObj);
		} catch (Exception e){
			e.printStackTrace();
		}

		return JSONMessage.success();

	}
	
}





















