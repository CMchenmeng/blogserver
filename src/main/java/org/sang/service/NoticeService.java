package org.sang.service;

import org.sang.bean.Notice;
import org.sang.mapper.NoticeMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class NoticeService {

    @Autowired
    NoticeMapper noticeMapper;


    public int addNotice(Notice notice){

        //处理文通知公告摘要
        if (notice.getSummary() == null || "".equals(notice.getSummary())) {
            //直接截取
            String stripHtml = stripHtml(notice.getContent());
            notice.setSummary(stripHtml.substring(0, stripHtml.length() > 50 ? 50 : stripHtml.length()));
        }

        notice.setPublishTime(new Timestamp(System.currentTimeMillis()));
        notice.setEditTime(new Timestamp(System.currentTimeMillis()));  //由于业务逻辑时按照修改时间进行排序，所有新添加的通知公告赋初值
        notice.setUid(Util.getCurrentUser().getId());
        notice.setState(1); //默认第一次添加的通知公告可以显示给所有用户  由管理员添加，所以不需要审核
        notice.setPageView(0);
        notice.setIsTop(0);
        return noticeMapper.addNotice(notice);
    }

    public String stripHtml(String content) {
        content = content.replaceAll("<p .*?>", "");
        content = content.replaceAll("<br\\s*/?>", "");
        content = content.replaceAll("\\<.*?>", "");
        return content;
    }

    public Notice getNoticeById(Long aid) {
        Notice notice = noticeMapper.getNoticeById(aid);
        //articleMapper.pvIncrement(aid); //当前用户对当前文章的浏览次数
        return notice;
    }

    public int getNoticeCountByState(Integer state,Long uid,String keywords){
        return noticeMapper.getNoticeCountByState(state,uid,keywords);
    }

    public List<Notice> getNoticeByState(Integer state, Integer page, Integer count,String keywords){
        int start = (page - 1) * count;
        Long uid = Util.getCurrentUser().getId();
        return noticeMapper.getNoticeByState(state, start, count, uid,keywords);
    }

    public int upNoticeToFirst(Long id,Integer isTop){
        Timestamp editTime = new Timestamp(System.currentTimeMillis());
        return noticeMapper.upNoticeToFirst(id,editTime,isTop);
    }

    public int updateNoticeStateById(Long id,Integer state){
        Timestamp editTime = new Timestamp(System.currentTimeMillis());
        return noticeMapper.updateNoticeStateById(id,state,editTime);
    }

    public int deleteNoticeById(Long noticeId){
        return noticeMapper.deleteNoticeById(noticeId);
    }


}
