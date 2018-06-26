package cn.xyz.repository;

import cn.xyz.mianshi.vo.Chatkeep;

import java.util.List;

/**
 * Created by Administrator on 2017/12/23.
 */
public interface ChatkeepRepository {
    Object saveChatkeep(Chatkeep chatkeep);

    Chatkeep getChatkeep(int userId, int toUserId, String jid, int type);

    Chatkeep deleteChatkeep(int userId, int toUserId, String jid, int type);

    List<Chatkeep> getChatkeepList(int userId, int type);
}
