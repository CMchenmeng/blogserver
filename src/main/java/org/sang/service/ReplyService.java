package org.sang.service;

import org.sang.bean.Reply;
import org.sang.mapper.ArticleMapper;
import org.sang.mapper.ReplyMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class ReplyService {

    @Autowired
    ReplyMapper replyMapper;

    @Autowired
    ArticleMapper articleMapper;

    public int addNewReply(Long aid,Reply reply){
        //处理文通知公告摘要
        String stripHtml = stripHtml(reply.getContent());
        reply.setSummary(stripHtml.substring(0, stripHtml.length() > 50 ? 50 : stripHtml.length()));

        reply.setPublishTime(new Timestamp(System.currentTimeMillis()));
        reply.setEditTime(new Timestamp(System.currentTimeMillis()));  //业务逻辑时按照上传时间升序进行排序，所有新添加的帖子回复赋初值
        reply.setAid(aid);
        reply.setUid(Util.getCurrentUser().getId());
        reply.setState(1); //默认第一次添加的回复可以显示给所有用户（否则用户不知道自己是否已发表评论成功）
        return replyMapper.addNewReply(reply);
    }
    public String stripHtml(String content) {
        content = content.replaceAll("<p .*?>", "");
        content = content.replaceAll("<br\\s*/?>", "");
        content = content.replaceAll("\\<.*?>", "");
        return content;
    }
    public int deleteReplyById(Long id){
        return replyMapper.deleteReplyById(id);
    }

    public int deleteReplyByAid(Long aid){
        return replyMapper.deleteReplyByAid(aid);
    }

    public int updateReplyState(Long rid,Integer state){
        return replyMapper.updateReplyState(rid,state);
    }

    public int getReplyCountByActicleId(Long aid){
        return replyMapper.getReplyCountByActicleId(aid);
    }

    public List<Reply> getAllReplyByActicleId(Integer state, Integer page, Integer count,String keywords,Long aid){
        int start = (page - 1) * count;
        return replyMapper.getAllReplyByActicleId(state,start,count,keywords,aid);
    }

    public Reply getReplyById(Long id){
        return replyMapper.getReplyById(id);
    }
}
