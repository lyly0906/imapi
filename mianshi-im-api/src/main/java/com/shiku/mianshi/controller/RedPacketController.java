package com.shiku.mianshi.controller;


import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alipay.util.AliPayUtil;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.impl.ConsumeRecordManagerImpl;
import cn.xyz.mianshi.service.impl.RedPacketManagerImpl;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.mianshi.vo.RedPacket;

@RestController
public class RedPacketController {
	@Resource(name = "dsForRW")
	Datastore dsForRW;

	@Autowired
	private RedPacketManagerImpl service;
	@Autowired
	private UserManagerImpl userManager;
	@Autowired
	ConsumeRecordManagerImpl consumeRecordManager;

	@RequestMapping("/redPacket/sendRedPacket")
	public JSONMessage sendRedPacket(RedPacket packet) {
		Integer userId=ReqUtil.getUserId();
		if(userManager.getUser(userId).getBalance()<packet.getMoney()){
			//余额不足
			return JSONMessage.failure("余额不足,请先充值!");
		}
		packet.setUserId(userId);
		packet.setUserName(userManager.getUser(userId).getNickname());
			packet.setOver(packet.getMoney());
			long time=DateUtil.currentTimeSeconds();
			packet.setSendTime(time);
			packet.setOutTime(time+KConstants.Expire.DAY1);
			Object data=service.saveRedPacket(packet);
				//修改金额
				userManager.rechargeUserMoeny(userId, packet.getMoney(), KConstants.MOENY_REDUCE);
			//开启一个线程 添加一条消费记录
			new Thread(new Runnable() {
				@Override
				public void run() {
					String tradeNo=AliPayUtil.getOutTradeNo();
					//创建充值记录
					ConsumeRecord record=new ConsumeRecord();
					record.setUserId(userId);
					record.setTradeNo(tradeNo);
					record.setMoney(packet.getMoney());
					record.setStatus(KConstants.OrderStatus.END);
					record.setType(KConstants.MOENY_REDUCE);
					record.setPayType(3); //余额支付
					record.setDesc("红包发送");
					record.setTime(DateUtil.currentTimeSeconds());
					consumeRecordManager.save(record);
				}
			}).start();
			
		return JSONMessage.success(null,data);
	}
	//获取红包详情
	@RequestMapping("/redPacket/getRedPacket")
	public JSONMessage getRedPacket(String id) {
		JSONMessage result=service.getRedPacketById(ReqUtil.getUserId(), ReqUtil.parseId(id));
		//System.out.println("获取红包  ====>  "+result);
		return result;
	}
	//打开红包
	@RequestMapping("/redPacket/openRedPacket")
	public JSONMessage openRedPacket(String id) {
		JSONMessage result=service.openRedPacketById(ReqUtil.getUserId(), ReqUtil.parseId(id));
		//System.out.println("打开红包  ====>  "+result);
		return result;
	}
	//查询发出的红包
	@RequestMapping("/redPacket/getSendRedPacketList")
	public JSONMessage getSendRedPacketList(@RequestParam(defaultValue="0")int pageIndex,@RequestParam(defaultValue="10")int pageSize) {
		Object data=service.getSendRedPacketList(ReqUtil.getUserId(),pageIndex,pageSize);
		return JSONMessage.success(null, data);
	}
	//查询收到的红包
	@RequestMapping("/redPacket/getRedReceiveList")
	public JSONMessage getRedReceiveList(@RequestParam(defaultValue="0")int pageIndex,@RequestParam(defaultValue="10")int pageSize) {
		Object data=service.getRedReceiveList(ReqUtil.getUserId(),pageIndex,pageSize);
		return	JSONMessage.success(null, data);
	}
	
	
	
	
}
