package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Article;
import org.sang.bean.upFile;

import java.util.List;

@Mapper
public interface upFileMapper {
    int addNewupFile(upFile upfile);

    int updateupFile(upFile upfile);

    int addupFile(upFile upfile);

    upFile getupFileById(long fid);

    List<upFile> getupFileByState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count, @Param("uid") Integer uid, @Param("keywords") String keywords);

    int getupFileCountByState(@Param("state") Integer state, @Param("uid") int uid, @Param("keywords") String keywords);
}
