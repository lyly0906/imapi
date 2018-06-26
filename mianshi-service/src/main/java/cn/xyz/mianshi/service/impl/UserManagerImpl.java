package cn.xyz.mianshi.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.constants.KKeyConstant;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.jedis.JedisCallback;
import cn.xyz.commons.support.jedis.JedisCallbackVoid;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.Md5Util;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.example.UserQueryExample;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.Report;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.User.UserSettings;
import cn.xyz.repository.UserRepository;
import cn.xyz.service.KSMSServiceImpl;
import cn.xyz.service.KXMPPServiceImpl;
import redis.clients.jedis.Jedis;

@Service(UserManagerImpl.BEAN_ID)
public class UserManagerImpl extends MongoRepository<User, Integer> implements UserManager {
	public static final String BEAN_ID = "UserManagerImpl";

	@Autowired
	private CompanyManager companyManager;
	@Autowired
	private FriendsManager friendsManager;
	

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private KSMSServiceImpl smsService;
	
	@Resource(name = "dsForTigase")
	private Datastore dsForTigase;
	@Resource(name = "xmppConfig")
	private XMPPConfig xmppConfig;

	@Override
	public User createUser(String telephone, String password) {
		User user = new User();
		user.setUserId(createUserId());
		user.setUserKey(DigestUtils.md5Hex(telephone));
		user.setPassword(DigestUtils.md5Hex(password));
		user.setTelephone(telephone);

		userRepository.addUser(user);

		return user;
	}

	@Override
	public void createUser(User user) {
		userRepository.addUser(user);

	}

	@Override
	public User.UserSettings getSettings(int userId) {
		UserSettings settings=null;
		User user=null;
		user=getUser(userId);
		if(null==user)
			return null;
		settings=user.getSettings();
		return null!=settings?settings:new UserSettings();
	}

	@Override
	public User getUser(int userId) {
		//先从 Redis 缓存中获取
		User user =KSessionUtil.getUserByUserId(userId);
		if(null==user){
				user = userRepository.getUser(userId);
			if (null == user){
				System.out.println("id为"+userId+"的用户不存在" );
				return null;
			}
			
			KSessionUtil.saveUserByUserId(userId, user);
		}
		
		return user;
	}

	@Override
	public User getUser(int userId, int toUserId) {
		User user = getUser(toUserId);

		Friends friends = friendsManager.getFriends(new Friends(userId, toUserId));
		user.setFriends(null == friends ? null : friends);

		// if (userId == toUserId) {
		// List<ResumeV2> resumeList = resumeManager.selectByUserId(toUserId);
		// user.setResumeList(resumeList);
		// }

		return user;
	}

	@Override
	public User getUser(String telephone) {
		//Integer userId=KSessionUtil.getUserIdByTelephone(telephone);
		
		return userRepository.getUser(telephone);
	}

	@Override
	public User getUserByPhone(String phone) {

		return userRepository.getUserByPhone(phone);
	}

	@Override
	public int getUserId(String accessToken) {
		return 0;
	}

	@Override
	public boolean isRegister(String telephone) {
		return 1 == userRepository.getCount(telephone);
	}

	@Override
	public User login(String telephone, String password) {
		String userKey = DigestUtils.md5Hex(telephone);

		User user = userRepository.getUserv1(userKey, null);

		if (null == user) {
			throw new ServiceException("帐号不存在");
		} else {
			String _md5 = DigestUtils.md5Hex(password);
			String _md5_md5 = DigestUtils.md5Hex(_md5);

			if (_md5.equals(user.getPassword()) || _md5_md5.equals(user.getPassword())) {
				return user;
			} else {
				throw new ServiceException("帐号或密码错误");
			}
		}
	}

	@Override
	public Map<String, Object> login(UserExample example) {
		User user = userRepository.getUser(example.getAreaCode(),example.getTelephone(), null);
		if (null == user) {
			throw new ServiceException(1040101, "帐号不存在, 请重新注册!");
		} else {
			
			
			String password = example.getPassword();
			String _md5 = DigestUtils.md5Hex(password);
			String _md5_md5 = DigestUtils.md5Hex(_md5);
			if (password.equals(user.getPassword()) || _md5.equals(user.getPassword())
					|| _md5_md5.equals(user.getPassword())) {
				return loginSuccess(user, example);
			}
			throw new ServiceException(1040102, "帐号密码错误");
		}
	}
	@Override
	public Map<String, Object> loginv1(UserExample example) {
		User user = userRepository.getUserv1(example.getTelephone(), null);
		if (null == user) {
			throw new ServiceException(1040101, "帐号不存在, 请重新注册!");
		}else if(!StringUtil.isEmpty(example.getRandcode())){
			//使用验证码登陆
			if(smsService.isAvailable(user.getTelephone(), example.getRandcode()))
				return loginSuccess(user, example);
			else throw new ServiceException(0, "验证码不正确");
			
		} else {
			String password = example.getPassword();
			String _md5 = DigestUtils.md5Hex(password);
			String _md5_md5 = DigestUtils.md5Hex(_md5);
			if (password.equals(user.getPassword()) || _md5.equals(user.getPassword())
					|| _md5_md5.equals(user.getPassword())) {
				return loginSuccess(user, example);
			}
			throw new ServiceException(1040102, "帐号密码错误");
		}
	}
	
		//登陆成功方法
		public  Map<String, Object> loginSuccess(User user,UserExample example){
			// 获取上次登录日志
			Object login = userRepository.getLogin(user.getUserId());
			// 保存登录日志
			userRepository.updateLogin(user.getUserId(), example);
			// f1981e4bd8a0d6d8462016d2fc6276b3
			Map<String, Object> data = userRepository.getAT(user.getUserId(), example.getTelephone());
			data.put("userId", user.getUserId());
			//data.put("password", user.getPassword());
			data.put("active", user.getActive());
			data.put("nickname", user.getNickname());
			data.put("companyId", user.getCompanyId());
			data.put("offlineNoPushMsg", user.getOfflineNoPushMsg());
			data.put("login", login);
			
			Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(user.getUserId());
			
			//好友关系数量
			data.put("friendCount", q.countAll());
			// 保存登录日志
			
			///检查该用户  是否注册到 Tigase
				examineTigaseUser(user.getUserId(), user.getPassword());
			return data;
		}

	@Override
	public Map<String, Object> loginAuto(String access_token, int userId, String serial,String appId) {
		
		User user = getUser(userId);
		 if (null == user) 
				throw new ServiceException(1040101, "帐号不存在, 请重新注册!");
		 
				 User.LoginLog loginLog = userRepository.getLogin(userId);
		        System.out.println(loginLog);
				boolean exists = jedisTemplate.execute(new JedisCallback<Boolean>() {
	
					@Override
					public Boolean execute(Jedis jedis) {
						String atKey = KKeyConstant.userIdKey(access_token);
						return jedis.exists(atKey);
					}
	
				});
			// 1=没有设备号、2=设备号一致、3=设备号不一致
			int serialStatus = null == loginLog ? 1 : (serial.equals(loginLog.getSerial()) ? 2 : 3);
			System.out.println(serialStatus);
			// 1=令牌存在、0=令牌不存在
			int tokenExists=exists ? 1 : 0;	
			
			try {

			Map<String, Object> result = Maps.newHashMap();
			result.put("serialStatus", serialStatus);
			result.put("tokenExists", tokenExists);
			result.put("userId", userId);
			result.put("nickname", user.getNickname());
			result.put("name", user.getName());
			result.put("login", loginLog);

			System.out.println(result);
			//更新appId
			updateAttributeByIdAndKey(userId, "appId", appId);
			return result;
		} catch (NullPointerException e) {
			throw new ServiceException("帐号不存在");
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void logout(String access_token,String areaCode,String userKey) {
		if(!StringUtil.isEmpty(userKey)){
			Query<User> q = dsForRW.createQuery(User.class)
					.filter("areaCode", areaCode)
					.field("userKey")
					.equal(userKey);
			UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
			ops.set("active",DateUtil.currentTimeSeconds());
			ops.set("loginLog.offlineTime",DateUtil.currentTimeSeconds());
			dsForRW.findAndModify(q, ops);	
		}
		
		jedisTemplate.execute(new JedisCallbackVoid() {		
		 @Override
		 public void execute(Jedis jedis) {
		 String atKey = KKeyConstant.atKey(userKey);
		 String userIdKey = KKeyConstant.userIdKey(access_token);
		 jedis.del(atKey, userIdKey);
		 }
		 });
	}

	@Override
	public List<DBObject> query(UserQueryExample param) {
		return userRepository.queryUser(param);
	}

	@Override
	public Map<String, Object> register(UserExample example) {
		if (isRegister(example.getTelephone()))
			throw new ServiceException("手机号已被注册");

		// 生成userId
		Integer userId = createUserId();
		// 新增用户
		Map<String, Object> data = userRepository.addUser(userId, example);

		if (null != data) {
			try {
				KXMPPServiceImpl.getInstance().registerByThread(userId.toString(), example.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return data;
		}
		throw new ServiceException("用户注册失败");
	}

	@Override
	public Map<String, Object> registerIMUser(UserExample example) {
		if (isRegister(example.getTelephone()))
			throw new ServiceException("手机号已被注册");

		// 生成userId
		Integer userId =createUserId();
		// 新增用户
		Map<String, Object> data = userRepository.addUser(userId, example);
		if (null != data) {
			try {
				KXMPPServiceImpl.getInstance().registerByThread(userId.toString(), example.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				friendsManager.followUser(userId, 10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//调用组织架构功能示例方法
			companyManager.autoJoinCompany(userId);
			
			return data;
		}
		throw new ServiceException("用户注册失败");
	}

	@Override
	public void resetPassword(String telephone, String password) {
		userRepository.updatePassword(telephone, password);
		KXMPPServiceImpl.getInstance().updateToTig(getUser(telephone).getUserId(), password);
	}

	@Override
	public void updatePassword(int userId, String oldPassword, String newPassword, int isadmin) {
		User user = getUser(userId);
		String _md5 = DigestUtils.md5Hex(oldPassword);
		String _md5_md5 = DigestUtils.md5Hex(_md5);
		if(isadmin == 1){
			userRepository.updatePassowrd(userId, newPassword);
			KXMPPServiceImpl.getInstance().updateToTig(userId, newPassword);
		}else{
			if (oldPassword.equals(user.getPassword()) || _md5.equals(user.getPassword())
					|| _md5_md5.equals(user.getPassword())) {
				userRepository.updatePassowrd(userId, newPassword);
				KXMPPServiceImpl.getInstance().updateToTig(userId, newPassword);
			} else
				throw new ServiceException("旧密码错误");
		}
	}

	@Override
	public User updateSettings(int userId,User.UserSettings userSettings) {		
		return userRepository.updateSettings(userId, userSettings);
	}

	@Override
	public User updateUser(int userId, UserExample param) {
		return userRepository.updateUser(userId, param);
	}
	public List<User> findUserList(int pageIndex, int pageSize,Integer notId) {
		Query<User> query=createQuery();
		List<Integer> ids=new ArrayList<Integer>(){{add(10000);add(10005);add(10006);add(notId);}};
		query.field("_id").notIn(ids);
		return query.order("-_id").offset(pageIndex * pageSize).limit(pageSize).asList();
	}
	
	@Override
	public List<DBObject> findUser(int pageIndex, int pageSize) {
		return userRepository.findUser(pageIndex, pageSize);
	}

	@Override
	public List<Integer> getAllUserId() {
		return dsForRW.getCollection((User.class)).distinct("_id");
	}

	@Override
	public void outtime(String access_token, int userId) {
		Query<User> q = dsForRW.createQuery(User.class).field("_id")
				.equal(userId);
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
		ops.set("active",DateUtil.currentTimeSeconds());
		ops.set("loginLog.offlineTime",DateUtil.currentTimeSeconds());
		dsForRW.findAndModify(q, ops);
	}

	@Override
	public void addUser(int userId, String password) {
		userRepository.addUser(userId, password);
	}

	/*@Override
	public User getfUser(int userId) {
		User user = userRepository.getUser(userId);
		if (null == user)
			return null;
		if (0 != user.getCompanyId())
			user.setCompany(companyManager.get(user.getCompanyId()));
			return user;		
	}*/
	
	//用户充值 type 1 充值  2 消费
	public Double rechargeUserMoeny(Integer userId,Double money,int type){
			try {
				Query<User> q =dsForRW.createQuery(User.class);
				q.field("_id").equal(userId);
				
				UpdateOperations<User> ops =dsForRW.createUpdateOperations(User.class);
				User user=KSessionUtil.getUserByUserId(userId);
				if(null==user)
					return 0.0;
				if(KConstants.MOENY_ADD==type){
					ops.inc("balance", money);
					ops.inc("totalRecharge",money);
					user.setBalance(user.getBalance()+money);
				}else{ 
					ops.inc("balance", -money);
					ops.inc("totalConsume",money);
					user.setBalance(user.getBalance()-money);
				}
				dsForRW.update(q, ops);
				KSessionUtil.saveUserByUserId(userId, user);
			return q.get().getBalance();
			} catch (Exception e) {
				e.printStackTrace();
				return 0.0;
			}
	}
		//用户充值 type 1 充值  2 消费
		public Double getUserMoeny(Integer userId){
				try {
					Query<User> q =dsForRW.createQuery(User.class);
					q.field("_id").equal(userId);
					
				return q.get().getBalance();
				} catch (Exception e) {
					e.printStackTrace();
					return 0.0;
				}
		}
		public int getOnlinestateByUserId(Integer userId) {
			DBObject q=new BasicDBObject("_id",userId);
			List<Object> states= distinct("onlinestate", q);
			if(states!=null&&states.size()>0)
				return (int)states.get(0);
			return 0;
		}
		
		
		public void examineTigaseUser(Integer userId,String password){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						DBObject q=new BasicDBObject("user_id",userId+"@"+xmppConfig.getHost());
						DBObject obj=dsForTigase.getDB().getCollection("tig_users").findOne(q);
						if(null==obj){
							KXMPPServiceImpl.getInstance().registerByThread(String.valueOf(userId), password);
						}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}).start();
			
			
		}
		
		public void report(Integer userId,Integer toUserId,String reason){
			Report report=new Report();
			report.setUserId(userId);
			report.setToUserId(toUserId);
			report.setReason(reason);
			report.setTime(DateUtil.currentTimeSeconds());
			saveEntity(report);
			
		}

		/* (non-Javadoc)
		 * @see cn.xyz.mianshi.service.UserManager#getNickName(int)
		 */
		@Override
		public String getNickName(int userId) {
			User user=KSessionUtil.getUserByUserId(userId);
			if(user!=null)
				return user.getNickname();
			List names=distinct("nickname", new BasicDBObject("_id",userId));
			 if(null==names||0==names.size())
				 return "";
			 return names.get(0).toString();
		}
		
		
		
		//获取用户Id
		public synchronized Integer createUserId(){
			DBCollection collection=dsForRW.getDB().getCollection("idx_user");
			if(null==collection)
				return createIdxUserCollection(collection,0);
			DBObject obj=collection.findOne();
			if(null!=obj){
				Integer id=new Integer(obj.get("id").toString());
				id+=1;
				collection.update(new BasicDBObject("_id", obj.get("_id")),
						new BasicDBObject(MongoOperator.INC, new BasicDBObject("id", 1)));
				return id; 
			}else{
				return createIdxUserCollection(collection,0);
			}
			
		}
		
		private Integer createIdxUserCollection(DBCollection collection,long userId){
			if(null==collection)
				collection=dsForRW.getDB().createCollection("idx_user", new BasicDBObject());
			BasicDBObject init=new BasicDBObject();
			Integer id=getMaxUserId();
			if(0==id||id<1000000)
				id=new Integer("10000001");
			id+=1;
			 init.append("id", id);
			 init.append("stub","id");
			collection.insert(init);
			return id;
		}
		private Integer getMaxUserId(){
			BasicDBObject projection=new BasicDBObject("_id", 1);
			DBObject dbobj=dsForRW.getDB().getCollection("user").findOne(null, projection, new BasicDBObject("_id", -1));
			if(null==dbobj)
				return 0;
			Integer id=new Integer(dbobj.get("_id").toString());
				return id;
		}
		
		public Integer getServiceNo(String areaCode){
			DBCollection collection=getDatastore().getDB().getCollection("sysServiceNo");
			BasicDBObject obj=(BasicDBObject) collection.findOne(new BasicDBObject("areaCode", areaCode));
			if(null!=obj)
				return obj.getInt("userId");
			return createServiceNo(areaCode);
		}
		
		//获取系统最大客服号
		private Integer getMaxServiceNo(){
			DBCollection collection=getDatastore().getDB().getCollection("sysServiceNo");
			BasicDBObject obj=(BasicDBObject) collection.findOne(null, new BasicDBObject("userId", 1), new BasicDBObject("userId", -1));
			if(null!=obj){
				return obj.getInt("userId");
			}else{
				BasicDBObject query=new BasicDBObject("_id",new BasicDBObject(MongoOperator.LT, 1000200));
				query.append("_id",new BasicDBObject(MongoOperator.GT, 10200));
				BasicDBObject projection=new BasicDBObject("_id", 1);
				DBObject dbobj=dsForRW.getDB().getCollection("user").findOne(query, projection, new BasicDBObject("_id", -1));
				if(null==dbobj)
					return 100200;
				Integer id=new Integer(dbobj.get("_id").toString());
					return id;
			}
		}
		
		//创建系统服务号
		private Integer createServiceNo(String areaCode){
			DBCollection collection=getDatastore().getDB().getCollection("sysServiceNo");
			Integer userId=getMaxServiceNo()+1;
			BasicDBObject value=new BasicDBObject("areaCode", areaCode);
			value.append("userId", userId);
			collection.save(value);
			addUser(userId, Md5Util.md5Hex(userId+""));
			
			return userId;
		}
		//消息免打扰
		@Override
		public User updataOfflineNoPushMsg(int userId,int OfflineNoPushMsg) {
			Query<User> q=dsForRW.createQuery(User.class).field("_id").equal(userId);
			UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
			ops.set("OfflineNoPushMsg", OfflineNoPushMsg);
			
			return dsForRW.findAndModify(q, ops);
		}
}
