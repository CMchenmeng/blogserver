package org.sang.service;

import org.sang.bean.Article;
import org.sang.bean.Notice;
import org.sang.bean.Reply;
import org.sang.bean.vo.ArticleBean;
import org.sang.mapper.ArticleMapper;
import org.sang.mapper.ReplyMapper;
import org.sang.mapper.TagsMapper;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ArticleService {

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    ReplyMapper replyMapper;

    @Autowired
    TagsMapper tagsMapper;

    public int updateArticle(Article article) {
        //将html转换为纯文本
        String mdContent = Util.delHTMLTag(article.getHtmlContent());
        //处理文章摘要
        if (article.getSummary() == null || "".equals(article.getSummary())) {
            //直接截取
            String stripHtml = stripHtml(mdContent);
            article.setSummary(stripHtml.substring(0, stripHtml.length() > 50 ? 50 : stripHtml.length()));
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //设置发表日期
        article.setEditTime(timestamp);
        //更新
        // article.setEditTime(new Timestamp(System.currentTimeMillis()));
        int i = articleMapper.updateArticle(article);
        //修改标签
        String[] dynamicTags = article.getDynamicTags();
        if (dynamicTags != null && dynamicTags.length > 0) {
            int tags = addTagsToArticle(dynamicTags, article.getId());
            if (tags == -1) {
                return tags;
            }
        }
        return i;
    }

    public int addArticle(String title,String htmlContent,Long cid) {
        //添加操作
        Article article = new Article();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        article.setTitle(title);
        article.setHtmlContent(htmlContent);
        article.setCid(cid);
        //设置发表日期
        article.setPublishTime(timestamp);
        //设置当前用户
        article.setUid(Util.getCurrentUser().getId());
       //将html转换为纯文本
        String mdContent = Util.delHTMLTag(htmlContent);
        article.setMdContent(mdContent);
        //处理文章摘要 //直接截取
        String stripHtml = stripHtml(mdContent);
        article.setSummary(stripHtml.substring(0, stripHtml.length() > 50 ? 50 : stripHtml.length()));

        article.setState(0);
        article.setIsTop(0);
        article.setPageView(0);
        int i = articleMapper.addNewArticle(article);
        //打标签
        String[] dynamicTags = article.getDynamicTags();
        if (dynamicTags != null && dynamicTags.length > 0) {
            int tags = addTagsToArticle(dynamicTags, article.getId());
            if (tags == -1) {
                return tags;
            }
        }
        return i;
    }

    private int addTagsToArticle(String[] dynamicTags, Long aid) {
        //1.删除该文章目前所有的标签
        tagsMapper.deleteTagsByAid(aid);
        //2.将上传上来的标签全部存入数据库
        tagsMapper.saveTags(dynamicTags);
        //3.查询这些标签的id
        List<Long> tIds = tagsMapper.getTagsIdByTagName(dynamicTags);
        //4.重新给文章设置标签
        int i = tagsMapper.saveTags2ArticleTags(tIds, aid);
        return i == dynamicTags.length ? i : -1;
    }

    public String stripHtml(String content) {
        content = content.replaceAll("<p .*?>", "");
        content = content.replaceAll("<br\\s*/?>", "");
        content = content.replaceAll("\\<.*?>", "");
        return content;
    }

    public List<Article> getArticleByState(Integer state, Integer page, Integer count,String keywords) {
        int start = (page - 1) * count;
        //Long uid = Util.getCurrentUser().getId();
        return articleMapper.getArticleByState(state, start, count,keywords);
    }

//    public List<Article> getArticleByStateByAdmin(Integer page, Integer count,String keywords) {
//        int start = (page - 1) * count;
//        return articleMapper.getArticleByStateByAdmin(start, count,keywords);
//    }

    public int getArticleCountByState(Integer state,String keywords) {
        return articleMapper.getArticleCountByState(state,keywords);
    }

    public int getArticleCountByUserAndState(Integer state,Long uid,String keywords) {
        return articleMapper.getArticleCountByUserAndState(state,uid,keywords);
    }

    public List<Article> getArticleByUserAndState(Integer state, Integer page, Integer count,Long uid,String keywords) {
        int start = (page - 1) * count;
        //Long uid = Util.getCurrentUser().getId();
        return articleMapper.getArticleByUserAndState(state, start, count,uid,keywords);
    }

    public int updateArticleState(Long[] aids, Integer state) {
        Timestamp editTime = new Timestamp(System.currentTimeMillis());
        return articleMapper.updateArticleState(aids,editTime, state);//修改文章帖子的状态
    }

    public int deleteArticleById(Long aid) {
        int i = articleMapper.deleteArticleById(aid);
        if(i != 0)  //当文章帖子被删除后，对应的回复也需要删除。
            replyMapper.deleteReplyByAid(aid);
        return i;

    }

    public Article getArticleById(Long aid) {
        Article article = articleMapper.getArticleById(aid);

        //articleMapper.pvIncrement(aid); //当前用户对当前文章的浏览次数
        return article;
    }

    public int updateArticleTop(Long aid,Integer isTop){
        Timestamp editTime = new Timestamp(System.currentTimeMillis());
        return articleMapper.updateArticleTop(aid,editTime,isTop);
    }

    public void pvIncrement(Long aid){
      articleMapper.pvIncrement(aid);
    }

    public void pvReduce(Long aid){
        articleMapper.pvReduce(aid);
    }

    public void pvStatisticsPerDay() {
        articleMapper.pvStatisticsPerDay();
    }

    /**
     * 获取最近七天的日期
     * @return
     */
    public List<String> getCategories() {
        return articleMapper.getCategories(Util.getCurrentUser().getId());
    }

    /**
     * 获取最近七天的数据
     * @return
     */
    public List<Integer> getDataStatistics() {
        return articleMapper.getDataStatistics(Util.getCurrentUser().getId());
    }

    /**
     * 获取文章数量前十
     * @return
     */
    public List<ArticleBean> getArticleCountAndUser(){
        return articleMapper.getArticleCountAndUser();
    }

    //热帖，获取前十评论数的文章 发表人，文章标题，文章评论数
    public List<Article> getHotArticle(){
        return  articleMapper.getHotArticle();
    }
}
