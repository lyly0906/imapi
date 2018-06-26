package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import cn.xyz.commons.utils.DateUtil;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(value = "u_friends", noClassnameStored = true)
@Indexes(@Index("userId,toUserId"))
public class Friends {

	public static class Blacklist {
		public static final int No = 0;
		public static final int Yes = 1;
	}

	public static class Status {
		/** 关注 */
		public static final int Attention = 1;// 2
		/** 好友 */
		public static final int Friends = 2;// 3
		/** 陌生人 */
		public static final int Stranger = 0;// 1
	}

	private Integer blacklist=0;// 是否拉黑（1=是；0=否）
	private Integer isBeenBlack=0; //是否被拉黑（1=是；0=否）
	
	private Integer offlineNoPushMsg=0;//消息免打扰（1=是；0=否）
	
	private int companyId;// 所属公司Id
	private long createTime;// 建立关系时间
	private @JSONField(serialize = false) @Id ObjectId id;// 关系Id
	private long modifyTime;// 修改时间
	
	private long lastTalkTime; //最后沟通时间
	
	private long msgNum;//未读消息数量
	private String remarkName;// 备注
	private Integer status;// 状态（1=关注；2=好友；0=陌生人）
	private String toNickname;// 好友昵称
	private int toUserId;// 好友Id
	private int userId;// 用户Id

	public Friends() {
		super();
	}

	public Friends(int userId) {
		super();
		this.userId = userId;
	}

	public Friends(int userId, int toUserId) {
		super();
		this.userId = userId;
		this.toUserId = toUserId;
	}

	public Friends(int userId, int toUserId,String toNickname, Integer status) {
		super();
		this.userId = userId;
		this.toUserId = toUserId;
		this.toNickname=toNickname;
		this.status = status;
	}

	public Friends(int userId, int toUserId,String toNickname, Integer status, Integer blacklist) {
		super();
		this.userId = userId;
		this.toUserId = toUserId;
		this.toNickname=toNickname;
		this.status = status;
		this.blacklist = blacklist;
		this.createTime = DateUtil.currentTimeSeconds();
	}

	public Integer getBlacklist() {
		return blacklist;
	}

	public Integer getIsBeenBlack() {
		return isBeenBlack;
	}

	public void setIsBeenBlack(Integer isBeenBlack) {
		this.isBeenBlack = isBeenBlack;
	}

	public int getCompanyId() {
		return companyId;
	}

	public Integer getOfflineNoPushMsg() {
		return offlineNoPushMsg;
	}

	public void setOfflineNoPushMsg(Integer offlineNoPushMsg) {
		this.offlineNoPushMsg = offlineNoPushMsg;
	}

	public long getCreateTime() {
		return createTime;
	}

	public ObjectId getId() {
		return id;
	}

	public long getModifyTime() {
		return modifyTime;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public Integer getStatus() {
		return status;
	}

	public String getToNickname() {
		return toNickname;
	}

	public int getToUserId() {
		return toUserId;
	}

	public int getUserId() {
		return userId;
	}

	public void setBlacklist(Integer blacklist) {
		this.blacklist = blacklist;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setToNickname(String toNickname) {
		this.toNickname = toNickname;
	}

	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getLastTalkTime() {
		return lastTalkTime;
	}

	public void setLastTalkTime(long lastTalkTime) {
		this.lastTalkTime = lastTalkTime;
	}

	public long getMsgNum() {
		return msgNum;
	}

	public void setMsgNum(long msgNum) {
		this.msgNum = msgNum;
	}

}