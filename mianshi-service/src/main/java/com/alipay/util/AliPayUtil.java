package com.alipay.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.alipay.config.AlipayConfig;
import com.alipay.sign.RSA;

import cn.xyz.commons.utils.PayConstants;



public class AliPayUtil {
	
	 public static String getOutTradeNo() {
		 int r1 = (int) (Math.random() * (10));// 产生2个0-9的随机数
			int r2 = (int) (Math.random() * (10));
			long now = System.currentTimeMillis();// 一个13位的时间戳
			String id = String.valueOf(r1) + String.valueOf(r2)
					+ String.valueOf(now);// 订单ID
			return id;
	   }
	 /**
	    * create the order info. 创建订单信息
	    *
	    */
	 public static String getOrderInfo(String subject, String body, String price,String orderNo) {

	      // 签约合作者身份ID
	      String orderInfo = "partner=" + "\"" + AlipayConfig.partner + "\"";

	      // 签约卖家支付宝账号
	      orderInfo += "&seller_id=" + "\"" + AlipayConfig.SELLER + "\"";

	      // 商户网站唯一订单号
	      orderInfo += "&out_trade_no=" + "\"" +orderNo+ "\"";

	      // 商品名称
	      orderInfo += "&subject=" + "\"" + subject + "\"";

	      // 商品详情
	      orderInfo += "&body=" + "\"" + body + "\"";

	      // 商品金额
	      orderInfo += "&total_fee=" + "\"" + price + "\"";

	      // 服务器异步通知页面路径  www.youjob.co:8094/m  http://notify.msp.hk/notify.htm
	      	//http://itldy.hicp.net:28035/user/recharge/aliPayCallBack
	      //http://www.youjob.co:8094/user/recharge/aliPayCallBack
	      orderInfo += "&notify_url=" + "\"" + PayConstants.ALIPAY_NOTIFY_URL + "\"";

	      // 服务接口名称， 固定值
	      orderInfo += "&service=\"mobile.securitypay.pay\"";

	      // 支付类型， 固定值
	      orderInfo += "&payment_type=\"1\"";

	      // 参数编码， 固定值
	      orderInfo += "&_input_charset=\"utf-8\"";

	      // 设置未付款交易的超时时间
	      // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
	      // 取值范围：1m～15d。
	      // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
	      // 该参数数值不接受小数点，如1.5h，可转换为90m。
	      orderInfo += "&it_b_pay=\"30m\"";

	      // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
	      // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

	      // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
	      orderInfo += "&return_url=\"m.alipay.com\"";

	      // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
	      // orderInfo += "&paymethod=\"expressGateway\"";
	      return orderInfo;
	   }
	   
	   
	   /**
	    * sign the order info. 对订单信息进行签名
	    *
	    * @param content
	    *            待签名订单信息
	    */
	 public static String sign(String content) {
	      return RSA.sign(content, AlipayConfig.private_key,AlipayConfig.input_charset);
	   }

	   /**
	    * get the sign type we use. 获取签名方式
	    *
	    */
	  /* private String getSignType() {
	      return "sign_type=\"RSA\"";
	   }*/
	 
	 /**
		 * 解析支付宝支付成功后返回的数据
		 * 
		 * @param request
		 * @return
		 * @throws UnsupportedEncodingException
		 */
		@SuppressWarnings("rawtypes")
		public static Map<String, String> getAlipayResult(javax.servlet.http.HttpServletRequest request) {
			// 获取支付宝POST过来反馈信息
			Map<String, String> params;
			params = new TreeMap<String, String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {

				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			return params;
		}
}
