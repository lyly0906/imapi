package cn.xyz.mianshi.service;

import java.util.List;

import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.NewFriends;
import cn.xyz.mianshi.vo.PageVO;

public interface FriendsManager {

	Friends addBlacklist(Integer userId, Integer toUserId);

	void deleteFans(int userId, int toUserId);
	void deleteFansAndFriends(int userId);
	boolean addFriends(Integer userId, Integer toUserId);

	void delNewFriend(int userId);

	Friends deleteBlacklist(Integer userId, Integer toUserId);

	boolean deleteFriends(Integer userId, Integer toUserId);


	JSONMessage followUser(Integer userId, Integer toUserId);

	List<Fans> getFansList(Integer userId);

	PageVO getFansPage(int userId, int pageIndex, int pageSize);

	Friends getFriends(Friends friends);
	
	public Friends getFriends(int userId, int toUserId);
	
	List<Integer> getFriendsIdList(int userId);
	

	List<Friends> queryBlacklist(Integer userId);

	List<Integer> queryFansId(Integer userId);

	List<Friends> queryFollow(Integer userId,int status);

	List<Integer> queryFollowId(Integer userId);

	List<Friends> queryFriends(Integer userId);

	PageVO queryFriends(Integer userId,int status,String keyword, int pageIndex, int pageSize);


	boolean unfollowUser(Integer userId, Integer toUserId);

	Friends updateRemark(int userId, int toUserId, String remarkName);
	
	List<NewFriends> newFriendList(int userId,int pageIndex,int pageSize);

	List<Integer> friendsAndAttentionUserId(Integer userId, String type);
	//消息免打扰
	Friends updateOfflineNoPushMsg(int userId,int toUserId,int offlineNoPushMsg);
}
