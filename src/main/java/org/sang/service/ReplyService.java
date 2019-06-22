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

    public int addNewReply(Reply reply){
        //处理文通知公告摘要
        if (reply.getSummary() == null || "".equals(reply.getSummary())) {
            //直接截取
            String stripHtml = stripHtml(reply.getContent());
            reply.setSummary(stripHtml.substring(0, stripHtml.length() > 50 ? 50 : stripHtml.length()));
        }

        reply.setPublishTime(new Timestamp(System.currentTimeMillis()));
       // reply.setEditTime(new Timestamp(System.currentTimeMillis()));  //由于业务逻辑时按照修改时间升序进行排序，所有新添加的帖子回复不需要赋初值
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

    public int updateReplyState(Reply reply){
        return replyMapper.updateReplyState(reply);
    }

    public int getReplyCountByActicleId(Long aid){
        return replyMapper.getReplyCountByActicleId(aid);
    }

    public List<Reply> getAllReplyByActicleId(Integer state, Integer page, Integer count,String keywords,Long aid){
        int start = (page - 1) * count;
        return replyMapper.getAllReplyByActicleId(state,start,count,keywords,aid);
    }
}
