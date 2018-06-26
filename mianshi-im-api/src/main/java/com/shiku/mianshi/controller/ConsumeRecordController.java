package com.shiku.mianshi.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.util.AliPayParam;
import com.alipay.util.AliPayUtil;
import com.alipay.util.AlipayNotify;
import com.wxpay.utils.WXNotify;
import com.wxpay.utils.WXPayUtil;
import com.wxpay.utils.WxPayResult;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.BeanUtils;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.impl.ConsumeRecordManagerImpl;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.vo.ConsumeRecord;

@RestController
public class ConsumeRecordController extends AbstractController {

	@Autowired
	private ConsumeRecordManagerImpl service;
	@Autowired
	private UserManagerImpl userManager;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@RequestMapping(value="/user/recharge/aliPayCallBack",method=RequestMethod.POST)
	public void aliPayCallBack(HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter out = null;
		try {
			Map<String, String> result=AliPayUtil.getAlipayResult(request);
			String tradeNo=result.get("out_trade_no");
			ConsumeRecord entity=service.getConsumeRecordByNo(tradeNo);
			if(null==entity)
				logger.info("交易订单号不存在！-----"+tradeNo);
			else if(0!=entity.getStatus())
				logger.info(tradeNo+"===status==="+entity.getStatus()+"=======交易已处理或已取消!");
			else if(AlipayNotify.verify(result)){
				if("TRADE_SUCCESS".equals(result.get("trade_status"))){
					//把支付宝返回的订单信息存到数据库
					AliPayParam aliCallBack=new AliPayParam();
					BeanUtils.populate(aliCallBack, result);
					if(aliCallBack.getTotal_fee().equals(entity.getMoney())){
						entity.setStatus(KConstants.OrderStatus.END);;
						service.update(entity.getId(), entity);
								service.saveEntity(aliCallBack);
								userManager.rechargeUserMoeny(entity.getUserId(), entity.getMoney(), KConstants.MOENY_ADD);
								logger.info(tradeNo+"------------支付宝支付成功!");
						// 给支付宝务端发送接收数据的成功信息
						out = response.getWriter();
						out.println("success");
					}else{
						logger.info("支付宝数据返回错误!");
						logger.info("localhost:Money---------"+entity.getMoney());
						logger.info("Alipay:Total_fee---------"+aliCallBack.getTotal_fee());
					}
				}else 
					logger.info("支付宝支付失败！");
			}else{
				logger.info("异步回调的签名sign校验失败!");
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally {
			if (out != null)
				out.close();
		}
		
		
	}

	@RequestMapping("/user/recharge/list")
	public JSONMessage getList(@RequestParam(defaultValue="0")int pageIndex,@RequestParam(defaultValue="10")int pageSize) {
		Object data = service.reChargeList(ReqUtil.getUserId(), pageIndex, pageSize);
		return JSONMessage.success(null, data);
	}
	@RequestMapping("/user/consumeRecord/list")
	public JSONMessage consumeRecordList(@RequestParam(defaultValue="0")int pageIndex,@RequestParam(defaultValue="10")int pageSize) {
		Object data = service.consumeRecordList(ReqUtil.getUserId(), pageIndex, pageSize);
		return JSONMessage.success(null, data);
	}
	@RequestMapping("/recharge/delete")
	public JSONMessage delete(String id) {
		 service.deleteById(ReqUtil.parseId(id));
		return JSONMessage.success();
	}
	
	@RequestMapping(value="/user/recharge/wxPayCallBack",method=RequestMethod.POST)
	public void wxPayCallBack(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		//把如下代码贴到的你的处理回调的servlet 或者.do 中即可明白回调操作
		logger.info("微信支付回调数据开始");
		BufferedOutputStream out = null;
		String inputLine;
		String notityXml = "";
		String resXml = "";
		try {
			while ((inputLine = request.getReader().readLine()) != null) {
				notityXml += inputLine;
			}
			request.getReader().close();
			
			Map<String,String> m = WXNotify.parseXmlToList2(notityXml);
			logger.info("接收到的报文：" + m);
				String tradeNo=m.get("out_trade_no");
				ConsumeRecord entity=service.getConsumeRecordByNo(tradeNo);
				if(null==entity)
					logger.info("交易订单号不存在！-----"+tradeNo);
				else if(0!=entity.getStatus())
					logger.info(tradeNo+"===status==="+entity.getStatus()+"=======交易已处理或已取消!");
				else if("SUCCESS".equals(m.get("result_code"))){
					boolean flag=Double.valueOf(m.get("cash_fee"))==entity.getMoney()*100;
					if(flag){
						 //logger.info("支付金额比较"+m.get("cash_fee")+"=="+entity.getMoney()*100+"=======>"+flag);
						WxPayResult wpr = WXPayUtil.mapToWxPayResult(m);
						//支付成功
						resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
						entity.setStatus(KConstants.OrderStatus.END);
						service.update(entity.getId(), entity);
						userManager.rechargeUserMoeny(entity.getUserId(), entity.getMoney(), KConstants.MOENY_ADD);
						service.saveEntity(wpr);
						logger.info(tradeNo+"========>>微信支付成功!");
					}else{
						logger.info("支付宝数据返回错误!");
						logger.info("localhost:Money---------"+entity.getMoney()*100);
						logger.info("Wxpay:Cash_fee---------"+m.get("cash_fee"));
					}
				}else{
					logger.info("微信支付失败======"+m.get("return_msg"));
					resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
					+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
				}
				 out = new BufferedOutputStream(response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (out != null)
				out.close();
		}
		
	}

}
