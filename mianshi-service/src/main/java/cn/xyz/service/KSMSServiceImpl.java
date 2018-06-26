package cn.xyz.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.xyz.commons.autoconfigure.KApplicationProperties.SmsConfig;
import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.jedis.JedisTemplate;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.HttpClientUtil;
import cn.xyz.commons.utils.HttpUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.commons.utils.WebNetEncode;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.vo.SmsRecord;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

@Component
public class KSMSServiceImpl implements ApplicationContextAware {

	

	private String app_id = "";
	private String app_secret = "";
	private String app_template_id_invite = "";
	private String app_template_id_random = "";

	@Autowired
	private JedisTemplate jedisTemplate;

	private static ApplicationContext context;
	
	@Resource(name="smsConfig")
	private SmsConfig smsConfig;

	public static KSMSServiceImpl getInstance() {
		return context.getBean(KSMSServiceImpl.class);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public boolean isAvailable(String telephone, String randcode) {
		String key = String.format(KConstants.Key.RANDCODE, telephone);
		String _randcode = jedisTemplate.get(key);
		return randcode.equals(_randcode);
	}
	
	
	
	public String sendSmsToInternational(String telephone,String areaCode,String language,String code) {
		String key = String.format(KConstants.Key.RANDCODE, telephone);
		Long ttl = jedisTemplate.ttl(key);
		code=jedisTemplate.get(key);
		if (ttl >535) {
			String msg=ConstantUtil.getMsgByCode(KConstants.ResultCode.ManySedMsg+"", language).getValue();
			 msg=MessageFormat.format(msg,ttl-535);
			System.out.println("======  "+msg);
			throw new ServiceException(msg);
		}
		if(null==code)
			code=StringUtil.randomCode();
		final String smsCode=code;
		ThreadUtil.executeInThread(new Callback() {
			
			@Override
			public void execute(Object obj) {
				//String code = StringUtil.randomCode();
				if(1==smsConfig.getOpenSMS()){ //需要发送短信
						String msgId=sendSmsToMs360(telephone,areaCode,smsCode);
				        if(!StringUtil.isEmpty(msgId)){
					        if (!"-".equals(msgId.substring(0, 1))) {
								jedisTemplate.set(key, smsCode);
								jedisTemplate.expire(key, 600);
							} else {
								System.out.println("    发送短信错误      msgId=====>"+msgId);
								throw new ServiceException(KConstants.ResultCode.SedMsgFail,language);
							}
				        }else {//msgId ==null
				        	throw new  ServiceException(KConstants.ResultCode.SedMsgFail,language);
						}
				}else{
					jedisTemplate.set(key, smsCode);
					jedisTemplate.expire(key, 600);
				}
			}
		});
		 return code;
		
	}
	//使用天天国际短信平台发送国际短信
	private String sendSmsToMs360(String telephone,String areaCode,String code){
		 String ip = smsConfig.getHost();
	        int port = smsConfig.getPort();
	        // HTTP 请求工具
	        //"/mt/MT3.ashx"
	        HttpClientUtil util = new HttpClientUtil(ip,  port,smsConfig.getApi());
	        String user = smsConfig.getUsername();//你的用户名 
	        String pwd = smsConfig.getPassword();//你的密码：
	        String ServiceID = "SEND"; //固定，不需要改变
	        String dest = telephone; // 你的目的号码【收短信的电话号码】
	        String sender = "";// 你的原号码,可空【大部分国家原号码带不过去，只有少数国家支持透传，所有一般为空】
	        String msg ="[SHIKU IM], Your verification code is:"+code;//你的短信内容
	        if("86".equals(areaCode)||"886".equals(areaCode)||"852".equals(areaCode))
	        	msg = "[视酷IM],您的验证码为:"+code;
	        // codec=8 Unicode 编码,  3 ISO-8859-1, 0 ASCII
	        // 短信内容 HEX 编码，8 为 UTF-16BE HEX 编码， dataCoding = 8 ,支持所有国家的语言，建议直接使用 8
	        String hex = WebNetEncode.encodeHexStr(8, msg); 
	        hex = hex.trim() + "&codec=8";
	        //System.out.println("POST MT3");
	        // HTTP 封包请求, util.sendPostMessage  返回结果，
	        // 如果是以 “-” 开头的为发送失败，请查看错误代码，否则为MSGID
	      
	        String msgId=util.sendGetMessage(user, pwd, ServiceID, dest, sender, hex);
	        
	        System.out.println("msgid =  "+msgId);
	        //发送短信记录保存到数据库
	        saveSMSToDB(telephone, areaCode, code, msg, msgId);
	        
	        return msgId;
	}
	
	private void saveSMSToDB(String telephone,String areaCode,String code,String content,String msgId){
		SmsRecord sms=ConstantUtil.getSmsPrice(areaCode);
		if(null==sms)
			sms=new SmsRecord();
		sms.setAreaCode(areaCode);
		sms.setTelephone(telephone);
		sms.setCode(code);
		sms.setContent(content);
		sms.setMsgId(msgId);
		sms.setTime(DateUtil.currentTimeSeconds());
		
		ConstantUtil.dsForRW.save(sms);
		
	}

	
	
	public static class Result {
		private String access_token;
		private Integer expires_in;
		private String idertifier;
		private String res_code;
		private String res_message;

		public String getAccess_token() {
			return access_token;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public String getIdertifier() {
			return idertifier;
		}

		public String getRes_code() {
			return res_code;
		}

		public String getRes_message() {
			return res_message;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}

		public void setIdertifier(String idertifier) {
			this.idertifier = idertifier;
		}

		public void setRes_code(String res_code) {
			this.res_code = res_code;
		}

		public void setRes_message(String res_message) {
			this.res_message = res_message;
		}
	}
	public JSONMessage applyVerify(String telephone) {
		String key = MessageFormat.format("randcode:{0}", telephone);
		Long ttl = jedisTemplate.ttl(key);
		if (ttl > 0) {
			throw new ServiceException("请不要频繁请求短信验证码，等待" + ttl + "秒后再次请求");
		}
		JSONMessage jMessage;
		try {
			String param1 = StringUtil.randomCode();
			//String param2 = "2分钟";

			Map<String, String> params = new HashMap<String, String>();
			params.put("param1", param1);
			//params.put("param2", param2);

			Result result = sendSms(app_template_id_random, telephone, params);

			if ("0".equals(result.getRes_code())) {
				Map<String, String> data = new HashMap<String, String>(1);
				data.put("randcode", param1);
				jMessage = JSONMessage.success(null, data);
				jedisTemplate.set(key, param1);
				jedisTemplate.expire(key, 120);
			} else {
				jMessage = JSONMessage.failure(result.getRes_message());
			}
		} catch (Exception e) {
			jMessage = KConstants.Result.InternalException;
		}

		return jMessage;
	}

	public String getApp_id() {
		return app_id;
	}

	public String getApp_secret() {
		return app_secret;
	}

	public String getApp_template_id_invite() {
		return app_template_id_invite;
	}

	public String getApp_template_id_random() {
		return app_template_id_random;
	}

	public String getToken() throws Exception {
		String token = jedisTemplate.get("open.189.access_token");
		if (StringUtil.isNullOrEmpty(token)) {
			Result result = getTokenObj();
			if ("0".equals(result.getRes_code())) {
				jedisTemplate.execute(new JedisCallbackVoid() {

					@Override
					public void execute(Jedis jedis) {
						Pipeline pipe = jedis.pipelined();
						pipe.set("open.189.access_token", result.getAccess_token());
						pipe.expire("open.189.access_token", result.getExpires_in());
						pipe.sync();
					}

				});
				token = result.getAccess_token();
			}
		}
		return token;
	}

	public Result getTokenObj() throws Exception {
		HttpUtil.Request request = new HttpUtil.Request();
		request.setSpec("https://oauth.api.189.cn/emp/oauth2/v3/access_token");
		request.setMethod(HttpUtil.RequestMethod.POST);
		request.getData().put("grant_type", "client_credentials");
		request.getData().put("app_id", app_id);
		request.getData().put("app_secret", app_secret);
		Result result = HttpUtil.asBean(request, Result.class);
		return result;
	}

	public JSONMessage sendInvite(String telephone, String companyName, String username, String password) {
		JSONMessage jMessage;

		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("param1", companyName);
			params.put("param2", username);
			params.put("param3", password);
			Result result = sendSms(app_template_id_invite, telephone, params);

			if ("0".equals(result.getRes_code())) {
				jMessage = JSONMessage.success();
			} else {
				jMessage = JSONMessage.failure(result.getRes_message());
			}
		} catch (Exception e) {
			jMessage = KConstants.Result.InternalException;
		}

		return jMessage;
	}

	private Result sendSms(String template_id, String telephone, Map<String, String> params) throws Exception {
		HttpUtil.Request request = new HttpUtil.Request();
		request.setSpec("http://api.189.cn/v2/emp/templateSms/sendSms");
		request.setMethod(HttpUtil.RequestMethod.POST);
		request.getData().put("app_id", app_id);
		request.getData().put("access_token", getToken());
		request.getData().put("acceptor_tel", telephone);
		request.getData().put("template_id", template_id);
		request.getData().put("template_param", JSON.toJSONString(params));
		request.getData().put("timestamp", DateUtil.getFullString());

		return HttpUtil.asBean(request, Result.class);
	}

}
