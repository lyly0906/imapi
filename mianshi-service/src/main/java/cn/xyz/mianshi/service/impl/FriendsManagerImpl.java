package cn.xyz.mianshi.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.MapUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.CompanyManager;
import cn.xyz.mianshi.service.FriendsManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.CompanyVO;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.NewFriends;
import cn.xyz.mianshi.vo.PageVO;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.FriendsRepository;
import cn.xyz.repository.UserRepository;

@Service
public class FriendsManagerImpl extends MongoRepository<Friends, ObjectId> implements FriendsManager  {

	private static final String groupCode = "110";

	private static Logger Log = LoggerFactory.getLogger(FriendsManager.class);

	@Autowired
	private CompanyManager companyService;
	@Resource(name = "dsForTigase")
	private Datastore dsForTig;
	@Resource(name = "dsForRW")
	protected Datastore dsForRW;
	@Autowired
	private FriendsRepository friendsRepository;
	@Autowired
	private UserManager userService;
	@Autowired
	private UserRepository userRepository;

	@Override
	public Friends addBlacklist(Integer userId, Integer toUserId) {
		// 是否存在AB关系
		Friends friendsAB = friendsRepository.getFriends(userId, toUserId);

		if (null == friendsAB) {
			Friends friends = new Friends(userId, toUserId,userService.getNickName(toUserId), Friends.Status.Stranger, Friends.Blacklist.Yes);
			friendsRepository.saveFriends(friends);
		} else {
			// 更新关系
			friendsRepository.updateFriends(new Friends(userId, toUserId,null, 0, Friends.Blacklist.Yes));
			//修改另一张
			Query<Friends> q=dsForRW.createQuery(Friends.class).field("userId").equal(toUserId).field("toUserId").equal(userId);
			UpdateOperations<Friends> ops = dsForRW.createUpdateOperations(Friends.class);
			ops.set("status", 0);
			update(q, ops);
		}

		return friendsRepository.getFriends(userId, toUserId);
	}

	private void saveFansCount(int userId) {
		BasicDBObject q = new BasicDBObject("_id", userId);
		DBCollection dbCollection = dsForTig.getDB().getCollection("shiku_msgs_count");
		if (0 == dbCollection.count(q)) {
			BasicDBObject jo = new BasicDBObject("_id", userId);
			jo.put("count", 0);// 消息数
			jo.put("fansCount", 1);// 粉丝数
			dbCollection.insert(jo);
		} else {
			dbCollection.update(q, new BasicDBObject("$inc", new BasicDBObject("fansCount", 1)));
		}
	}

	@Override
	public boolean addFriends(Integer userId, Integer toUserId) {
		if (deleteFriends(userId, toUserId)) {
			friendsRepository.saveFans(new Fans(userId, toUserId));
			friendsRepository.saveFans(new Fans(toUserId, userId));
			saveFansCount(toUserId);
			friendsRepository.saveFriends(new Friends(userId, toUserId,userService.getNickName(toUserId), Friends.Status.Friends));
			friendsRepository.saveFriends(new Friends(toUserId, userId,userService.getNickName(userId),Friends.Status.Friends));

			return true;
		} else
			throw new ServiceException("加好友失败");
	}

	@Override
	public Friends deleteBlacklist(Integer userId, Integer toUserId) {
		// 是否存在AB关系
		Friends friendsAB = friendsRepository.getFriends(userId, toUserId);

		if (null == friendsAB) {
			// 无记录
		} else {
			// 陌生人黑名单
			if (Friends.Blacklist.Yes == friendsAB.getBlacklist() && Friends.Status.Stranger == friendsAB.getStatus()) {
				// 物理删除
				friendsRepository.deleteFriends(userId, toUserId);
			} else {
				// 恢复关系
				friendsRepository.updateFriends(new Friends(userId, toUserId,null, 0, Friends.Blacklist.No));
			}
			// 是否存在AB关系
			friendsAB = friendsRepository.getFriends(userId, toUserId);
		}

		return friendsAB;
	}

	@Override
	public boolean deleteFriends(Integer userId, Integer toUserId) {
		User user = userService.getUser(userId);
		User toUser = userService.getUser(toUserId);
		friendsRepository.deleteFans(userId, toUserId);
		friendsRepository.deleteFriends(userId, toUserId);
		friendsRepository.deleteFans(toUserId, userId);
		friendsRepository.deleteFriends(toUserId, userId);
		friendsRepository.saveNewFriends(new NewFriends(toUserId,userId,user.getNickname(),2,505,1,user.getNickname()+"已将"+toUser.getNickname()+"从好友记录删除"));
		return true;
	}

	

	@Override
	public JSONMessage followUser(Integer userId, Integer toUserId) {
		final String serviceCode = "08";
		JSONMessage jMessage;
		User toUser = userService.getUser(toUserId);
		//好友不存在
		if(null==toUser){
			if(10000==toUserId)
				return null;
			else
				return JSONMessage.failure("关注失败, 用户不存在!");
				
		}
			
		try {
			User user = userService.getUser(userId);
			
			// 是否存在AB关系
			Friends friendsAB = friendsRepository.getFriends(userId, toUserId);
			// 是否存在BA关系
			Friends friendsBA = friendsRepository.getFriends(toUserId, userId);
			// 获取目标用户设置
			User.UserSettings userSettingsB = userService.getSettings(toUserId);

			// ----------------------------
			// 0 0 0 0 无记录 执行关注逻辑
			// A B 1 0 非正常 执行关注逻辑
			// A B 1 1 拉黑陌生人 执行关注逻辑
			// A B 2 0 关注 重复关注
			// A B 3 0 好友 重复关注
			// A B 2 1 拉黑关注 恢复关系
			// A B 3 1 拉黑好友 恢复关系
			// ----------------------------
			// 无AB关系或陌生人黑名单关系，加关注
			if(null != friendsAB&&friendsAB.getIsBeenBlack()==1){
				return jMessage = JSONMessage.failure("加好友失败");
			}
			if (null == friendsAB || Friends.Status.Stranger == friendsAB.getStatus()) {
				// 目标用户拒绝关注
				if (0 == userSettingsB.getAllowAtt()) {
					jMessage = new JSONMessage(groupCode, serviceCode, "01", "关注失败，目标用户拒绝关注");
				}
				// 目标用户允许关注
				else {
					int statusA = 0;

					// 目标用户加好需验证，执行加关注
					if (1 == userSettingsB.getFriendsVerify()) {
						// ----------------------------
						// 0 0 0 0 无记录 执行单向关注
						// B A 1 0 非正常 执行单向关注
						// B A 1 1 拉黑陌生人 执行单向关注
						// B A 2 0 关注 加好友
						// B A 3 0 好友 加好友
						// B A 2 1 拉黑关注 加好友
						// B A 3 1 拉黑好友 加好友
						// ----------------------------
						// 无BA关系或陌生人黑名单关系，单向关注
						if (null == friendsBA || Friends.Status.Stranger == friendsBA.getStatus()) {
							statusA = Friends.Status.Attention;
						} else {
							statusA = Friends.Status.Friends;

							friendsRepository
									.updateFriends(new Friends(toUserId, user.getUserId(),user.getNickname(), Friends.Status.Friends));
						}
					}
					// 目标用户加好友无需验证，执行加好友
					else {
						statusA = Friends.Status.Friends;

						if (null == friendsBA) {
							friendsRepository.saveFans(new Fans(userId, toUserId));
							friendsRepository.saveFriends(new Friends(toUserId, user.getUserId(),user.getNickname(),
									Friends.Status.Friends, Friends.Blacklist.No));

							saveFansCount(toUserId);
						} else
							friendsRepository
									.updateFriends(new Friends(toUserId, user.getUserId(),user.getNickname(), Friends.Status.Friends));
					}

					if (null == friendsAB) {
						friendsRepository.saveFans(new Fans(toUserId, userId));
						friendsRepository.saveFriends(new Friends(userId, toUserId,toUser.getNickname(), statusA, Friends.Blacklist.No));
						friendsRepository.updateNewFriends(new NewFriends(userId,toUserId,user.getNickname(),statusA,508,1,user.getNickname()+"加"+toUser.getNickname()+"为好友"));
						saveFansCount(toUserId);
					} else {
						friendsRepository.updateFriends(new Friends(userId, toUserId,toUser.getNickname(), statusA, Friends.Blacklist.No));
					}

					if (statusA == Friends.Status.Attention) {
						jMessage = JSONMessage.success("关注成功，已关注目标用户", MapUtil.newMap("type", 1));
						friendsRepository.saveNewFriends(new NewFriends(toUserId,userId,user.getNickname(),statusA,502,1,user.getNickname()+"加"+toUser.getNickname()+"的关注"));
					} else {
						jMessage = JSONMessage.success("关注成功，已互为好友", MapUtil.newMap("type", 2));
						friendsRepository.saveNewFriends(new NewFriends(toUserId,userId,user.getNickname(),statusA,508,1,user.getNickname()+"加"+toUser.getNickname()+"为好友"));
					}

				}
			}
			// 有关注或好友关系，重复关注
			else if (Friends.Blacklist.No == friendsAB.getBlacklist()) {
				if (Friends.Status.Attention == friendsAB.getStatus())
					jMessage = JSONMessage.success("关注失败， 重复关注", MapUtil.newMap("type", 3));
				else
					jMessage = JSONMessage.success("关注失败， 已互为好友", MapUtil.newMap("type", 4));
			}
			// 有关注黑名单或好友黑名单关系，恢复关系
			else {
				friendsRepository.updateFriends(new Friends(userId, toUserId,toUser.getNickname(), Friends.Blacklist.No));

				jMessage = null;
			}
		} catch (Exception e) {
			Log.error("关注失败", e);

			jMessage = JSONMessage.failure("关注失败");
		}

		return jMessage;
	}
	public Friends getFriends(int userId, int toUserId) {
		return friendsRepository.getFriends(userId, toUserId);
	}

	@Override
	public List<Fans> getFansList(Integer userId) {
		
		List<Fans> result = friendsRepository.getFansList(userId);
		Iterator<Fans> iter = result.iterator(); 
		User user=null;
		Fans friends=null;
		while (iter.hasNext()) { 
			 friends=iter.next();
			 user = userService.getUser(friends.getToUserId());
			if(null==user){
				deleteFansAndFriends(friends.getToUserId());
				iter.remove();
				continue;
			}
			friends.setToNickname(user.getNickname());
		}
//		
//		for (Fans friends : result) {
//			try {
//				
//				User user = userService.getUser(friends.getToUserId());
//				friends.setToNickname(user.getNickname());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

		return result;
	}

	@Override
	public PageVO getFansPage(int userId, int pageIndex, int pageSize) {
		Query<Fans> q = dsForRW.createQuery(Fans.class).field("userId").equal(userId);
		long total = q.countAll();
		List<Fans> pageData = q.offset(pageIndex * pageSize).limit(pageSize).asList();
		User user=null;
		for (Fans friends : pageData) {
			try {
				user=userService.getUser(friends.getToUserId());
				if(null==user){
					deleteFansAndFriends(friends.getToUserId());
				}
				friends.setToNickname(userService.getNickName(friends.getToUserId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new PageVO(pageData, total, pageIndex, pageSize);
	}

	@Override
	public Friends getFriends(Friends p) {
		return friendsRepository.getFriends(p.getUserId(), p.getToUserId());
	}

	@Override
	public List<Integer> getFriendsIdList(int userId) {
		List<Integer> result = Lists.newArrayList();

		try {
			List<Friends> friendsList = friendsRepository.queryFriends(userId);
			friendsList.forEach(friends -> {
				result.add(friends.getToUserId());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<Friends> queryBlacklist(Integer userId) {
		return friendsRepository.queryBlacklist(userId);
	}

	@Override
	public List<Integer> queryFansId(Integer userId) {
		return friendsRepository.queryFansId(userId);
	}

	@Override
	public List<Friends> queryFollow(Integer userId,int status) {
		List<Friends> result = friendsRepository.queryFollow(userId,status);
		Iterator<Friends> iter = result.iterator(); 
		while (iter.hasNext()) { 
			Friends friends=iter.next();
			User user = userService.getUser(friends.getToUserId());
			if(null==user){
				iter.remove();
				deleteFansAndFriends(friends.getToUserId());
				continue;
			}
			friends.setCompanyId(user.getCompanyId());
			friends.setToNickname(user.getNickname());
		}
//		for (Friends friends : result) {
//			User user = userService.getfUser(friends.getToUserId());
////			if(null==user){
////				continue;
////			}
//			friends.setCompanyId(user.getCompanyId());
//			friends.setToNickname(user.getNickname());
//		}
		
		return result;

	}

	@Override
	public List<Integer> queryFollowId(Integer userId) {
		return friendsRepository.queryFollowId(userId);
	}

	@Override
	public List<Friends> queryFriends(Integer userId) {
		List<Friends> result = friendsRepository.queryFriends(userId);

		for (Friends friends : result) {
			User toUser = userService.getUser(friends.getToUserId());
			if(null==toUser){
				deleteFansAndFriends(friends.getToUserId());
				continue;
			}
			friends.setToNickname(toUser.getNickname());
			friends.setCompanyId(toUser.getCompanyId());
		}

		return result;
	}
	
	
	@Override   //返回好友的userId 和单向关注的userId
	public List<Integer> friendsAndAttentionUserId(Integer userId,String type) {
		List<Friends> result = new ArrayList<Friends>();
		if("friendList".equals(type) || "blackList".equals(type)){  //返回好友的userId 和单向关注的userId
			 result = friendsRepository.friendsOrBlackList(userId, type);
		}else{
			throw new ServiceException("无法识别的参数");
		}
		List<Integer> userIds = new ArrayList<Integer>();
		for (Friends friend : result) {
			userIds.add(friend.getToUserId());
		}
		return userIds;
	}

	@Override
	public PageVO queryFriends(Integer userId,int status,String keyword, int pageIndex, int pageSize) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId);
			if(0<status)
				q.filter("status", status);
			if(!StringUtil.isEmpty(keyword)){
				//q.field("toNickname").containsIgnoreCase(keyword);
				q.or(q.criteria("toNickname").containsIgnoreCase(keyword),
					  q.criteria("remarkName").containsIgnoreCase(keyword));
			}
		long total = q.countAll();
		List<Friends> pageData = q.offset(pageIndex * pageSize).limit(pageSize).asList();
		for (Friends friends : pageData) {
			User toUser = userService.getUser(friends.getToUserId());
			if(null==toUser){
				deleteFansAndFriends(friends.getToUserId());
				continue;
			}
			friends.setToNickname(toUser.getNickname());
			friends.setCompanyId(toUser.getCompanyId());
		}
		return new PageVO(pageData, total, pageIndex, pageSize);
	}
	public List<Friends> queryFriendsList(Integer userId,int status, int pageIndex, int pageSize) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId);
			if(0<status)
				q.filter("status", status);
			/*if(!StringUtil.isEmpty(keyword)){
				q.or(q.criteria("nickname").containsIgnoreCase(keyword),
						q.criteria("telephone").contains(keyword));
			}*/
		List<Friends> pageData = q.offset(pageIndex * pageSize).limit(pageSize).asList();
		for (Friends friends : pageData) {
			User toUser = userService.getUser(friends.getToUserId());
			if(null==toUser){
				deleteFansAndFriends(friends.getToUserId());
				continue;
			}
			friends.setToNickname(toUser.getNickname());
			friends.setCompanyId(toUser.getCompanyId());
		}
		return pageData;
	}

	

	@Override
	public boolean unfollowUser(Integer userId, Integer toUserId) {
		// 删除用户关注
		User user = userService.getUser(userId);
		User toUser = userService.getUser(toUserId);
		friendsRepository.deleteFriends(userId, toUserId);
		// 删除目标用户粉丝
		friendsRepository.deleteFans(toUserId, userId);
		friendsRepository.saveNewFriends(new NewFriends(toUserId,userId,user.getNickname(),2,504,1,user.getNickname()+"取消对"+toUser.getNickname()+"的关注"));
		return true;
	}

	@Override
	public Friends updateRemark(int userId, int toUserId, String remarkName) {
		Friends friends = new Friends(userId, toUserId);
		friends.setRemarkName(remarkName);
		return friendsRepository.updateFriends(friends);
	}

	@Override
	public void deleteFans(int userId, int toUserId) {
		friendsRepository.deleteFans(userId, toUserId);
	}

	@Override
	public void deleteFansAndFriends(int userId) {
		friendsRepository.deleteFans(userId);
		friendsRepository.deleteFriends(userId);
	}

	/* (non-Javadoc)
	 * @see cn.xyz.mianshi.service.FriendsManager#newFriendList(int,int,int)
	 */
	@Override
	public List<NewFriends> newFriendList(int userId,int pageIndex,int pageSize) {
		
		Query<NewFriends> query=dsForRW.createQuery(NewFriends.class);
		query.filter("userId", userId);
		query.or(query.criteria("userId").equal(userId),
				query.criteria("toUserId").equal(userId));
		
		List<NewFriends> pageData=query.order("-modifyTime").offset(pageIndex * pageSize).limit(pageSize).asList();
		System.out.println(pageData);

		Friends friends=null;
		for (NewFriends newFriends : pageData) {
			friends=getFriends(newFriends.getUserId(), newFriends.getToUserId());
			newFriends.setToNickname(userService.getNickName(newFriends.getToUserId()));
			
			/*if(userId==newFriends.getToUserId()){
				friends=getFriends(newFriends.getToUserId(), newFriends.getUserId());
				newFriends.setToNickname(userService.getNickName(newFriends.getUserId()));
			}
			else{
				friends=getFriends(newFriends.getUserId(), newFriends.getToUserId());
				newFriends.setToNickname(userService.getNickName(newFriends.getToUserId()));
			}*/
			
			if(null!=friends)
				newFriends.setStatus(friends.getStatus());
		}
		//long total=query.countAll();
		//return new PageVO(pageData, total, pageIndex, pageSize);
		
		return pageData;
		
	}
	//消息免打扰
	@Override
	public Friends updateOfflineNoPushMsg(int userId, int toUserId, int offlineNoPushMsg) {
		Query<Friends> q = dsForRW.createQuery(Friends.class).field("userId").equal(userId).field("toUserId").equal(toUserId);
		UpdateOperations<Friends> ops = dsForRW.createUpdateOperations(Friends.class);
		ops.set("offlineNoPushMsg", offlineNoPushMsg);
		
		return dsForRW.findAndModify(q, ops);
	}

	@Override
	public void delNewFriend(int userId) {
		friendsRepository.deleteNewFriends(userId);
	}

}
