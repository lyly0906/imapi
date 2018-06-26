package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import cn.xyz.commons.utils.DateUtil;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(value = "u_fans", noClassnameStored = true)
@Indexes(@Index("userId,toUserId"))
public class Fans {

	private @JSONField(serialize = false) @Id ObjectId id;// 粉丝关系Id
	private long time;// 关注世间
	private String toNickname;// 粉丝用户昵称
	private int toUserId;// 粉丝用户Id
	private int userId;// 用户Id

	public Fans() {
		super();
	}

	public Fans(int userId, int toUserId) {
		super();
		this.userId = userId;
		this.toUserId = toUserId;
		this.time = DateUtil.currentTimeSeconds();
	}

	public ObjectId getId() {
		return id;
	}

	public long getTime() {
		return time;
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

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setTime(long time) {
		this.time = time;
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

}
