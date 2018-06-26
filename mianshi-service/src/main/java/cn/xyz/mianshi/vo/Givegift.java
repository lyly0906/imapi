package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import cn.xyz.commons.utils.JSONUtil;

@Entity(value = "givegift", noClassnameStored = true)
public class Givegift {
	private int count;// 礼物数量
	private @Id ObjectId giftId;// 礼物Id
	private @Indexed int id;// 送礼物记录Id
	private @Indexed ObjectId msgId;// 送礼物所属消息Id
	private String nickname;// 送礼物用户昵称
	private Double price;// 礼物价格
	private long time;// 送礼物时间
	private @Indexed int userId;// 送礼物用户Id

	public Givegift() {}
	
	public Givegift(int count, ObjectId giftId, int id, ObjectId msgId, String nickname, Double price, long time,
			int userId) {
		this.count = count;
		this.giftId = giftId;
		this.id = id;
		this.msgId = msgId;
		this.nickname = nickname;
		this.price = price;
		this.time = time;
		this.userId = userId;
	}

	public int getCount() {
		return count;
	}

	public ObjectId getGiftId() {
		return giftId;
	}

	public int getId() {
		return id;
	}

	public ObjectId getMsgId() {
		return msgId;
	}

	public String getNickname() {
		return nickname;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public long getTime() {
		return time;
	}

	public int getUserId() {
		return userId;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setGiftId(ObjectId giftId) {
		this.giftId = giftId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMsgId(ObjectId msgId) {
		this.msgId = msgId;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return JSONUtil.toJSONString(this);
	}

}
