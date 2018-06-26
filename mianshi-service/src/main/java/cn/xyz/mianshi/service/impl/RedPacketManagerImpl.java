package cn.xyz.mianshi.service.impl;



import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.util.AliPayUtil;
import com.google.common.collect.Maps;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.mianshi.vo.RedPacket;
import cn.xyz.mianshi.vo.RedReceive;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

@Service
public class RedPacketManagerImpl extends MongoRepository<RedPacket, ObjectId>{
	
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	@Resource(name = "morphia")
	private Morphia morphia;
	@Autowired
	private UserManagerImpl userManager;
	@Autowired
	ConsumeRecordManagerImpl consumeRecordManager;
	public RedPacket saveRedPacket(RedPacket entity){
			ObjectId id= (ObjectId) save(entity).getId();
			entity.setId(id);
			return entity;
	}
	public synchronized JSONMessage getRedPacketById(Integer userId,ObjectId id){
		RedPacket packet=get(id);
		Map<String,Object> map=Maps.newHashMap();
		map.put("packet", packet);
		//判断红包是否超时
		if(DateUtil.currentTimeSeconds()>packet.getOutTime()){
			map.put("list", getRedReceivesByRedId(packet.getId()));
			return JSONMessage.failureAndData("该红包已超过24小时!", map);
		}
		if(1==packet.getType()&&packet.getUserId().equals(userId)){
			map.put("list", getRedReceivesByRedId(packet.getId()));
			return JSONMessage.failureAndData(null, map); //你已经领过了 !
		}
			
		//判断红包是否已领完
		if(packet.getCount()>packet.getReceiveCount()){
			//判断当前用户是否领过该红包
			if(null==packet.getUserIds()||!packet.getUserIds().contains(userId))
				return JSONMessage.success(null,map);
			else {
				map.put("list", getRedReceivesByRedId(packet.getId()));
				return JSONMessage.failureAndData(null, map); //你已经领过了 !
			}
		}else{//红包已经领完了
			map.put("list", getRedReceivesByRedId(packet.getId()));
			return JSONMessage.failureAndData(null,map);
		}
	}
	
	
	public synchronized JSONMessage openRedPacketById(Integer userId,ObjectId id){
		RedPacket packet=get(id);
		Map<String,Object> map=Maps.newHashMap();
		map.put("packet", packet);
		//判断红包是否超时
		if(DateUtil.currentTimeSeconds()>packet.getOutTime()){
			map.put("list", getRedReceivesByRedId(packet.getId()));
			return JSONMessage.failureAndData("该红包已超过24小时!", map);
		}
		//判断红包是否已领完
		if(packet.getCount()>packet.getReceiveCount()){
			//判断当前用户是否领过该红包
			//
			if(null==packet.getUserIds()||!packet.getUserIds().contains(userId)){
				packet=openRedPacket(userId, packet);
				map.put("packet", packet);
				map.put("list", getRedReceivesByRedId(packet.getId()));
				return JSONMessage.success(null,map);
			}
			else {
				map.put("list", getRedReceivesByRedId(packet.getId()));
				return JSONMessage.failureAndData(null, map); //你已经领过了 !
			}
		}else{ //你手太慢啦  已经被领完了
			map.put("list", getRedReceivesByRedId(packet.getId()));
			return JSONMessage.failureAndData("你手太慢啦  已经被领完了!",map);
		}
	}
	private synchronized RedPacket openRedPacket(Integer userId,RedPacket packet){
		int overCount= packet.getCount()-packet.getReceiveCount();
		User user=userManager.getUser(userId);
		Double money=0.0;
		//普通红包
		if(1==packet.getType())
			money=packet.getMoney()/packet.getCount();
		else  //拼手气红包或者口令红包
			money=getRandomMoney(overCount, packet.getOver());
		
			packet.setOver(packet.getOver()-money);
			packet.getUserIds().add(userId);
		UpdateOperations<RedPacket> ops=createUpdateOperations();
			ops.set("receiveCount", packet.getReceiveCount()+1);
			ops.set("over",packet.getOver());
			ops.set("userIds", packet.getUserIds());
			if(0==packet.getOver()){
				ops.set("status", 2);
				packet.setStatus(2);
			}
				updateAttributeByOps(packet.getId(), ops);
			
			
		//实例化一个红包接受对象
		RedReceive receive=new RedReceive();
			receive.setMoney(money);
			receive.setUserId(userId);
			receive.setSendId(packet.getUserId());
			receive.setRedId(packet.getId());
			receive.setTime(DateUtil.currentTimeSeconds());
			receive.setUserName(userManager.getUser(userId).getNickname());
			receive.setSendName(userManager.getUser(packet.getUserId()).getNickname());
			ObjectId id=(ObjectId) dsForRW.save(receive).getId();
			receive.setId(id);
			
				//修改金额
				userManager.rechargeUserMoeny(userId, money, KConstants.MOENY_ADD);
				 final Double num=money;
			MessageBean messageBean=new MessageBean();
			messageBean.setType(KXMPPServiceImpl.OPENREDPAKET);
			messageBean.setFromUserId(user.getUserId().toString());
			messageBean.setFromUserName(user.getNickname());
			if(packet.getRoomJid()!=null){
				messageBean.setObjectId(packet.getRoomJid());
			}
			messageBean.setContent(packet.getId().toString());
			try {
				KXMPPServiceImpl.getInstance().send(packet.getUserId(), messageBean.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			//开启一个线程 添加一条消费记录
			new Thread(new Runnable() {
				@Override
				public void run() {
					String tradeNo=AliPayUtil.getOutTradeNo();
					//创建充值记录
					ConsumeRecord record=new ConsumeRecord();
					record.setUserId(userId);
					record.setTradeNo(tradeNo);
					record.setMoney(num);
					record.setStatus(KConstants.OrderStatus.END);
					record.setType(KConstants.MOENY_ADD);
					record.setPayType(3); //余额支付
					record.setDesc("红包接受");
					record.setTime(DateUtil.currentTimeSeconds());
					consumeRecordManager.save(record);
				}
			}).start();
		return packet;
	}
	
	
	private synchronized Double getRandomMoney(int remainSize,Double remainMoney) {
		    // remainSize 剩余的红包数量
		    // remainMoney 剩余的钱
			Double money=0.0;
		    if (remainSize == 1) {
		        remainSize--;
		         money=(double) Math.round(remainMoney * 100) / 100;
		         System.out.println("=====> "+money);
	            return money;
		    }
		    Random r     = new Random();
		    double min   = 0.01; //
		    double max   = remainMoney / remainSize * 2;
		     money = r.nextDouble() * max;
		    money = money <= min ? 0.01: money;
		    money = Math.floor(money * 100) / 100;
		    System.out.println("=====> "+money);
		    remainSize--;
		    remainMoney -= money;
		    DecimalFormat df = new DecimalFormat("#.00");
		   return Double.valueOf(df.format(money));
	}
	
	
	//根据红包Id 获取该红包的领取记录
	public synchronized List<RedReceive> getRedReceivesByRedId(ObjectId redId){
		return (List<RedReceive>) getEntityListsByKey(RedReceive.class, "redId", redId,"-money");
	}
	//发送的红包
	public List<RedPacket> getSendRedPacketList(Integer userId,int pageIndex,int pageSize){
		Query<RedPacket> q=createQuery().field("userId").equal(userId);
		return q.order("-sendTime").offset(pageIndex*pageSize).limit(pageSize).asList();
	}
	//收到的红包
	public List<RedReceive> getRedReceiveList(Integer userId,int pageIndex,int pageSize){
		return (List<RedReceive>) getEntityListsByKey(RedReceive.class, "userId", userId, "-time", pageIndex, pageSize);
	}
	
	
	//发送的红包
	public Object getRedPacketList(Integer userId,int pageIndex,int pageSize){
		Query<RedPacket> q=createQuery();
		if(0!=userId)
			q.field("userId").equal(userId);
		q.order("-sendTime");
		List<RedPacket> pageData=q.offset(pageIndex*pageSize).limit(pageSize).asList();
		
		long total=q.countAll();
		return new PageVO(pageData, total, pageIndex, pageSize);
	}
	
}
