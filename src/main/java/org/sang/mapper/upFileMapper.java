package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Article;
import org.sang.bean.upFile;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface upFileMapper {
    int addNewupFile(upFile upfile);

    int updateupFile(upFile upfile);

    int updateUpFileState(@Param("ids") Long ids[], @Param("editTime") Timestamp editTime, @Param("suid") Long suid,@Param("state") Integer state);

    int deleteUpFileById(@Param("ids") Long[] ids);

    int addupFile(upFile upfile);

    upFile getupFileById(@Param("id") Long id);

    List<upFile> getupFileByState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count,@Param("keywords") String keywords,@Param("begin") Integer begin,@Param("end") Integer end);

    int getupFileCountByState(@Param("state") Integer state, @Param("keywords") String keywords, @Param("begin") Integer begin,@Param("end") Integer end);

    List<upFile> getupFileByStateAndcid(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count,@Param("keywords") String keywords,@Param("cid") Long cid);

    int getupFileCountByStateAndcid(@Param("state") Integer state, @Param("keywords") String keywords, @Param("cid") Long cid);

    int upFileToFirst(@Param("id") Long id,@Param("editTime")Timestamp editTime,@Param("isTop") Integer isTop);

    void downIncrement(@Param("fid") Long fid);
}
