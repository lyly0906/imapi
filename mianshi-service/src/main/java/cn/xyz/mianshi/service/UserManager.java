package cn.xyz.mianshi.service;

import java.util.List;
import java.util.Map;

import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.example.UserQueryExample;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.User.UserSettings;

import com.mongodb.DBObject;

public interface UserManager {

	User createUser(String telephone, String password);

	void createUser(User user);

	User.UserSettings getSettings(int userId);

	User getUser(int userId);
	
	/*User getfUser(int userId);*/

	User getUser(int userId, int toUserId);

	User getUser(String telephone);

	User getUserByPhone(String phone);
	
	String getNickName(int userId);

	int getUserId(String accessToken);

	boolean isRegister(String telephone);

	User login(String telephone, String password);
	

	Map<String, Object> login(UserExample example);
	Map<String, Object> loginv1(UserExample example);
	

	Map<String, Object> loginAuto(String access_token, int userId, String serial,String appId);

	void logout(String access_token,String areaCode,String userKey);
	
	void outtime(String access_token,int userId);

	List<DBObject> query(UserQueryExample param);

	Map<String, Object> register(UserExample example);

	Map<String, Object> registerIMUser(UserExample example);
	
	void addUser(int userId,String password);

	void resetPassword(String telephone, String password);

	void updatePassword(int userId, String oldPassword, String newPassword, int isadmin);

	User updateSettings(int userId,User.UserSettings userSettings);

	User updateUser(int userId, UserExample example);

	List<DBObject> findUser(int pageIndex, int pageSize);

	List<Integer> getAllUserId();
	//消息免打扰
	User updataOfflineNoPushMsg(int userId,int OfflineNoPushMsg);

}
