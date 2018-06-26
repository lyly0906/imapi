package cn.xyz.mianshi.service.impl;

import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.ChatkeepManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.vo.Chatkeep;
import cn.xyz.mianshi.vo.Friends;
import cn.xyz.mianshi.vo.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.xyz.repository.ChatkeepRepository;

import java.util.List;

/**
 * Created by Administrator on 2017/12/23.
 */
@Service
public class ChatkeepManagerImpl extends MongoRepository<Friends, ObjectId> implements ChatkeepManager {
    @Autowired
    private UserManager userService;
    @Autowired
    private ChatkeepRepository chatkeepRepository;
    @Override
    public JSONMessage keepUser(Integer userId, Integer toUserId, String jid, Integer type) {

        Chatkeep chatkeep = chatkeepRepository.getChatkeep(userId, toUserId, jid, type);
        if (null == chatkeep) {
            chatkeepRepository.saveChatkeep(new Chatkeep(userId,toUserId,jid,type));
            return JSONMessage.success("增加会话成功！");
        }else{
            return JSONMessage.failure("已存在该会话关系！");
        }
    }

    @Override
    public JSONMessage delkeepUser(Integer userId, Integer toUserId, String jid, Integer type) {
        Chatkeep chatkeep = chatkeepRepository.getChatkeep(userId, toUserId, jid, type);
        if(null != chatkeep){
            // 说明有记录了，则执行删除操作
            chatkeepRepository.deleteChatkeep(userId,toUserId,jid,type);
            return JSONMessage.success("会话删除成功！");
        }else{
            return JSONMessage.failure("还不存在会话关系！");
        }
    }

    @Override
    public List<Chatkeep> queryChatkeep(Integer userId, Integer type) {
        List<Chatkeep> result = chatkeepRepository.getChatkeepList(userId,type);
        return result;
    }
}
