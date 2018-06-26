package cn.xyz.repository.mongo;

import cn.xyz.mianshi.vo.Chatkeep;
import cn.xyz.repository.ChatkeepRepository;
import cn.xyz.repository.MongoRepository;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/12/23.
 */
@Service
public class ChatkeepRepositoryImpl extends MongoRepository implements ChatkeepRepository {
    @Override
    public Object saveChatkeep(Chatkeep chatkeep) {
        return dsForRW.save(chatkeep).getId();
    }

    @Override
    public Chatkeep getChatkeep(int userId, int toUserId, String jid, int type){
        Query<Chatkeep> q = dsForRW.createQuery(Chatkeep.class);
        q.field("userId").equal(userId);
        if(type == 1){
            q.field("jid").equal(jid);
        }else{
            q.field("toUserId").equal(toUserId);
        }
        Chatkeep chatkeep = q.get();
        return chatkeep;
    }

    @Override
    public Chatkeep deleteChatkeep(int userId, int toUserId, String jid, int type){
        Query<Chatkeep> q = dsForRW.createQuery(Chatkeep.class);
        if(type == 1){
            q = dsForRW.createQuery(Chatkeep.class).field("userId").equal(userId).field("jid").equal(jid);
        }else{
           q = dsForRW.createQuery(Chatkeep.class).field("userId").equal(userId).field("toUserId").equal(toUserId);
        }
        return dsForRW.findAndDelete(q);
    }

    @Override
    public List<Chatkeep> getChatkeepList(int userId, int type){
        Query<Chatkeep> q = dsForRW.createQuery(Chatkeep.class).field("userId").equal(userId).field("type").equal(type);

        return q.asList();
    }
}
