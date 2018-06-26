package com.shiku.mianshi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import cn.xyz.commons.utils.Md5Util;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Menu;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

@RestController
public class MpController {
	@Resource(name = "dsForRW")
	Datastore dsForRW;
	@Autowired
	private UserManager userManager;

	@RequestMapping("/public/menu/list")
	public JSONMessage getMenuList(int userId) {
		userId=0!=userId?userId:ReqUtil.getUserId();
		Query<Menu> q = dsForRW.createQuery(Menu.class).field("userId").equal(userId).field("parentId")
				.equal(0);
		List<Menu> data = q.order("index").asList();
		if (null != data) {
			for (Menu menu : data) {
				q = dsForRW.createQuery(Menu.class).field("parentId").equal(menu.getId());
				menu.setMenuList(q.order("index").asList());
			}
		}
		return JSONMessage.success(null, data);
	}
	//单条图文
	@RequestMapping("/pulic/pushToAll")
	public void pushToAll(){
		Integer userId=ReqUtil.getUserId();
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("title", "测试单条图文消息");
		jsonObj.put("sub", "Test a single text message");
		jsonObj.put("img", "http://shiku.co/img/hd1.png");
		jsonObj.put("url", "http://shiku.co/");
		List<Integer> toUserIdList = Lists.newArrayList();
		User touser=userManager.getUser(userId);
		toUserIdList.add(userId);
		User user=userManager.getUser(10000);
		user.setPassword(Md5Util.md5Hex("10000"));
		MessageBean messageBean=new MessageBean();
		messageBean.setType(80);
		messageBean.setFromUserId(user.getUserId().toString());
		messageBean.setFromUserName(user.getNickname());
		messageBean.setToUserId(touser.getUserId().toString());
		messageBean.setToUserName(touser.getUsername());
		messageBean.setContent(jsonObj.toString());
		try {
			
			KXMPPServiceImpl.getInstance().send(user,toUserIdList, messageBean.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//多条图文
	@RequestMapping("/public/manyToAll")
	public void manyToAll(){
		Integer userId=ReqUtil.getUserId();
		JSONObject jsonObj=null;
		String[] title={"测试多条图文消息(一)","测试多条图文消息(二)","测试多条图文消息(三)"};
		String[] url={"http://shiku.co/faq.html","http://shiku.co/livedemo.html","http://shiku.co/features.html"};
		String[] img={"http://shiku.co/img/hd2.png","http://img2.ph.126.net/REHyw0wOVk-u6bqS0JsrMA==/6598211059727802272.jpg","http://img0.ph.126.net/G6-JfZERL5gRoXgGxgGkiQ==/6598105506611511171.jpg"};
		List<Object> list=new ArrayList<Object>();
		for(int i=0;i<title.length;i++){
			jsonObj=new JSONObject();
			jsonObj.put("title",title[i]);
			jsonObj.put("url", url[i]);
			jsonObj.put("img", img[i]);
			list.add(jsonObj);
		}
		List<Integer> toUserIdList = Lists.newArrayList();
		User touser=userManager.getUser(userId);
		toUserIdList.add(touser.getUserId());
		User user=userManager.getUser(10000);
		user.setPassword(Md5Util.md5Hex("10000"));
		MessageBean messageBean=new MessageBean();
		messageBean.setType(81);
		messageBean.setFromUserId(user.getUserId().toString());
		messageBean.setFromUserName(user.getNickname());
		messageBean.setToUserId(touser.getUserId().toString());
		messageBean.setToUserName(touser.getUsername());
		messageBean.setContent(list.toString());
		try {
			if(user.getUserId()==10000){
				KXMPPServiceImpl.getInstance().send(user, toUserIdList, messageBean.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
