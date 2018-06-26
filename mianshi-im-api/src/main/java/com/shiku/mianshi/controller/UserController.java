package com.shiku.mianshi.controller;

import java.util.*;
import java.text.ParseException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import cn.xyz.commons.autoconfigure.KApplicationProperties;
import cn.xyz.commons.utils.*;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.service.impl.FriendsManagerImpl;
import cn.xyz.mianshi.service.impl.RoomManagerImplForIM;
import cn.xyz.mianshi.vo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mongodb.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.jivesoftware.smack.packet.Message;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.alipay.util.AliPayUtil;
import com.google.common.collect.Maps;
import com.wxpay.utils.WXPayUtil;
import com.wxpay.utils.WxPayDto;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.example.UserQueryExample;
import cn.xyz.mianshi.service.impl.ConsumeRecordManagerImpl;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.service.KXMPPServiceImpl;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController extends AbstractController {
	@Resource
	private KApplicationProperties.XMPPConfig xmppConfig;
	@Autowired
	private FriendsManagerImpl friendsManager;
	@Resource(name = "jedisTemplate")
	protected JedisTemplate jedisTemplate;
	@Autowired
	private UserManagerImpl userManager;
	@Resource(name = RoomManager.BEAN_ID)
	private RoomManagerImplForIM roomManager;
	@Autowired
	ConsumeRecordManagerImpl consumeRecordManager;
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;
	@RequestMapping(value = "/register")


	public JSONMessage register(@Valid UserExample example) {
		example.setPhone(example.getTelephone());
		example.setTelephone(example.getAreaCode()+example.getTelephone());
		Object data = userManager.registerIMUser(example);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/login")
	public JSONMessage login(@ModelAttribute UserExample example) {
		Object data = userManager.login(example);
		return JSONMessage.success(null, data);
	}
	@RequestMapping(value = "/login/v1")
	public JSONMessage loginv1(@ModelAttribute UserExample example) {
		//example.setTelephone(example.getAreaCode()+example.getTelephone());
		Object data = userManager.login(example);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/login/auto")
	public JSONMessage loginAuto(@RequestParam String access_token, @RequestParam int userId,
			@RequestParam(defaultValue="") String serial,@RequestParam(defaultValue="") String appId) {
		Object data = userManager.loginAuto(access_token, userId, serial,appId);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/logout")
	public JSONMessage logout(@RequestParam String access_token,@RequestParam(defaultValue="86") String areaCode,String telephone) {
		userManager.logout(access_token,areaCode,telephone);
		return JSONMessage.success(null);
	}
	@RequestMapping(value = "/outtime")
	public JSONMessage outtime(@RequestParam String access_token,@RequestParam int userId) {
		userManager.outtime(access_token,userId);
		return JSONMessage.success(null);
	}

	@RequestMapping("/update")
	public JSONMessage updateUser(@ModelAttribute UserExample param) {
		User data = userManager.updateUser(ReqUtil.getUserId(), param);
		return JSONMessage.success(null, data);
	}
	//设置消息免打扰
	@RequestMapping("/update/OfflineNoPushMsg")
	public JSONMessage updatemessagefree(@RequestParam int offlineNoPushMsg){
		Query<User> q = dsForRW.createQuery(User.class).field("_id")
				.equal(ReqUtil.getUserId());
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
		ops.set("offlineNoPushMsg",offlineNoPushMsg);
		User data=dsForRW.findAndModify(q, ops);
		return JSONMessage.success(null, data);
	}
	
	@RequestMapping("/channelId/set")
	public JSONMessage setChannelId(@RequestParam String deviceId,String channelId) {
		if(StringUtil.isEmpty(channelId))
			return JSONMessage.success();
		String key1 = String.format("user:%s:channelId", ReqUtil.getUserId());
		String key2 = String.format("user:%s:deviceId", ReqUtil.getUserId());
		String key3 = String.format("channelId:%s", channelId);
		jedisTemplate.del(key1, key2, key3);
		jedisTemplate.set(key1, channelId);
		jedisTemplate.set(key2, deviceId);
		jedisTemplate.set(key3, ReqUtil.getUserId() + "");

		return JSONMessage.success();
	}

	@RequestMapping(value = "/get")
	public JSONMessage getUser(@RequestParam(defaultValue = "0") int userId) {
		int loginedUserId = ReqUtil.getUserId();
		int toUserId = 0 == userId ? loginedUserId : userId;
		User user = userManager.getUser(loginedUserId, toUserId);
		user.setOnlinestate(userManager.getOnlinestateByUserId(toUserId));
		KSessionUtil.saveUserByUserId(userId, user);
		return JSONMessage.success(null, user);
	}

	@RequestMapping(value = "/query")
	public JSONMessage queryUser(@ModelAttribute UserQueryExample param) {
		Object data = userManager.query(param);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/queryByPhone", method = { RequestMethod.POST })
	public JSONMessage queryByPhone(@RequestParam String phone) {
		Object data = userManager.getUserByPhone(phone);
		return JSONMessage.success(null, data);
	}

	@RequestMapping("/password/reset")
	public JSONMessage resetPassword(@RequestParam(defaultValue="86") String areaCode,
			@RequestParam(defaultValue = "") String telephone,
			@RequestParam(defaultValue = "") String randcode, @RequestParam(defaultValue = "") String newPassword) {
		JSONMessage jMessage;
		telephone=areaCode+telephone;
		if (StringUtil.isEmpty(telephone) || (StringUtil.isEmpty(randcode)) || StringUtil.isEmpty(newPassword)) {
			jMessage = KConstants.Result.ParamsAuthFail;
		} else {
			userManager.resetPassword(telephone, newPassword);
			Integer userId=ReqUtil.getUserId();
			KSessionUtil.deleteUserByUserId(userId);
			jMessage = JSONMessage.success(null);
		}

		return jMessage;
	}

	@RequestMapping("/password/update")
	public JSONMessage updatePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam(defaultValue = "0") int isadmin) {
		JSONMessage jMessage;
		if(isadmin == 1){
			Integer userId=ReqUtil.getUserId();
			userManager.updatePassword(userId, oldPassword, newPassword, isadmin);
			KSessionUtil.deleteUserByUserId(userId);
			jMessage = JSONMessage.success(null);
		}else{
			if (StringUtil.isEmpty(oldPassword) || StringUtil.isEmpty(newPassword)) {
				jMessage = KConstants.Result.ParamsAuthFail;
			} else {
				Integer userId=ReqUtil.getUserId();
				userManager.updatePassword(userId, oldPassword, newPassword, isadmin);
				KSessionUtil.deleteUserByUserId(userId);
				jMessage = JSONMessage.success(null);
			}
		}
		return jMessage;
	}

	@RequestMapping(value = "/settings")
	public JSONMessage getSettings(@RequestParam int userId) {
		Object data = userManager.getSettings(0 == userId ? ReqUtil.getUserId() : userId);
		return JSONMessage.success(null, data);
	}

	@RequestMapping(value = "/settings/update")
	public JSONMessage updateSettings(@ModelAttribute User.UserSettings userSettings) {
		Integer userId=ReqUtil.getUserId();
		Object data = userManager.updateSettings(userId,userSettings);
		KSessionUtil.deleteUserByUserId(userId);
		return JSONMessage.success(null, data);
	}
	@RequestMapping(value = "/recharge/getSign")
	public JSONMessage getSign(@RequestParam int payType,@RequestParam String price) {
		Map<String,String> map=Maps.newLinkedHashMap();
		String orderInfo="";
		if(0<payType){
			String orderNo=AliPayUtil.getOutTradeNo();
			 ConsumeRecord entity=new ConsumeRecord();
			 	entity.setUserId(ReqUtil.getUserId());
				entity.setTime(DateUtil.currentTimeSeconds());
				entity.setType(KConstants.MOENY_ADD);
				entity.setDesc("余额充值");
				entity.setStatus(KConstants.OrderStatus.CREATE);
				entity.setTradeNo(orderNo);
				entity.setPayType(payType);
				entity.setMoney(new Double(price));
				if(KConstants.PayType.ALIPAY==payType){
					orderInfo= AliPayUtil.getOrderInfo("聘吧余额充值", "余额充值", price,orderNo);
					 String sign=AliPayUtil.sign(orderInfo);
					consumeRecordManager.saveConsumeRecord(entity);
					 map.put("sign", sign);
					 map.put("orderInfo", orderInfo);
					 System.out.println("orderInfo>>>>>"+orderInfo);
					 //System.out.println("sign>>>>>"+sign);
				return JSONMessage.success(null, map);
			}else {
				WxPayDto tpWxPay = new WxPayDto();
				//tpWxPay.setOpenId(openId);
				tpWxPay.setBody("酷聊余额充值");
				tpWxPay.setOrderId(orderNo);
				tpWxPay.setSpbillCreateIp(PayConstants.WXSPBILL_CREATE_IP);
				tpWxPay.setTotalFee(price);
				consumeRecordManager.saveConsumeRecord(entity);
				Object data=WXPayUtil.getPackage(tpWxPay);
				return JSONMessage.success(null, data);
			}
		}
			return JSONMessage.failure("没有选择支付类型");
	}
	
	//@RequestMapping(value = "/useralladd")
	public JSONMessage useralladd() throws Exception {
		Cursor attach=dsForRW.getDB().getCollection("user").find();		
		while (attach.hasNext()) {
			DBObject fileobj=attach.next();
			DBObject ref = new BasicDBObject();
			ref.put("user_id", fileobj.get("_id")+"@www.shiku.co");
			DBObject obj=dsForTigase.getDB().getCollection("tig_users").findOne(ref);
			if(null!=obj){
				System.out.println("123");
			}else{
				KXMPPServiceImpl.getInstance().register(fileobj.get("_id").toString(),fileobj.get("password").toString());
			}			
		}
		return JSONMessage.success();
	}
	@RequestMapping(value = "/Recharge")
	public JSONMessage Recharge(Double money, int type) throws Exception{
		String tradeNo=AliPayUtil.getOutTradeNo();
		Integer userId=ReqUtil.getUserId();
		Map<String, Object> data=Maps.newHashMap();
			//创建充值记录
			ConsumeRecord record=new ConsumeRecord();
			record.setUserId(userId);
			record.setTradeNo(tradeNo);
			record.setMoney(money);
			record.setStatus(KConstants.OrderStatus.END);
			record.setType(KConstants.MOENY_ADD);
			record.setPayType(type);
			record.setDesc("余额充值");
			record.setTime(DateUtil.currentTimeSeconds());
			consumeRecordManager.save(record);
			try {
				Double balance=userManager.rechargeUserMoeny(userId, money, KConstants.MOENY_ADD);
				data.put("balance", balance);
				return JSONMessage.success(null,data);
			} catch (Exception e) {
				return JSONMessage.error(e);
			}
			
	}

	@RequestMapping(value = "/deleteUserByTB", method = { RequestMethod.GET })
	public JSONMessage deleteUserByTB(@RequestParam(defaultValue = "0") int userId,
						   @RequestParam(defaultValue = "") String nickname, @RequestParam int pageIndex)  throws Exception{
		try {
			if (0 != userId) {
				DBCollection dbCollection = dsForRW.getCollection(User.class);
				dbCollection.remove(new BasicDBObject("_id", userId));

				DBCollection tdbCollection = dsForTigase.getDB().getCollection("tig_users");
				String xmpphost=xmppConfig.getDomain();
				tdbCollection.remove(new BasicDBObject("user_id", userId+"@"+xmpphost));

				//增加删除ejabberd
				JSONObject obj = new JSONObject();
				obj.put("host", xmppConfig.getDomain());
				obj.put("user", userId+"");

				HttpUtil.postxmpp(obj,xmppConfig.getDelUser(),xmppConfig.getAuthorization());

				//删除用户关系
				friendsManager.deleteFansAndFriends(userId);


				//得到该用户所在的房间
				List<Object> historyIdList = Lists.newArrayList();
				int type = 2;
				int pageSize = 500;
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
					System.out.println(dbObj.get("roomId"));
					User user = userManager.getUser(ReqUtil.getUserId());
					roomManager.deleteMember(user, new ObjectId(dbObj.get("roomId").toString()), userId);
				}
			}
			/*if (!StringUtil.isEmpty(nickname)) {
				DBCollection dbCollection = dsForRW.getCollection(User.class);
				dbCollection.remove(new BasicDBObject("nickname", Pattern.compile(nickname)));
			}*/

			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("删除用户异常");
		}
	}

	@RequestMapping(value = "/getUserMoeny")
	public JSONMessage getUserMoeny() throws Exception{
		Integer userId=ReqUtil.getUserId();
		Map<String, Object> data=Maps.newHashMap();
		Double balance=userManager.getUserMoeny(userId);
		if(null==balance)
			balance=0.0;
		data.put("balance", balance);
		return JSONMessage.success(null,data);
			
	}	
	@RequestMapping(value = "/getUserStatusCount")
	public JSONMessage getUserStatusCount(@RequestParam(defaultValue="0") int pageIndex,
			@RequestParam(defaultValue="100") int pageSize,String sign,
			String startDate,String endDate,@RequestParam(defaultValue="0") int type) throws Exception{
		Map data=Maps.newHashMap();
		Map<String,Long> timeM=new HashMap<String, Long>();
		Query<UserStatusCount> query=dsForRW.createQuery(UserStatusCount.class);
		 long currentTime =DateUtil.currentTimeSeconds();
		long startTime=0;
		long endTime=0;
		if(!StringUtils.isEmpty(sign)){
			timeM =getTimes(new Integer(sign));
			startTime = timeM.get("startTime");
			endTime = timeM.get("endTime");
				
		}else{
			try {
				startTime=DateUtil.getDate(startDate, "yyyy-MM-dd").getTime()/1000;
				endTime=DateUtil.getDate(endDate, "yyyy-MM-dd").getTime()/1000;
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException("时间转换异常");
			}
		}
		
		if(0<type)
			query.field("type").equal(type);
		if(0!=endTime){
			query.field("time").greaterThanOrEq(startTime);
			query.field("time").lessThanOrEq(endTime);
		}
		query.order("time");
		
		List<UserStatusCount> list=query.asList();
		
		
		return JSONMessage.success(null,list);
			
	}	
	
	@RequestMapping(value = "/getOnLine")
	public JSONMessage getOnlinestateByUserId(Integer userId){
		userId=null!=userId?userId:ReqUtil.getUserId();
		Object data=userManager.getOnlinestateByUserId(userId);
		return JSONMessage.success(null, data);
	}
	@RequestMapping("/report")
	public JSONMessage report(@RequestParam Integer toUserId, @RequestParam String text) {
		userManager.report(ReqUtil.getUserId(), toUserId, text);
		return JSONMessage.success();
	}

	@RequestMapping(value = "/chat_logs_all", method = { RequestMethod.POST })
	public JSONMessage chat_logs_all(@RequestParam(defaultValue = "0") long startTime,
									  @RequestParam(defaultValue = "0") long endTime, @RequestParam(defaultValue = "0") int sender,
									  @RequestParam(defaultValue = "0") int receiver, @RequestParam(defaultValue = "0") int pageIndex,
									  @RequestParam(defaultValue = "25") int pageSize, HttpServletRequest request) {
		DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
		BasicDBObject q = new BasicDBObject();
		if (0 == receiver) {
			q.put("receiver", new BasicDBObject("$ne", 10005));
		} else {
			q.put("direction",1);
			q.put("receiver", BasicDBObjectBuilder.start("$eq", receiver).add("$ne", 10005).get());
		}
		if (0 == sender) {
			q.put("sender", new BasicDBObject("$ne", 10005));
		} else {
			q.put("direction",1);
			q.put("sender", BasicDBObjectBuilder.start("$eq", sender).add("$ne", 10005).get());
		}

		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));


        //System.out.println(q);
		DBCursor cursor = dbCollection.find(q).sort(new BasicDBObject("_id", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);

		java.util.List<DBObject> list = Lists.newArrayList();
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}

		return JSONMessage.success(null, list);
	}

	@RequestMapping(value = "/groupchat_logs_all", method = { RequestMethod.POST })
	public JSONMessage groupchat_logs_all(@RequestParam(defaultValue = "0") long startTime,
										   @RequestParam(defaultValue = "0") long endTime,
										   @RequestParam(defaultValue = "0") int pageIndex, @RequestParam(defaultValue = "25") int pageSize, @RequestParam(defaultValue = "") String jid, @RequestParam(defaultValue = "0") int sender,
										   HttpServletRequest request) {

		DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_muc_msgs");

		BasicDBObject q = new BasicDBObject();

		if (0 != startTime)
			q.put("ts", new BasicDBObject("$gte", startTime));
		if (0 != endTime)
			q.put("ts", new BasicDBObject("$lte", endTime));

		if(!StringUtil.isEmpty(jid))
		    q.put("room_jid_id", new BasicDBObject("$eq",jid));

		if(0 != sender)
			q.put("sender", new BasicDBObject("$eq",sender));

		DBCursor cursor = dbCollection.find(q).sort(new BasicDBObject("_id", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		java.util.List<DBObject> list = Lists.newArrayList();
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}

		return JSONMessage.success(null, list);
	}

	@RequestMapping(value = "/outSendXmpp", method = { RequestMethod.POST })
	public JSONMessage outSendXmpp(@RequestParam String body, String telephone,@RequestParam(defaultValue = "") String text) {
		List<String> idList = StringUtil.isEmpty(text) ? null : JSON.parseArray(text, String.class);
        User user = userManager.getUserByPhone(telephone);
		if (null != idList && !idList.isEmpty()) {
			for (String mobile : idList) {
				System.out.println(idList);
				System.out.println(mobile);
				User toUser = userManager.getUserByPhone(mobile);
				System.out.println(toUser);
				if(toUser.getUserId() != 10000){
					new Thread(new Runnable() {
						@Override
						public void run() {
							Message message = new Message();
							message.setFrom(user.getUserId()+"@"+xmppConfig.getDomain());
							message.setTo(toUser.getUserId()+"@"+xmppConfig.getDomain());
							JSONObject jsonBody=new JSONObject();
							String messageId = UUID.randomUUID().toString();
							message.setPacketID(messageId);
							jsonBody.put("messageId", messageId);
							jsonBody.put("isRead", false);
							jsonBody.put("content", body);
							jsonBody.put("fromUserName",user.getNickname());
							jsonBody.put("timeSend", DateUtil.currentTimeSeconds());
							jsonBody.put("type", 1);
							message.setBody(jsonBody.toJSONString());

							message.setType(Message.Type.chat);

							// 修改ejabberd密码
							JSONObject  obj = new JSONObject();
							obj.put("subject", "");
							obj.put("type", "chat");
							obj.put("from", user.getUserId()+"@"+xmppConfig.getDomain());
							obj.put("to", toUser.getUserId()+"@"+xmppConfig.getDomain());
							obj.put("body", jsonBody.toJSONString());

							HttpUtil.postxmpp(obj,xmppConfig.getSendMessage(),xmppConfig.getAuthorization());
							System.out.println("发送消息成功：" + jsonBody.toJSONString());

							try{
								DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
								BasicDBObject dbObj = new BasicDBObject();
								dbObj.put("body", jsonBody.toJSONString());
								dbObj.put("direction", 1);
								dbObj.put("message", message.toXML());
								dbObj.put("receiver", toUser.getUserId());
								dbObj.put("receiver_jid", toUser.getUserId()+"@"+xmppConfig.getDomain());
								dbObj.put("sender", user.getUserId());
								dbObj.put("sender_jid", user.getUserId()+"@"+xmppConfig.getDomain());
								dbObj.put("ts", System.currentTimeMillis());
								dbObj.put("type", 1);
								dbObj.put("contentType", 1);
								dbObj.put("messageId", messageId);
								dbObj.put("timeSend", System.currentTimeMillis());
								dbObj.put("context", body);

								dbCollection.insert(dbObj);

							} catch (Exception e){
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
			return JSONMessage.success();
		}else{
			return JSONMessage.failure("接收消息的用户不存在！");
		}
	}
	
	
	
	/**
	 * 获取开始时间和结束时间
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, Long> getTimes(Integer sign) {
		Long startTime = null;
		Long endTime = DateUtil.currentTimeSeconds();
		Map<String, Long> map = Maps.newLinkedHashMap();
		
		if(sign==-3){//最近一个月
			startTime =endTime-(KConstants.Expire.DAY1*30);
		}
		else if(sign==-2){//最近7天
			startTime =endTime-(KConstants.Expire.DAY1*7);
		}
		else if(sign==-1){//最近48小时
				startTime =endTime-(KConstants.Expire.DAY1*2);
		}
		// 表示今天
		else if (sign == 0) {
			startTime = DateUtil.getTodayMorning().getTime()/1000;
		}
		
		else if(sign == 3) {
			startTime = DateUtil.strYYMMDDToDate("2000-01-01").getTime()/1000;
		}
		
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		return map;
	}
}
