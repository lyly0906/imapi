package com.shiku.mianshi.controller;

import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.ChatkeepManager;
import cn.xyz.mianshi.service.impl.ChatkeepManagerImpl;
import cn.xyz.mianshi.vo.Chatkeep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 关系接口
 *
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/chatkeep")
public class ChatkeepController {
    @Autowired
    private ChatkeepManager chatkeepManager;

    //新增需要删除的会话id
    @RequestMapping("/add")
    public JSONMessage addChatKeep(@RequestParam(defaultValue = "") Integer toUserId,@RequestParam(defaultValue = "") String jid, @RequestParam Integer type) {
        chatkeepManager.keepUser(ReqUtil.getUserId(), toUserId, jid, type);
        return JSONMessage.success(null);
    }

    //删除会话id
    @RequestMapping("/delete")
    public JSONMessage delChatKeep(@RequestParam(defaultValue = "") Integer toUserId,@RequestParam(defaultValue = "") String jid, @RequestParam Integer type) {
        chatkeepManager.delkeepUser(ReqUtil.getUserId(), toUserId, jid, type);
        return JSONMessage.success(null);
    }

    //返回会话记录
    @RequestMapping("/get")
    public JSONMessage ChatKeepList(@RequestParam Integer type) {
        List<Chatkeep> data = chatkeepManager.queryChatkeep(ReqUtil.getUserId(),type);
        return JSONMessage.success(null, data);
    }
}
