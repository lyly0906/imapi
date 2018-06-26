package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import cn.xyz.commons.utils.DateUtil;

import com.alibaba.fastjson.annotation.JSONField;

@Entity(value = "u_chatkeep", noClassnameStored = true)
@Indexes(@Index("userId,toUserId"))
/**
 * Created by Administrator on 2017/12/23.
 */
public class Chatkeep {

    private @JSONField(serialize = false) @Id ObjectId id;// 粉丝关系Id
    private long time;// 添加时间
    private String toNickname;// 会话用户昵称
    private int toUserId;// 会话用户Id
    private int userId;// 用户Id
    private String jid; // 群id
    private int type; // 用户类型 0 为单聊 1 为群聊

    public Chatkeep() {
        super();
    }


    public Chatkeep(int userId, int toUserId, String jid, int type) {
        super();
        this.userId = userId;
        this.toUserId = toUserId;
        this.jid = jid;
        this.time = DateUtil.currentTimeSeconds();
        this.type = type;
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

    public String getJid() {
        return jid;
    }

    public int getType() { return type; }

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

    public void setJid(String jid) { this.jid = jid; }

    public void setType(int type) { this.type = type; }
}
