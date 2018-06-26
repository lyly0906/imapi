/**
 * 
 */
package cn.xyz.mianshi.vo;

import cn.xyz.commons.utils.DateUtil;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author lidaye
 * 2017年7月21日
 */
@Entity(value="NewFriends",noClassnameStored=true)
public class NewFriends {
	
	private @JSONField(serialize = false) @Id ObjectId id;// 关系Id
	
	private long createTime;// 建立关系时间
	private long modifyTime;// 修改时间
	
	private String content;// 信息内容
	
	private int direction;// 0=发出去的；1=收到的
	private int type;// 消息Type  
	private Integer status;// 状态（1=关注；2=好友；0=陌生人）
	private String toNickname;// 好友昵称
	private int toUserId;// 好友Id
	private int userId;// 用户Id

	public NewFriends() {
		super();
	}

	public NewFriends(int userId) {
		super();
		this.userId = userId;
	}

	public NewFriends(int userId, int toUserId) {
		super();
		this.userId = userId;
		this.toUserId = toUserId;
	}

	public NewFriends(int userId, int toUserId,String toNickname, Integer status) {
		super();
		this.userId = userId;
		this.toUserId = toUserId;
		this.toNickname=toNickname;
		this.status = status;
	}

	public NewFriends(int userId, int toUserId,String toNickname, Integer status, Integer type,Integer direction,String content) {
		super();
		this.userId = userId;
		this.toUserId = toUserId;
		this.toNickname=toNickname;
		this.status = status;
		this.type = type;
		this.direction = direction;
		this.content = content;
		this.modifyTime = DateUtil.currentTimeSeconds();
		this.createTime = DateUtil.currentTimeSeconds();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getToNickname() {
		return toNickname;
	}
	public void setToNickname(String toNickname) {
		this.toNickname = toNickname;
	}
	public int getToUserId() {
		return toUserId;
	}
	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
	

}
