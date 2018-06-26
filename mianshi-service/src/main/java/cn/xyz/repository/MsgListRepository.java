package cn.xyz.repository;

public interface MsgListRepository {

	void addToHotList(int cityId, int userId, String messageId, double score);

	void addToLatestList(int cityId, int userId, String messageId);

	Object getHotList(int cityId, int pageIndex, int pageSize);

	Object getLatestList(int cityId, int pageIndex, int pageSize);

	String getHotId(int cityId, Object userId);

	String getLatestId(int cityId, Object userId);

}
