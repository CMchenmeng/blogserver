package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Reply;

import java.util.List;

@Mapper
public interface ReplyMapper {

    int addNewReply(Reply reply);

    int deleteReplyById(Long id);

    int deleteReplyByAid(Long aid);

    int updateReplyState(Long rid,Integer state);

    Reply getReplyById(@Param("id") Long id);

    int getReplyCountByActicleId(@Param("aid") Long aid);

    List<Reply> getAllReplyByActicleId(@Param("state")Integer state, @Param("start")Integer start,
                                       @Param("count") Integer count, @Param("keywords")String keywords, @Param("aid")Long aid);
}
