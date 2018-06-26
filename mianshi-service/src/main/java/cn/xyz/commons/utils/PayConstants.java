package cn.xyz.commons.utils;

/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */

/**
 * 支付宝服务窗环境常量（demo中常量只是参考，需要修改成自己的常量值）
 * 
 * @author taixu.zqq
 * @version $Id: AlipayServiceConstants.java, v 0.1 2014年7月24日 下午4:33:49
 *          taixu.zqq Exp $
 */
public class PayConstants {
	public static final String configUrl="config";
	/** 扫码成功的编码 */
	public static final String SUCCESS_CODE = "10000";

	/** 扫码失败的编码 */
	public static final String FAIL_CODE = "40004";

	/** 支付宝公钥-从支付宝服务窗获取 */
	//public static final String ALIPAY_PUBLIC_KEY =PropertiesUtils.getProperty(configUrl, "alipay_public_key");

	/** 签名编码-视支付宝服务窗要求 */
	public static final String SIGN_CHARSET = "UTF-8";

	/** 字符编码-传递给支付宝的数据编码 */
	public static final String CHARSET = "UTF-8";

	/** 签名类型-视支付宝服务窗要求 */
	public static final String SIGN_TYPE = "RSA";


	/** 服务窗appId */
	// TODO !!!! 注：该appId必须设为开发者自己的服务窗id 这里只是个测试id
	//public static final String APP_ID = ;
	// 收款支付宝账号
	//public static final String SELLER_EMAIL =;
	// 商户的私钥
	//public static final String KEY =;

	// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志文件夹路径
	public static final String LOG_PATH = "D:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static final String INPUT_CHARSET = "UTF-8";

	// 签名方式 不需修改
	public static final String MD5_SIGNTYPE = "MD5";
	public static final String RSA_SIGNTYPE = "RSA";

	// 开发者请使用openssl生成的密钥替换此处
	// 请看文档：https://fuwu.alipay.com/platform/doc.htm#2-1接入指南
	// TODO !!!! 注：该私钥为测试账号私钥 开发者必须设置自己的私钥 , 否则会存在安全隐患
	//public static final String PRIVATE_KEY = ;

	// TODO !!!! 注：该公钥为测试账号公钥 开发者必须设置自己的公钥 ,否则会存在安全隐患
	//public static final String PUBLIC_KEY = ;

	/** 支付宝网关 */
	public static final String ALIPAY_GATEWAY = "https://openapi.alipay.com/gateway.do";

	/** 授权访问令牌的授权类型 */
	public static final String GRANT_TYPE = "authorization_code";
	
	//用于支付宝手机支付
	public static String ALI_PUBLIC_KEY  = "";
	
	
	/**
	 * 微信相关
	 */
	//调用统一下单接口(微信预支付)地址
	public final static String PREPAY_ID_URL="https://api.mch.weixin.qq.com/pay/unifiedorder";
	public final static String QUERY_URL="https://api.mch.weixin.qq.com/pay/orderquery";
	public final static String WX_APPID="";
	public final static String WXAPPSECRET="";
	public final static String WX_PARTNERKEY="";
	public final static String WXMCH_ID="";
	public final static String WXSPBILL_CREATE_IP="";
	public final static String TRADE_TYPE_JS="NATIVE";
	public final static String WX_JSAPI="APP";
	
	/**支付回调url*/
	//http://itldy.hicp.net:28035/user/recharge/aliPayCallBack
	//http://www.youjob.co:8094/user/recharge/aliPayCallBack
	public final static String WXPAY_NOTIFY_URL="http://www.youjob.co:8092/user/recharge/wxPayCallBack";
	public final static String WXJSPAY_NOTIFY_URL="http://www.youjob.co:8092/user/recharge/wxPayCallBack";
	
	public final static String ALIPAY_NOTIFY_URL="http://www.youjob.co:8092/user/recharge/aliPayCallBack";
	public final static String ALIPAY_WAP_NOTIFY_URL="";
}