package cn.xyz.commons.constants;

public class KKeyConstant {
	public static final String HotMsgIdListTemplate = "msg.id.hot.list:%1$s";
	public static final String HotMsgListTemplate = "msg.hot.list:%1$s";
	public static final String LatestMsgIdListTemplate = "msg.id.latest.list:%1$s";
	public static final String LatestMsgListTemplate = "msg.latest.list:%1$s";
	public static final String UserHotMsgIdTemplate = "user.msg.hot:%1$s";
	public static final String UserLatestMsgIdTemplate = "user.msg.latest:%1$s";

	// user:%1$s
	// user:id:%1$s
	// user:access_token:%1$s
	/**
	 * 根据userKey生成获取accessToken的键
	 * 
	 * @param userKey
	 * @return uk_${userKey}
	 */
	public static String atKey(String userKey) {
		return String.format("uk_%1$s", userKey);
	}

	/**
	 * 根据accessToken生成获取userId的键
	 * 
	 * @param accessToken
	 * @return at_${accessToken}
	 */
	public static String userIdKey(String accessToken) {
		return String.format("at_%1$s", accessToken);
	}

	/**
	 * 根据userId生成获取用户数据的键
	 * 
	 * @param userId
	 *            用户Id
	 * @return
	 */
	public static String userKey(Integer userId) {
		return String.format("u_%1$s", userId);
	}
}
