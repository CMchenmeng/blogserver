package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Notice;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface NoticeMapper {

    int addNotice(Notice notice);

    int updateNoticeStateById(@Param("id") Long id,@Param("state") Integer state, @Param("editTime")Timestamp editTime);

    int upNoticeToFirst(@Param("id") Long id,@Param("editTime")Timestamp editTime,@Param("isTop") Integer isTop);

    int deleteNoticeById(@Param("id") Long id);

    Notice getNoticeById( Long id);

    int getNoticeCountByState(@Param("state") Integer state, @Param("uid") Long uid, @Param("keywords") String keywords);

    List<Notice> getNoticeByState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count, @Param("uid") Long uid, @Param("keywords") String keywords);

}
