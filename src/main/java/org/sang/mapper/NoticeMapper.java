package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Notice;

import java.util.List;

@Mapper
public interface NoticeMapper {

    int addNotice(Notice notice);

    int updateNoticeStateById(Notice notice);

    int upNoticeToFirst(Notice notice);

    int deleteNoticeById( Long id);

    Notice getNoticeById( Long id);

    int getNoticeCountByState(@Param("state") Integer state, @Param("uid") Long uid, @Param("keywords") String keywords);

    List<Notice> getNoticeByState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count, @Param("uid") Long uid, @Param("keywords") String keywords);

}
