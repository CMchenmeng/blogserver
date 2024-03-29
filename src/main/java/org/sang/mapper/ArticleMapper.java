package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Article;
import org.sang.bean.vo.ArticleBean;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sang on 2017/12/20.
 */
@Mapper
public interface ArticleMapper {
    int addNewArticle(Article article);

    int updateArticle(Article article);

    List<Article> getArticleByState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count,@Param("keywords") String keywords);

//    List<Article> getArticleByStateByAdmin(@Param("start") int start, @Param("count") Integer count, @Param("keywords") String keywords);

    int getArticleCountByState(@Param("state") Integer state, @Param("keywords") String keywords);

    List<Article> getArticleByUserAndState(@Param("state") Integer state, @Param("start") Integer start, @Param("count") Integer count,@Param("uid") Long uid,@Param("keywords") String keywords);

    int getArticleCountByUserAndState(@Param("state") Integer state, @Param("uid") Long uid,@Param("keywords") String keywords);

    int updateArticleState(@Param("aids") Long aids[], @Param("editTime") Timestamp editTime,@Param("state") Integer state);

    int deleteArticleById(@Param("aid") Long aids);

    Article getArticleById(Long aid);

    int updateArticleTop(@Param("aid") Long aid,@Param("editTime")Timestamp editTime,@Param("isTop") Integer isTop);

    void pvIncrement(Long aid);

    void pvReduce(Long aid);

    //INSERT INTO pv(countDate,pv,uid) SELECT NOW(),SUM(pageView),uid FROM article GROUP BY uid
    void pvStatisticsPerDay();

    List<String> getCategories(Long uid);

    List<Integer> getDataStatistics(Long uid);

    //统计作者文章发表数量
    List<ArticleBean> getArticleCountAndUser();
    //文章热帖
    List<Article> getHotArticle();
}
