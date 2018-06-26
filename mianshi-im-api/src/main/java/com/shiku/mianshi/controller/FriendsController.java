package com.shiku.mianshi.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.User;

/**
 * 关系接口
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/friends")
public class FriendsController {

	@Autowired
	private FriendsManager friendsManager;
	@Autowired
	private UserManager userManager;
	
	//新增关注
	@RequestMapping("/attention/add")
	public JSONMessage addAtt(@RequestParam Integer toUserId) {
		return friendsManager.followUser(ReqUtil.getUserId(), toUserId);
	}

	//删除新朋友记录
	@RequestMapping("/newfriend/del")
	public JSONMessage delNewFriend(@RequestParam(value = "UserId") Integer UserId) {
		friendsManager.delNewFriend(UserId);
		return JSONMessage.success("删除新朋友消息成功");
	}
	//添加黑名单
	@RequestMapping("/blacklist/add")
	public JSONMessage addBlacklist(@RequestParam Integer toUserId) {
		Object data=friendsManager.addBlacklist(ReqUtil.getUserId(), toUserId);
		return JSONMessage.success("加入黑名单成功",data);
	}
	//加好友
	@RequestMapping("/add")
	public JSONMessage addFriends(@RequestParam(value = "toUserId") Integer toUserId) {
		friendsManager.addFriends(ReqUtil.getUserId(), toUserId);

		return JSONMessage.success("加好友成功");
	}

	//加好友
	@RequestMapping(value = "/addfriend", method = RequestMethod.POST)
	public JSONMessage addFriendsForOut(@RequestParam(value = "UserId") Integer UserId, @RequestParam(value = "toUserId") Integer toUserId) {
		friendsManager.addFriends(UserId, toUserId);

		return JSONMessage.success("加好友成功");
	}

	//移出黑名单
	@RequestMapping("/blacklist/delete")
	public JSONMessage deleteBlacklist(@RequestParam Integer toUserId) {
		Object data = friendsManager.deleteBlacklist(ReqUtil.getUserId(), toUserId);
		return JSONMessage.success("取消拉黑成功", data);
	}
	//取消关注
	@RequestMapping("/attention/delete")
	public JSONMessage deleteFollow(@RequestParam(value = "toUserId") Integer toUserId) {
		friendsManager.unfollowUser(ReqUtil.getUserId(), toUserId);
		return JSONMessage.success("取消关注成功");
	}
	//删除好友
	@RequestMapping("/delete")
	public JSONMessage deleteFriends(@RequestParam Integer toUserId) {
		friendsManager.deleteFriends(ReqUtil.getUserId(), toUserId);
		return JSONMessage.success("删除好友成功");
	}
	//修改备注
	@RequestMapping("/remark")
	public JSONMessage friendsRemark(@RequestParam int toUserId, @RequestParam String remarkName) {
		friendsManager.updateRemark(ReqUtil.getUserId(), toUserId, null == remarkName ? "" : remarkName);

		return JSONMessage.success(null);
	}
	//黑名单列表
	@RequestMapping("/blacklist")
	public JSONMessage queryBlacklist() {
		List<Friends> data = friendsManager.queryBlacklist(ReqUtil.getUserId());

		return JSONMessage.success(null, data);
	}
	//粉丝列表
	@RequestMapping("/fans/list")
	public JSONMessage queryFans(@RequestParam(defaultValue = "") Integer userId) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		List<Fans> data = friendsManager.getFansList(userId);

		return JSONMessage.success(null, data);
	}
	//关注列表
	@RequestMapping("/attention/list")
	public JSONMessage queryFollow(@RequestParam(defaultValue = "") Integer userId,@RequestParam(defaultValue = "0")int status) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		List<Friends> data = friendsManager.queryFollow(userId,status);

		return JSONMessage.success(null, data);
	}
	
	@RequestMapping("/get")
	public JSONMessage getFriends(@RequestParam(defaultValue = "") Integer userId,int toUserId) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		Friends data = friendsManager.getFriends(userId, toUserId);

		return JSONMessage.success(null, data);
	}

	@RequestMapping("/list")
	public JSONMessage queryFriends(@RequestParam Integer userId,@RequestParam(defaultValue="") String keyword) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		Object data = friendsManager.queryFriends(userId);

		return JSONMessage.success(null, data);
	}

	@RequestMapping("/page")
	public JSONMessage getFriendsPage(@RequestParam Integer userId,@RequestParam(defaultValue="") String keyword,
			@RequestParam(defaultValue = "2") int status,
			@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "0") int pageSize) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		Object data = friendsManager.queryFriends(userId,status,keyword, pageIndex, pageSize);

		return JSONMessage.success(null, data);
	}
	//消息免打扰
	@RequestMapping("/update/OfflineNoPushMsg")
	public JSONMessage updateOfflineNoPushMsg(@RequestParam Integer userId,@RequestParam Integer toUserId,@RequestParam int offlineNoPushMsg){
		userId=(null == userId ? ReqUtil.getUserId() : userId);
		Friends data=friendsManager.updateOfflineNoPushMsg(userId,toUserId,offlineNoPushMsg);
		
		return JSONMessage.success(null,data);
	}
	
	@RequestMapping("/friendsAndAttention") //返回好友的userId 和单向关注的userId  及黑名单的userId
	public JSONMessage getFriendsPage(@RequestParam Integer userId,@RequestParam(defaultValue="") String type) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		Object data = friendsManager.friendsAndAttentionUserId(userId,type);
		return JSONMessage.success(null, data);
	}
	
	@RequestMapping("/newFriend/list")
	public JSONMessage newFriendList(@RequestParam Integer userId,@RequestParam(defaultValue="0") int pageIndex,
			@RequestParam(defaultValue="10") int pageSize) {
		userId = (null == userId ? ReqUtil.getUserId() : userId);
		Object data = friendsManager.newFriendList(userId, pageIndex, pageSize);

		return JSONMessage.success(null, data);
	}
	
	@RequestMapping("/addAllFriendsSys")
	public JSONMessage addAllFriendsSys(@RequestParam(defaultValue="10000") Integer toUserId) {
		List<Integer> userIds=userManager.getAllUserId();
		 ExecutorService pool = Executors.newFixedThreadPool(10);  
		for (Integer userId : userIds) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					friendsManager.followUser(userId, toUserId);
				}
			});
			
		}
		return JSONMessage.success();
	}

}
