package cn.xyz.repository;

import java.util.List;

import cn.xyz.mianshi.vo.Fans;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.NewFriends;

public interface FriendsRepository {


	Fans deleteFans(int userId, int toUserId);
	void deleteFans(int userId);


	Friends deleteFriends(int userId, int toUserId);
	void deleteFriends(int userId);

    void deleteNewFriends(int userId);
	Friends getFriends(int userId, int toUserId);

	List<Friends> queryBlacklist(int userId);

	List<Fans> getFansList(int userId);

	List<Integer> queryFansId(int userId);

	List<Friends> queryFollow(int userId,int status);

	List<Integer> queryFollowId(int userId);

	List<Friends> queryFriends(int userId);
	
	List<Friends> friendsOrBlackList(int userId,String type);


	Object saveFans(Fans fans);


	Object saveFriends(Friends friends);

	Object saveNewFriends(NewFriends friends);

	Friends updateFriends(Friends friends);

	NewFriends updateNewFriends(NewFriends friends);
}
