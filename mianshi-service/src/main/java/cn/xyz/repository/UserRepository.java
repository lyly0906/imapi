package cn.xyz.repository;

import java.util.List;
import java.util.Map;

import cn.xyz.mianshi.example.UserExample;
import cn.xyz.mianshi.example.UserQueryExample;
import cn.xyz.mianshi.vo.User;

import com.mongodb.DBObject;

public interface UserRepository {

	Map<String, Object> addUser(int userId, UserExample param);

	void addUser(User user);
	
	void addUser(int userId,String password);

	List<User> findByTelephone(List<String> telephoneList);

	Map<String, Object> getAT(int userId, String userKey);

	long getCount(String telephone);

	User.LoginLog getLogin(int userId);

	User.UserSettings getSettings(int userId);

	User getUser(int userId);

	User getUser(String telephone);

	User getUserByPhone(String phone);

	User getUser(String areaCode,String userKey, String password);
	User getUserv1(String userKey, String password);

	List<DBObject> queryUser(UserQueryExample param);

	List<DBObject> findUser(int pageIndex, int pageSize);

	Map<String, Object> saveAT(int userId, String uesrKey);

	void updateLogin(int userId, String serial);

	void updateLogin(int userId, UserExample example);

	User updateUser(int userId, UserExample param);
	
	User updateSettings(int userId,User.UserSettings userSettings);

	User updateUser(User user);

	void updatePassword(String telephone, String password);

	void updatePassowrd(int userId, String password);

}
