package com.shiku.mianshi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.service.impl.LiveRoomManagerImpl;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.vo.Givegift;
import cn.xyz.mianshi.vo.LiveRoom;
import cn.xyz.mianshi.vo.LiveRoom.LiveRoomMember;

@RestController
@RequestMapping("/liveRoom")
public class LiveRoomController extends AbstractController{
	
	@Autowired
	private UserManager userManager;
	
	@Autowired
	private LiveRoomManagerImpl liveRoomManager;
	//获取直播间详情
	@RequestMapping(value = "/get")
	public JSONMessage getLiveRoom(@RequestParam(defaultValue="") String roomId) {
		Object data=null;
		try {
			LiveRoom room=liveRoomManager.get(new ObjectId(roomId));
			if(!room.getUrl().contains("//"))
				room.setUrl(KSessionUtil.getConfig().getLiveUrl()+room.getUrl());
			data=room;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(data);
		return JSONMessage.success(null,data);
	}
	//获取所有的直播房间
	@RequestMapping(value = "/list")
	public JSONMessage findLiveRoomList(@RequestParam(defaultValue="") String name,@RequestParam(defaultValue="") String nickName,
			@RequestParam(defaultValue="0") Integer userId,@RequestParam(defaultValue="0") Integer pageIndex,
			@RequestParam(defaultValue="10") Integer pageSize) {
		Object data=null;
		try {

			data=liveRoomManager.findLiveRoomList(name, nickName, userId, pageIndex, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSONMessage.success(null,data);
	}
	
	//创建直播间
	@RequestMapping(value = "/create")
	public JSONMessage createLiveRoom(@ModelAttribute LiveRoom room) {
		Object data=null;
		try {
			room.setUserId(ReqUtil.getUserId());
			data=liveRoomManager.createLiveRoom(room);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSONMessage.success(null,data);
	}
	//更新直播间
	@RequestMapping(value = "/update")
	public JSONMessage updateLiveRoom(@ModelAttribute LiveRoom room) {
		Map<String,Object> data=new HashMap<String,Object>();
		try {
			liveRoomManager.updateLiveRoom(room);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSONMessage.success();
	}
	//删除直播间
	@RequestMapping(value = "/delete")
	public JSONMessage deleteLiveRoom(@RequestParam(defaultValue="")String roomId) {
		Map<String,Object> data=new HashMap<String,Object>();
		try {
			liveRoomManager.deleteLiveRoom(new ObjectId(roomId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSONMessage.success();
	}
	//查询房间成员
	@RequestMapping(value = "/memberList")
	public JSONMessage findLiveRoomMemberList(@RequestParam(defaultValue="") String roomId) {
		Object data=null;
		ObjectId id=null;
		try {
			if(!StringUtil.isEmpty(roomId))
				id=new ObjectId(roomId);
			data=liveRoomManager.findLiveRoomMemberList(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSONMessage.success(null,data);
	}
	//获取单个成员
	@RequestMapping(value="/get/member")
	public JSONMessage getLiveRoomMember(@RequestParam String roomId,@RequestParam Integer userId){
		Object data=null;
		data=liveRoomManager.getLiveRoomMember(new ObjectId(roomId), userId);
		if(data==null){
			return JSONMessage.failure("用户不在改房间");
		}else{
			return JSONMessage.success(null, data);
		}
		
		
	}
	//加入直播间
	@RequestMapping(value = "/enterInto")
	public JSONMessage enterIntoLiveRoom(@RequestParam(defaultValue="")String roomId) {
		/*Map<String,Object> data=new HashMap<String,Object>();*/
		boolean red=true;
		try {
			red=liveRoomManager.enterIntoLiveRoom(ReqUtil.getUserId(), new ObjectId(roomId));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(red==false){
			return JSONMessage.failure(null);
		}else{
			return JSONMessage.success();
		}
		
	}
	//退出直播间
	@RequestMapping(value = "/quit")
	public JSONMessage exitLiveRoom(@RequestParam(defaultValue="")String roomId,@RequestParam(defaultValue="0")Integer userId) {
			Map<String,Object> data=new HashMap<String,Object>();
			try {
				liveRoomManager.exitLiveRoom(userId, new ObjectId(roomId));
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		
		return JSONMessage.success();
	}
	//踢出直播间
	@RequestMapping(value="/kick")
	public JSONMessage kick(@RequestParam String roomId,@RequestParam Integer userId){
		try {
			liveRoomManager.kick(userId, new ObjectId(roomId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.success();
		
	}

	//开启/取消禁言
	@RequestMapping("/shutup")
	public JSONMessage shutup(@RequestParam int state,@RequestParam Integer userId,@RequestParam String roomId){
		try {
			liveRoomManager.shutup(state, userId,new ObjectId(roomId));
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return JSONMessage.success();
	}
	
	
	//发送弹幕
	@RequestMapping("/barrage")
	public JSONMessage barrage(@RequestParam Integer userId,@RequestParam ObjectId roomId,@RequestParam String text){
		JSONObject data=new JSONObject();
		ObjectId givegiftId;
		try {
			givegiftId=liveRoomManager.barrage(userId,roomId,text);
			data.put("givegiftId",givegiftId);
			return JSONMessage.success(null,data);
		} catch (ServiceException e) {
			return JSONMessage.failure(e.getMessage());
		}
		/*if(judge==true){
			return JSONMessage.success();
		}else{
			return JSONMessage.failure("余额不足");
		}*/
		
	}
	
	//显示所有礼物
	@RequestMapping(value="/giftlist")
	public JSONMessage giftlist(@RequestParam(defaultValue="") String name ,@RequestParam(defaultValue="0") Integer pageIndex,@RequestParam(defaultValue="10") Integer pageSize){
		Object data=null;
		data=liveRoomManager.findAllgift(name,pageIndex, pageSize);
		return JSONMessage.success(null, data);
	}
	
	//送礼物
	@RequestMapping(value="/give")
	public JSONMessage give(@RequestParam Integer userId,@RequestParam String giftId,@RequestParam int count,
			@RequestParam Double price,@RequestParam String roomId){
			JSONObject data=new JSONObject();
			ObjectId giftid;
		try {
			giftid=liveRoomManager.giveGift(userId, new ObjectId(giftId), count, price,new ObjectId(roomId));
			data.put("giftId",giftid);
			return JSONMessage.success(null,data);
		} catch (ServiceException e) {
			return JSONMessage.failure(e.getMessage());
		}
		
	}
	//查询购买礼物的记录
	@RequestMapping(value="/giftdeal")
	public JSONMessage giftdeal(@RequestParam Integer userId,@RequestParam(defaultValue="10") Integer pageSize,@RequestParam(defaultValue="0") Integer pageIndex){
		Object data=null;
		try {
			data=liveRoomManager.giftdeal(userId, pageIndex, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.success(null, data);
	}
	//设置管理员
	@RequestMapping(value="/setmanage")
	public JSONMessage setManage(@RequestParam Integer userId,@RequestParam int type,@RequestParam String roomId){
		try {
			liveRoomManager.setmanage(userId,type,new ObjectId(roomId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.success();
	}
	//点赞
	@RequestMapping(value="/praise")
	public JSONMessage addpraise(@RequestParam String roomId){
		try {
			liveRoomManager.addpraise(new ObjectId(roomId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.success();
	}
	
}
