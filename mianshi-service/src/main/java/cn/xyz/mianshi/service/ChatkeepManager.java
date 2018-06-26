package cn.xyz.mianshi.service;

import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.vo.Chatkeep;

import java.util.List;

/**
 * Created by Administrator on 2017/12/23.
 */
public interface ChatkeepManager {
    JSONMessage keepUser(Integer userId, Integer toUserId, String jid, Integer type);

    JSONMessage delkeepUser(Integer userId, Integer toUserId, String jid, Integer type);

    List<Chatkeep> queryChatkeep(Integer userId, Integer type);
}
