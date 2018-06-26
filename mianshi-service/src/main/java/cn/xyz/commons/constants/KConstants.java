package cn.xyz.commons.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cn.xyz.commons.vo.JSONMessage;


/**
 * 常量
 * 
 * @author luorc
 * 
 */
public interface KConstants {
	
	
	public static final String PAGE_INDEX = "0";
	public static final String PAGE_SIZE = "15";
	
	public static final int MOENY_ADD = 1; //金钱增加
	public static final int MOENY_REDUCE = 2; //金钱减少
	public static final double LBS_KM=111.01;
	public static final int LBS_DISTANCE=50;
	
	
	//订单状态
	public interface OrderStatus {
		public static final int CREATE=0;//创建
		public static final int END=1;//成功
		public static final int DELETE=-1;//删除
		//public static final int payEND=1;
	}
	//支付方式
	public interface PayType {
		public static final int ALIPAY=1;//支付宝支付
		public static final int WXPAY=2;//微信支付
	}
	public interface Key {
		public static final String OEPN189_ACCESS_TOKEN = "KSMSService:access_token";
		public static final String RANDCODE = "KSMSService:randcode:%s";
	}

	//public static final KServiceException InternalException = new KServiceException(KConstants.ErrCode.InternalException,KConstants.ResultMsg.InternalException);

	public interface Expire {
		static final int DAY1 = 86400;
		static final int DAY7 = 604800;
		static final int HOUR12 = 43200;
		static final int HOUR=3600;
	}

	public interface MsgType {
		static final Byte Text = 1;
		static final Byte Audio = 3;
		static final Byte Image = 2;
		static final Byte Video = 4;
		static final Byte Image_Audio = 5;
		static final Byte AddressBook = 32;//通讯好友
	}
	public interface SystemNo{
		static final int System=10000;//系统号码
		static final int NewKFriend=10001;//新朋友
		static final int Circle=10002;//商务圈
		static final int AddressBook=10003;//通讯录
		static final int Function=10004;//专长审核
		static final int Notice=10006;//系统通知
		
	}

	public interface Result {
		static final JSONMessage InternalException = new JSONMessage(1020101, "接口内部异常");
		static final JSONMessage ParamsAuthFail = new JSONMessage(1010101, "请求参数验证失败，缺少必填参数或参数错误");
		static final JSONMessage TokenEillegal = new JSONMessage(1030101, "缺少访问令牌");
		static final JSONMessage TokenInvalid = new JSONMessage(1030102, "访问令牌过期或无效");
		static final JSONMessage AUTH_FAILED = new JSONMessage(1030103, "权限验证失败");
	}
	public interface ResultCode {
		//接口调用成功
		static final int Success = 1;
		//接口调用失败
		static final int Failure = 0;
		//请求参数验证失败，缺少必填参数或参数错误
		static final int ParamsAuthFail = 1010101;
		//缺少请求参数：
		static final int ParamsLack = 1010102;
		//接口内部异常
		static final int InternalException = 1020101;
		//链接已失效
		static final int Link_Expired=1020102;
		//缺少访问令牌
		static final int TokenEillegal = 1030101;
		//访问令牌过期或无效
		static final int TokenInvalid = 1030102;
		//权限验证失败
		static final int AUTH_FAILED = 1030103;
		//帐号不存在
		static final int AccountNotExist = 1040101;
		//帐号或密码错误
		static final int AccountOrPasswordIncorrect = 1040102;
		//原密码错误
		static final int OldPasswordIsWrong = 1040103;
		//短信验证码错误或已过期
		static final int VerifyCodeErrOrExpired = 1040104;
		//发送验证码失败,请重发!
		static final int SedMsgFail = 1040105;
		//请不要频繁请求短信验证码，等待{0}秒后再次请求
		static final int ManySedMsg = 1040106;
		//手机号码已注册!
		static final int PhoneRegistered = 1040107;
		//余额不足
		static final int InsufficientBalance = 1040201;
		//账号余额不足不能发布职位,请先充值!
		static final int PublishVerify_FAILED = 1040202;
	
		//没有选择支付方式!
		static final int NotSelectPayType = 1040301;
		//支付宝支付后回调出错：
		static final int AliPayCallBack_FAILED = 1040302;
		//你没有权限删除!
		static final int NotPermissionDelete = 1040303;
		
		
		
		
		
	
	}
	
	

	public interface Version {

		/**
		 * 个人用户、牛人
		 */
		static final int P = 1;

		/**
		 * 企业用户、老板
		 */
		static final int B = 2;

		/**
		 * 猎人用户
		 */
		static final int M = 3;
	}
	
	
	
	
	
	
	
	
	
public interface ResultMsgs {
		
		static final Map<String,String> InternalException =new HashMap<String,String>(){
			{
				put("zh", "接口内部异常");
				put("en", "An exception occurs to internal interface.");
			}
		};
		static final Map<String,String> ParamsAuthFail =new HashMap<String,String>(){
			{
				put("zh", "请求参数验证失败，缺少必填参数或参数错误");
				put("en", "Request for parameter verification failed due to lack of required parameters or wrong parameters");
			}
		};
		static final Map<String,String> TokenEillegal =new HashMap<String,String>(){
			{
				put("zh", "缺少访问令牌");
				put("en", "Lack of access token");
			}
		};
		static final Map<String,String> TokenInvalid =new HashMap<String,String>(){
			{
				put("zh", "访问令牌过期或无效");
				put("en", "Access token gets expired or invalid");
			}
		};
		static final Map<String,String> AUTH_FAILED =new HashMap<String,String>(){
			{
				put("zh", "权限验证失败");
				put("en", "Permission verification failed");
			}
		};
		
		static final Map<String,String> AliPayCallBack_FAILED =new HashMap<String,String>(){
			{
				put("zh", "支付宝支付后回调出错：");
				put("en", "Retracement error occurs after payment through Alipay");
			}
		};
		
		static final Map<String,String> AccountNotExist =new HashMap<String,String>(){
			{
				put("zh", "帐号不存在");
				put("en", "The account isn't existed.");
			}
		};
		static final Map<String,String> AccountOrPasswordIncorrect =new HashMap<String,String>(){
			{
				put("zh", "帐号或密码错误");
				put("en", "The account or password is wrong");
			}
		};
		static final Map<String,String> OldPasswordIsWrong =new HashMap<String,String>(){
			{
				put("zh", "原密码错误");
				put("en", "The original password is wrong");
			}
		};
		static final Map<String,String> VerifyCodeErrOrExpired =new HashMap<String,String>(){
			{
				put("zh", "短信验证码错误或已过期");
				put("en", "The verification code is wrong or expired");
			}
		};
		static final Map<String,String> InsufficientBalance =new HashMap<String,String>(){
			{
				put("zh", "余额不足");
				put("en", "Insufficient balance");
			}
		};
		
	}

	public interface ErrCodes {
		static final String InternalException ="InternalException";
		static final String ParamsAuthFail ="ParamsAuthFail";
		static final String TokenEillegal ="TokenEillegal";
		static final String TokenInvalid ="TokenInvalid";
		static final String AUTH_FAILED ="AUTH_FAILED";
		static final String PublishVerify_FAILED ="PublishVerify_FAILED";
		static final String AliPayCallBack_FAILED="AliPayCallBack_FAILED";
		static final String NotExistSendResume_FAILED="NotExistSendResume_FAILED";
		static final String NotCreateResume="NotCreateResume";
		static final String NotSelectPayType="NotSelectPayType";
		static final String PhoneRegistered="PhoneRegistered";
		static final String AccountNotExist="AccountNotExist";
		static final String AccountOrPasswordIncorrect="AccountOrPasswordIncorrect";
		static final String OldPasswordIsWrong="OldPasswordIsWrong";
		static final String VerifyCodeErrOrExpired="VerifyCodeErrOrExpired";
		static final String InsufficientBalance="InsufficientBalance";
		static final String OpenTalkResumeNotDetailed="OpenTalkResumeNotDetailed";
		static final String Resume_ConditionNotSatisfied="Resume_ConditionNotSatisfied";
		static final String NotTalk_Oneself="NotTalk_Oneself";	
	}
	
	

}
