package com.shiku.mianshi.controller;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.xyz.commons.autoconfigure.KApplicationProperties;
import cn.xyz.commons.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.AdminManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.vo.Config;
import cn.xyz.service.KSMSServiceImpl;

@RestController
public class BasicController {

	@Autowired
	private AdminManager adminManager;
	@Autowired
	private KSMSServiceImpl pushManager;
	@Autowired
	private UserManager userManager;
	@Resource(name = "xmppConfig")
	private KApplicationProperties.XMPPConfig xmppConfig;
	
	
	@RequestMapping(value = "/getCurrentTime")
	public JSONMessage getCurrentTime() {
		return JSONMessage.success(null, cn.xyz.commons.utils.DateUtil.currentTimeSeconds());
	}
	@RequestMapping(value = "/config")
	public JSONMessage getConfig() {
		//Map<String, Object> map=new HashMap<String, Object>();
		Config config=adminManager.getConfig();
		config.setDistance(ConstantUtil.getAppDefDistance());
		return JSONMessage.success(null, config);
	}

	@RequestMapping(value="/initDomain",method={RequestMethod.POST})
	public void initDomain(@RequestParam int port,@RequestParam String domain){
		try {
			Process process = Runtime.getRuntime().exec("sh /opt/modify.sh "+ port + " " +domain);
			int exitValue = process.waitFor();
			System.out.println(exitValue);

			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			// 取得输出流
			String line = "";
			while ((line = reader.readLine()) != null) {
				if(line.indexOf("Application in") > 0){
					HttpUtil.URLGet("http://"+xmppConfig.getHost()+":"+port+"/config",null);
				}
			}
			reader.close();
			process.destroy();
			// 初始化两个系统用户
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/user/debug")
	public JSONMessage getUser(@RequestParam int userId) {
		return JSONMessage.success(null, userManager.getUser(userId));
	}

	@RequestMapping("/basic/randcode/sendSms")
	public JSONMessage sendSms(@RequestParam String telephone,@RequestParam(defaultValue="86") String areaCode
			,@RequestParam(defaultValue="zh") String language,@RequestParam(defaultValue="1") int isRegister) {
		Map<String, Object> params = new HashMap<String, Object>();
		telephone=areaCode+telephone;
		if(1==isRegister){
			if (userManager.isRegister(telephone)){
				params.put("code", "-1");
				return JSONMessage.failureByErrCode(KConstants.ResultCode.PhoneRegistered,language,params);
			}
		}
		String code=null;
		

		
		try {
			
			code=pushManager.sendSmsToInternational(telephone, areaCode,language,code);
			//线程延时返回结果
			Thread.sleep(2000);
			params.put("code", code);
			System.out.println("code >>>  "+code);
			//return JSONMessage.success(null,params);
		} catch (ServiceException e) {
			params.put("code", "-1");
			if(null==e.getResultCode())
				return JSONMessage.failure(e.getMessage());
			return JSONMessage.failureByErr(e, language,params);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JSONMessage.success(null,params);
	}

	@RequestMapping(value = "/config/set", method = RequestMethod.GET)
	public ModelAndView setConfig() {
		//adminManager.getConfig();

		ModelAndView mav = new ModelAndView("config_set");
		mav.addObject("config", adminManager.getConfig());
		return mav;
	}

	@RequestMapping(value = "/config/set", method = RequestMethod.POST)
	public void setConfig(HttpServletRequest request, HttpServletResponse response,@ModelAttribute Config config) throws Exception {
		/*BasicDBObject dbObj = new BasicDBObject();
		for (String key : request.getParameterMap().keySet())
			dbObj.put(key, request.getParameter(key));
		dbObj.put("XMPPHost", dbObj.get("XMPPDomain"));*/
		//
		
		config.XMPPHost=xmppConfig.getHost();
		config.setMeetingHost(config.getFreeswitch());
		adminManager.setConfig(config);
		System.out.println(config.toString());
		response.sendRedirect("/config/set");
	}

	@RequestMapping(value = "/verify/telephone")
	public JSONMessage virifyTelephone(@RequestParam(defaultValue="86") String areaCode,@RequestParam(value = "telephone", required = true) String telephone) {
		telephone=areaCode+telephone;
		return userManager.isRegister(telephone) ? JSONMessage.failure("手机号已注册") : JSONMessage.success("手机号未注册");
	}
}
