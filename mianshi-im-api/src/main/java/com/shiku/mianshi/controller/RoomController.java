package com.shiku.mianshi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Room;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.Room.Member;
import net.spy.memcached.KeyUtil;
import com.mongodb.DBObject;

import com.alibaba.fastjson.JSON;

/**
 * 群组接口
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/room")
public class RoomController {

	@Resource(name = RoomManager.BEAN_ID)
	private RoomManager roomManager;
	@Autowired
	private UserManager userManager;

	/**
	 * 新增房间
	 * 
	 * @param room
	 * @param text
	 * @return
	 */
	@RequestMapping("/add")
	public JSONMessage add(@ModelAttribute Room room, @RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int isadmin) {
		List<Integer> idList = StringUtil.isEmpty(text) ? null : JSON.parseArray(text, Integer.class);
		if(isadmin == 1){
			Object data = roomManager.add(userManager.getUser(ReqUtil.getUserId()), room, idList);
			return JSONMessage.success(null, data);
		}else{
			if(roomManager.exisname(room.getName())==null){
				Object data = roomManager.add(userManager.getUser(ReqUtil.getUserId()), room, idList);
				return JSONMessage.success(null, data);
			}else{
				return JSONMessage.failure("房间名已经存在");
			}
		}
	}

	/**
	 * 删除房间
	 * 
	 * @param roomId
	 * @return
	 */
	@RequestMapping("/delete")
	public JSONMessage delete(@RequestParam String roomId) {
		roomManager.delete(userManager.getUser(ReqUtil.getUserId()), new ObjectId(roomId));
		return JSONMessage.success();
	}

	/**
	 * 更新房间
	 * 
	 * @param roomId
	 * @param roomName
	 * @param notice
	 * @param desc
	 * @return
	 */
	@RequestMapping("/update")
	public JSONMessage update(@RequestParam String roomId, @RequestParam(defaultValue = "") String roomName,
			@RequestParam(defaultValue = "") String notice, @RequestParam(defaultValue = "") String desc,
			@RequestParam(defaultValue ="-1") int showRead) {
		// if (StringUtil.isEmpty(roomName) && StringUtil.isEmpty(notice)) {
		//
		// } else {
		// User user = userManager.getUser(ReqUtil.getUserId());
		// roomManager.update(user, new ObjectId(roomId), roomName, notice,
		// desc);
		// }
		//if(roomManager.exisname(roomName)==null){
			Map<String,Integer> data=new HashMap<>();
			data.put("showRead", showRead);
			User user = userManager.getUser(ReqUtil.getUserId());
			boolean result=roomManager.update(user, new ObjectId(roomId), roomName, notice, desc,showRead);
			if(result==false){
				return JSONMessage.failure("房间名已经存在");
			}else{
				return JSONMessage.success(null, data);
			}		
	}

	/**
	 * 根据房间Id获取房间
	 * 
	 * @param roomId
	 * @return
	 */
	@RequestMapping("/get")
	public JSONMessage get(@RequestParam String roomId) {
		Object data = roomManager.get(new ObjectId(roomId));
		return JSONMessage.success(null, data);
	}

	/**
	 * 获取房间列表（按创建时间排序）
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/list")
	public JSONMessage list(@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "") String roomName) {
		Object data = roomManager.selectList(pageIndex, pageSize, roomName);
		return JSONMessage.success(null, data);
	}

	@RequestMapping("/member/update")
	public JSONMessage updateMember(@RequestParam String roomId, @ModelAttribute Member member,
			@RequestParam(defaultValue = "") String text) {
		List<Integer> idList = StringUtil.isEmpty(text) ? null : JSON.parseArray(text, Integer.class);
		User user = userManager.getUser(ReqUtil.getUserId());
		if (null == idList)
			roomManager.updateMember(user, new ObjectId(roomId), member);
		else
			roomManager.updateMember(user, new ObjectId(roomId), idList);

		return JSONMessage.success();
	}
	//退出群组
	@RequestMapping("/member/delete")
	public JSONMessage deleteMember(@RequestParam String roomId, @RequestParam int userId) {
		User user = userManager.getUser(ReqUtil.getUserId());
		roomManager.deleteMember(user, new ObjectId(roomId), userId);
		return JSONMessage.success();
	}

	@RequestMapping("/member/get")	
	public JSONMessage getMember(@RequestParam String roomId, @RequestParam int userId) {
		Member data = roomManager.getMember(new ObjectId(roomId), userId);
		if(StringUtil.isEmpty(data.getCall()))
			data.setCall(roomManager.getCall(new ObjectId(roomId)));
		return JSONMessage.success(null, data);
	}

	@RequestMapping("/member/list")
	public JSONMessage getMemberList(@RequestParam String roomId,@RequestParam(defaultValue="") String keyword) {
		Object data = roomManager.getMemberList(new ObjectId(roomId),keyword);
		return JSONMessage.success(null, data);
	}
	//获取房间
	@RequestMapping("/get/call")
	public JSONMessage getRoomCall(@RequestParam String roomId){
		Object data=roomManager.getCall(new ObjectId(roomId));
		return JSONMessage.success(null, data);
		
	}
	@RequestMapping("/join")
	public JSONMessage join(@RequestParam String roomId, @RequestParam(defaultValue = "2") int type) {
		roomManager.join(ReqUtil.getUserId(), new ObjectId(roomId), type);
		return JSONMessage.success();
	}

	@RequestMapping("/joinbyjid")
	public JSONMessage joinbyjid(@RequestParam String jid, @RequestParam(defaultValue = "2") int type) {
		String Id = roomManager.getRoomList(jid);

		roomManager.join(ReqUtil.getUserId(), new ObjectId(Id), type);
		return JSONMessage.success();
	}

	@RequestMapping("/backbyjid")
	public JSONMessage backbyjid(@RequestParam String jid, @RequestParam(defaultValue = "2") int type) {
		String Id = roomManager.getRoomList(jid);
        ObjectId id = new ObjectId(Id);
		return JSONMessage.success(null, id);
	}

	@RequestMapping("/leave")
	public JSONMessage leave(@RequestParam String roomId) {
		roomManager.leave(ReqUtil.getUserId(), new ObjectId(roomId));
		return JSONMessage.success();
	}

	@RequestMapping("/list/his")
	public JSONMessage historyList(@RequestParam(defaultValue = "0") int type,
			@RequestParam(defaultValue = "0") int pageIndex, @RequestParam(defaultValue = "10") int pageSize) {
		// Object data = roomManager.selectHistoryList(ReqUtil.getUserId(),
		// type);
		Object data = roomManager.selectHistoryList(ReqUtil.getUserId(), type, pageIndex, pageSize);
		return JSONMessage.success(null, data);
	}
	//设置/取消管理员
	@RequestMapping("/set/admin")
	public JSONMessage setAdmin(@RequestParam String roomId,@RequestParam int touserId,@RequestParam int type){
		roomManager.setAdmin(new ObjectId(roomId), touserId,type,ReqUtil.getUserId());
		return JSONMessage.success();
	}
	
	//添加（群共享）
	@RequestMapping("/add/share")
	public JSONMessage Addshare(@RequestParam ObjectId roomId,@RequestParam int type,@RequestParam long size,@RequestParam int userId
			,@RequestParam String url,@RequestParam String name){
		Object data=roomManager.Addshare(roomId,size,type ,userId, url,name);
		return JSONMessage.success(null, data);
	}
	//查询(群共享)
	@RequestMapping("/share/find")
	public JSONMessage findShare(@RequestParam ObjectId roomId,@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue="0") long time
			,@RequestParam(defaultValue="0") int userId,@RequestParam(defaultValue="10") int pageSize){
		Object data=roomManager.findShare(roomId, time, userId, pageIndex, pageSize);
		return JSONMessage.success(null, data);
	}
	
	@RequestMapping("/share/get")
	public JSONMessage getShare(@RequestParam ObjectId roomId,@RequestParam ObjectId shareId){
		Object data=roomManager.getShare(roomId, shareId);
		return JSONMessage.success(null, data);
	}
	//删除
	@RequestMapping("/share/delete")
	public JSONMessage deleteShare(@RequestParam String roomId,@RequestParam String shareId,@RequestParam int userId){
		roomManager.deleteShare(new ObjectId(roomId),new ObjectId(shareId),userId);
		return JSONMessage.success();
	}
}
