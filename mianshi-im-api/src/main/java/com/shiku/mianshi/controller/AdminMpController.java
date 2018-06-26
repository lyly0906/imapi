package com.shiku.mianshi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.xyz.commons.IdWorker;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.MPService;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Menu;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

/**
 * 酷聊公众号功能
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/mp")
public class AdminMpController {
	@Resource(name = "dsForRW")
	Datastore dsForRW;
	@Resource(name = "dsForTigase")
	private Datastore dsForTig;
	@Autowired
	private FriendsManager friendsManager;
	@Autowired
	MPService mpService;
	@Autowired
	private UserManager userManager;

	@RequestMapping("/fans/delete")
	public void deleteFans(HttpServletResponse response, @RequestParam int toUserId) throws IOException {
		User user = getUser();
		if (null != user) {
			friendsManager.deleteFans(user.getUserId(), toUserId);
			response.sendRedirect("/mp/fans");
		}
	}

	private User getUser() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		Object obj = request.getSession().getAttribute("MP_USER");
		return null == obj ? null : (User) obj;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "mp/login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response) {
		String telephone = request.getParameter("username");
		String password = request.getParameter("password");
		try {
			User user = userManager.login(telephone, password);
			request.getSession().setAttribute("MP_USER", user);
			response.sendRedirect("/mp/home");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("tips", "帐号或密码错误！");
		return "mp/login";
	}
	@RequestMapping(value="logout")
	public void logout(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		request.getRequestDispatcher("/mp/login").forward(request, response);
	}
	@RequestMapping("/menu/{op}")
	public void menuOp(HttpServletResponse response, @PathVariable String op, @ModelAttribute Menu entity)
			throws IOException {
		User user = getUser();
		if ("save".equals(op)) {
			entity.setId(IdWorker.getId());
			entity.setUserId(user.getUserId());
			entity.getName();
			dsForRW.save(entity);
			response.sendRedirect("/mp/menuList");
		} else if ("delete".equals(op)) {
			dsForRW.delete(dsForRW.createQuery(Menu.class).field("_id").equal(entity.getId()));
			response.sendRedirect("/mp/menuList");
		}
	}
	//修改菜单
	@RequestMapping(value="/menu/update",method=RequestMethod.GET)
	public ModelAndView update(@RequestParam long id){
		ModelAndView mav=new ModelAndView("mp/updatemenu");
		Menu menu=dsForRW.createQuery(Menu.class).filter("_id", id).get();
		mav.addObject("menu",menu);
		return mav;
	}
	
	//提交修改
	@RequestMapping(value="/menu/saveupdate",method=RequestMethod.POST)
	public void saveupdate(HttpServletRequest request,HttpServletResponse response,@ModelAttribute Menu entity) throws IOException{
		//Menu menu=dsForRW.createQuery(Menu.class).filter("_id", entity.getId()).get();
		User user = getUser();
		//entity.setId(IdWorker.getId());
		entity.setUserId(user.getUserId());
		dsForRW.save(entity);
		response.sendRedirect("/mp/menuList");
	}
	
	@RequestMapping("/fans")
	public ModelAndView navFans(@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {
		ModelAndView mav = new ModelAndView("mp/fans");
		User user = getUser();
		if (null != user) {
			PageVO page = friendsManager.getFansPage(user.getUserId(), pageIndex, pageSize);
			mav.addObject("page", page);
		}
		return mav;
	}

	@RequestMapping("/home")
	public ModelAndView navHome() {
		User user = getUser();
		Query<Fans> q = dsForRW.createQuery(Fans.class).field("userId").equal(getUser().getUserId());
		long fansCount=q.countAll();
		DBObject dbObj = dsForTig.getDB().getCollection("shiku_msgs_count")
				.findOne(new BasicDBObject("_id", user.getUserId()));
		ModelAndView mav = new ModelAndView("mp/home");
		mav.addObject("msgCount", dbObj == null ? 0 : dbObj.get("count"));
		mav.addObject("userCount",fansCount);
		//dbObj == null ? 0 : (null == dbObj.get("fansCount") ? 0 : dbObj.get("fansCount"))
		mav.addObject("fansCount",fansCount);
		return mav;
	}

	@RequestMapping("/menuList")
	public ModelAndView navMenu(HttpServletRequest request,HttpServletResponse response) {
		User user = getUser();
		Query<Menu> q = dsForRW.createQuery(Menu.class).field("userId").equal(user.getUserId()).field("parentId")
				.equal(0);
		List<Menu> menuList = q.order("index").asList();
		if (null != menuList) {
			for (Menu menu : menuList) {
				q = dsForRW.createQuery(Menu.class).field("parentId").equal(menu.getId());
				menu.setMenuList(q.order("index").asList());
			}
		}
		ModelAndView mav = new ModelAndView("mp/menu");
		mav.addObject("menuList", menuList);
		request.getSession().setAttribute("menuList", menuList);
		return mav;
	}

	@RequestMapping("/msg")
	public ModelAndView msg(@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "15") int pageSize) {
		User user = getUser();

		ModelAndView mav = new ModelAndView("mp/msg");
		mav.addObject("msgList", mpService.getMsgList(user.getUserId(), pageIndex, pageSize));
		return mav;
	}

	@RequestMapping("/msg/list")
	public ModelAndView msgList(@RequestParam int toUserId, @RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "15") int pageSize) {
		User user = getUser();
		ModelAndView mav = new ModelAndView("mp/msg_list");
		mav.addObject("msgList", mpService.getMsgList(toUserId, user.getUserId(), pageIndex, pageSize));
		return mav;
	}

	@RequestMapping("/msg/reply")
	public ModelAndView msgReply(@RequestParam int toUserId) {
		ModelAndView mav = new ModelAndView("mp/msg_reply");
		mav.addObject("toUserId", toUserId);
		return mav;
	}

	@RequestMapping("/push")
	public String navPush() {
		return "mp/push";
	}
	
	@RequestMapping("/many")
	public String navMany(){
		return "mp/many";
	}
	
	@RequestMapping("/text")
	public String navText(){
		return "mp/text";
	}

	@RequestMapping(value = "/msg/send")
	@ResponseBody
	public JSONMessage msgSend(HttpServletResponse response, @RequestParam int toUserId, @RequestParam String body)
			throws Exception {
		User user = getUser();
		List<Fans> fansList = friendsManager.getFansList(getUser().getUserId());
		List<Integer> toUserIdList = Lists.newArrayList();
		for (Fans fans : fansList) {
			toUserIdList.add(fans.getToUserId());
		}
		MessageBean mb = new MessageBean();
		// = new String(body.getBytes("ISO-8859-1"), "utf-8")
		mb.setContent(body);
		// mb.setFileName(fileName);
		mb.setFromUserId(user.getUserId() + "");
		mb.setFromUserName(user.getNickname());
		// mb.setObjectId(objectId);
		mb.setTimeSend(DateUtil.currentTimeSeconds());
		mb.setToUserId(toUserId + "");
		// mb.setToUserName(toUserName);
		mb.setType(80);
		try {
			KXMPPServiceImpl.getInstance().send(user, toUserId, mb.toString());
		} catch (Exception e) {
			System.out.println(user.getUserId() + "：推送失败");
			return JSONMessage.failure(e.getMessage());
		}
		return JSONMessage.success();
	}

	@RequestMapping(value = "/pushToAll")
	@ResponseBody
	public JSONMessage pushToAll(HttpServletResponse response, @RequestParam String title,@RequestParam String sub,@RequestParam String img,@RequestParam String url) throws Exception {
		User user = getUser();
		List<Fans> fansList = friendsManager.getFansList(getUser().getUserId());
		List<Integer> toUserIdList = Lists.newArrayList();
		for (Fans fans : fansList) {
			toUserIdList.add(fans.getToUserId());
		}
		MessageBean mb = new MessageBean();
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("title", title);
		jsonObj.put("sub", sub);
		jsonObj.put("img", img);
		jsonObj.put("url", url);
		mb.setContent(jsonObj.toString());
		// mb.setFileName(fileName);
		mb.setFromUserId(user.getUserId() + "");
		mb.setFromUserName(user.getNickname());
		// mb.setObjectId(objectId);
		mb.setTimeSend(DateUtil.currentTimeSeconds());
		// mb.setToUserId(fans.getToUserId() + "");
		// mb.setToUserName(toUserName);
		mb.setType(80);
		try {
			ThreadUtil.executeInThread(new Callback() {
				
				@Override
				public void execute(Object obj) {
					KXMPPServiceImpl.getInstance().send(user, toUserIdList, mb.toString());
					
				}
			});
			
		} catch (Exception e) {
			System.out.println(user.getUserId() + "：推送失败");
			return JSONMessage.failure(e.getMessage());
		}
		return JSONMessage.success();
	}
	//
	@RequestMapping(value="/manyToAll")
	@ResponseBody
	public void many(HttpServletResponse response,HttpServletRequest request,@RequestParam String[] title,@RequestParam String[] url,@RequestParam String[] img) throws ServletException, IOException{
		User user = getUser();
		List<Fans> fansList = friendsManager.getFansList(getUser().getUserId());
		List<Integer> toUserIdList = Lists.newArrayList();
		for (Fans fans : fansList) {
			toUserIdList.add(fans.getToUserId());
		}
		List<Object> list=new ArrayList<Object>();
		JSONObject jsonObj=null;
		for(int i=0;i<title.length;i++){
			jsonObj=new JSONObject();
			jsonObj.put("title",title[i]);
			jsonObj.put("url", url[i]);
			jsonObj.put("img", img[i]);
			list.add(jsonObj);
		}
		MessageBean messageBean=new MessageBean();
		messageBean.setContent(list.toString());
		messageBean.setFromUserId(user.getUserId() + "");
		messageBean.setFromUserName(user.getNickname());
		messageBean.setTimeSend(DateUtil.currentTimeSeconds());
		messageBean.setType(81);
		try {
			ThreadUtil.executeInThread(new Callback() {
				
				@Override
				public void execute(Object obj) {
					KXMPPServiceImpl.getInstance().send(user, toUserIdList, messageBean.toString());
					
				}
			});
		} catch (Exception e) {
			System.out.println(user.getUserId() + "：推送失败");
			//return JSONMessage.failure(e.getMessage());
		}
		
		request.getRequestDispatcher("/mp/many").forward(request,response);
		//return JSONMessage.success();
		
	}
}
