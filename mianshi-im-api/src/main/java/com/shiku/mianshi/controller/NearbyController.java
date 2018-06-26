package com.shiku.mianshi.controller;

import java.util.Calendar;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.example.NearbyUser;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.vo.User;

/**
 * 附近接口
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/nearby")
public class NearbyController {

	@Resource(name = "dsForRW")
	private Datastore dsForRW;

	// @Autowired
	// private NearbyManager nearbyManager;
	//
	// @RequestMapping(value = "/user")
	// public JSONMessage getUserList(@ModelAttribute NearbyUser poi) {
	// JSONMessage jMessage;
	// try {
	// Object data = nearbyManager.getIMUserList(poi);
	// jMessage = JSONMessage.success(null, data);
	// } catch (Exception e) {
	// jMessage = JSONMessage.error(e);
	// }
	// return jMessage;
	// }

	
	//附近的用户
	@RequestMapping(value = "/user")
	public JSONMessage nearbyUser(@ModelAttribute NearbyUser poi) {
		JSONMessage jMessage = null;
		try {
			// List<Integer> userIdList =
			// friendsManager.getFriendsIdList(ReqUtil
			// .getUserId());
			/*if(100>poi.getPageSize())
			poi.setPageSize(100);*/
			Query<User> q = dsForRW.createQuery(User.class);
			// if (null != userIdList && !userIdList.isEmpty())
			// q.filter("userId nin", userIdList);
			
			int distance=poi.getDistance();
			Double d=0d;
			if(0==distance)
				distance=ConstantUtil.getAppDefDistance();
				d=distance/KConstants.LBS_KM;
			if (0 != poi.getLatitude() && 0 != poi.getLongitude())
				q.field("loc").near(poi.getLongitude(), poi.getLatitude(),d);
			if (!StringUtil.isEmpty(poi.getNickname())) {
				// q.field("nickname").contains(poi.getNickname());
				// q.field("desc").contains(poi.getNickname());
				// q.field("telephone").contains(poi.getNickname());
				q.or(q.criteria("nickname").containsIgnoreCase(poi.getNickname()),
						q.criteria("description").containsIgnoreCase(poi.getNickname()),
						q.criteria("telephone").containsIgnoreCase(poi.getNickname()));
			}
			if (null != poi.getSex()) {
				q.field("sex").equal(poi.getSex());
			}
			if (null != poi.getActive() && 0 != poi.getActive()) {
				q.field("active").greaterThanOrEq(DateUtil.currentTimeSeconds() - poi.getActive()*86400000);
				q.field("active").lessThanOrEq(DateUtil.currentTimeSeconds());
				
			}
			/*if (null != poi.getMinAge() && null != poi.getMaxAge()) {
				int year = Calendar.getInstance().get(Calendar.YEAR);
				long start = DateUtil.toSeconds((year - poi.getMaxAge()) + "-01-01");
				long end = DateUtil.toSeconds((year - poi.getMinAge()) + "-12-31");

				q.field("birthday").greaterThanOrEq(start);
				q.field("birthday").lessThanOrEq(end);
			}*/
			q.offset(poi.getPageIndex() * (poi.getPageSize())).limit(poi.getPageSize());
			Object data = q.asList();
			jMessage = JSONMessage.success(null, data);
		} catch (Exception e) {
			e.printStackTrace();
			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}
	
	
	//最新的用户
	@RequestMapping("/newUser")
	public JSONMessage newUser(@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue="12") int pageSize) {
		JSONMessage jMessage = null;
		try {
		
			Query<User> q = dsForRW.createQuery(User.class);
			q.order("-createTime");  //按创建时间降序排列
			Object data = q.offset(pageIndex * pageSize).limit(pageSize).asList();
			jMessage = JSONMessage.success(null,data);
			
		} catch (Exception e) {
			e.printStackTrace();
			jMessage = JSONMessage.error(e);
		}

		return jMessage;
	}
	
	
	
}
