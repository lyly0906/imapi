package cn.xyz.mianshi.scheduleds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.TaskManagementConfigUtils;
import org.springframework.stereotype.Component;

import com.alipay.util.AliPayUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cn.xyz.commons.autoconfigure.KApplicationProperties.AppConfig;
import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.mianshi.service.impl.ConsumeRecordManagerImpl;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.mianshi.vo.RedPacket;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.UserStatusCount;

@Component
@EnableScheduling
public class CommTask implements ApplicationListener<ApplicationContextEvent>{

	
	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	
	public static final int STATUS_START=1;//红包发出状态
	public static final int STATUS_END=2;//已领完红包状态
	public static final int STATUS_RECEDE=-1;//已退款红包状态
	
	
	//public static final int STATUS_RECEDE=3;//已退款红包状态
	@Autowired
	private UserManagerImpl userManager;
	
	@Autowired
	private ConsumeRecordManagerImpl recordManager;
	@Resource(name = "appConfig")
	private AppConfig appConfig;
	@Resource(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
	private ScheduledAnnotationBeanPostProcessor scheduledProcessor;
	 public CommTask() {
			super();
	 }
	 
	 @Override
	public void onApplicationEvent(ApplicationContextEvent event) {
		 if(event.getApplicationContext().getParent() != null)
			 return;
		 //root application context 没有parent，他就是老大.
         //需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
		
				
				if(0==appConfig.getOpenTask()){
						
						ThreadUtil.executeInThread(new Callback() {
							@Override
							public void execute(Object obj) {
								 try {
										Thread.currentThread().sleep(10000);
										scheduledProcessor.destroy();
										System.out.println("====定时任务被关闭了=======》");
								 	} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							}
						});
						
				   }else System.out.println("====定时任务开启中=======》");
	}
	
	@Scheduled(cron = "0 0 0/1 * * ?")
	public void execute() {
		long start = System.currentTimeMillis();
		autoRefreshRedPackect();
		System.out.println("刷新红包成功,耗时" + (System.currentTimeMillis() - start) + "毫秒");
	}
	
	@Scheduled(cron = "0 0/5 * * * ?")
 	public void refreshUserStatusCount(){
		DBObject q = new BasicDBObject("onlinestate",1);
		
		long count =dsForRW.getCollection(User.class).getCount(q);
		
		UserStatusCount userCount=new UserStatusCount();
		//long count=(long)(Math.random()*(1000-100+1)+100);
		userCount.setType(1);
		userCount.setCount(count);
		userCount.setTime(DateUtil.currentTimeSeconds());
		userManager.saveEntity(userCount);
		System.out.println("刷新用户状态统计======》" +count);
	}
	@Scheduled(cron = "0 0 0/1 * * ?")
 	public void refreshUserStatusHour(){
		
		long currentTime =new Date().getTime()/1000;
		//DBObject q =null;
		Query<UserStatusCount> q=null;
		long startTime=currentTime-KConstants.Expire.HOUR;
			
		long endTime=currentTime;
		
		List<UserStatusCount> counts=null;
		UserStatusCount uCount=null;
		long sum=0;
		
		
			//q = new BasicDBObject();
			q=dsForRW.createQuery(UserStatusCount.class);
			q.enableValidation();
			
			sum=0;
			System.out.println("当前时间:"+DateUtil.TimeToStr(new Date()));
			q.field("time").greaterThanOrEq(startTime);
			q.field("time").lessThan(endTime);
			q.field("type").equal(1);
			counts=q.asList();
			 uCount=new UserStatusCount();
			for (UserStatusCount userStatus : counts) {
				sum+=userStatus.getCount();
			}
			System.out.println("List Size======="+counts.size());
			if(sum>0){
				uCount.setTime(startTime);
				uCount.setType(2);
				uCount.setCount(sum/counts.size());
				userManager.saveEntity(uCount);
				System.out.println("平均用户在线======》" +uCount.getCount());
			}
	}
	@Scheduled(cron = "0 0 10 * * ?")
 	public void refreshUserStatusDay(){
		Date yesterday=DateUtil.getYesterdayMorning();
		//long currentTime =new Date().getTime()/1000;
		//DBObject q =null;
		Query<UserStatusCount> q=null;
		long startTime=yesterday.getTime()/1000;
		long endTime=startTime+KConstants.Expire.DAY1;
		List<UserStatusCount> counts=null;
		UserStatusCount uCount=null;
		long sum=0;
		
		
			//q = new BasicDBObject();
			q=dsForRW.createQuery(UserStatusCount.class);
			q.enableValidation();
			sum=0;
			System.out.println("Day_Count 当前时间:"+DateUtil.TimeToStr(new Date()));
			q.field("time").greaterThanOrEq(startTime);
			q.field("time").lessThan(endTime);
			q.field("type").equal(2);
			counts=q.asList();
			 uCount=new UserStatusCount();
			for (UserStatusCount userStatus : counts) {
				sum+=userStatus.getCount();
			}
			System.out.println("Day_Count List Size======="+counts.size());
			if(sum>0){
				uCount.setTime(startTime);
				uCount.setType(3);
				uCount.setCount(sum/counts.size());
				userManager.saveEntity(uCount);
				System.out.println("Day_Count 平均用户在线======》" +uCount.getCount());
			}
	
		
		
	}
	@Scheduled(cron = "0 0 4 * * ?")
	public void refreshUserStatus(){
		BasicDBObject q = new BasicDBObject("_id",new BasicDBObject(MongoOperator.GT,1000));
		q.append("onlinestate", 1);
		DBObject values = new BasicDBObject();
		values.put(MongoOperator.SET,new BasicDBObject("onlinestate",0));
		dsForRW.getCollection(User.class).update(q, values, false, true);
	}
	
	//红包超时未领取 退回余额
	private void autoRefreshRedPackect(){
		//q.put("status", new BasicDBObject(MongoOperator.NE,STATUS_RECEDE).append(MongoOperator.NE,STATUS_END));
		long currentTime=DateUtil.currentTimeSeconds();
		DBObject obj=null;
		Integer userId=0;
		Double money=0.0;
		DBObject values = new BasicDBObject();
		List<DBObject> objs=new ArrayList<DBObject>();
		DBObject q = new BasicDBObject("outTime",new BasicDBObject(MongoOperator.LT,currentTime));
		q.put("over",new BasicDBObject(MongoOperator.GT,0));
		q.put("status",STATUS_START);//只查询发出状态的红包
		DBCursor cursor =dsForRW.getCollection(RedPacket.class).find(q);
		
			while (cursor.hasNext()) {
				 obj = (BasicDBObject) cursor.next();
				objs.add(obj);
			}
		if(0<objs.size()){
			values.put(MongoOperator.SET,new BasicDBObject("status", STATUS_RECEDE));
			dsForRW.getCollection(RedPacket.class).update(q, values,false,true);
		}
		for (DBObject dbObject : objs) {
			 userId= (Integer) dbObject.get("userId");
			 money =(Double) dbObject.get("over");
			 recedeMoney(userId,money);
		}
			
		System.out.println("红包超时未领取的数量 ======> "+objs.size());
		
	}
	
	private void recedeMoney(Integer userId,Double money){
		//实例化一天交易记录
		ConsumeRecord record=new ConsumeRecord();
		String tradeNo=AliPayUtil.getOutTradeNo();
		record.setTradeNo(tradeNo);
		record.setMoney(money);
		record.setUserId(userId);
		record.setType(KConstants.MOENY_ADD);
		record.setPayType(3);
		record.setTime(DateUtil.currentTimeSeconds());
		record.setStatus(1);
		record.setDesc("红包退款");
		recordManager.saveConsumeRecord(record);
		userManager.rechargeUserMoeny(userId, money, KConstants.MOENY_ADD);
		
		System.out.println(userId+"  发出的红包,剩余金额   "+money+"  未领取  退回余额!");
	}


	
}
