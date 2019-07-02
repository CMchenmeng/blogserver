package org.sang.controller;

import org.apache.commons.io.IOUtils;
import org.sang.bean.Article;
import org.sang.bean.Notice;
import org.sang.bean.Reply;
import org.sang.bean.RespBean;
import org.sang.service.ArticleService;
import org.sang.service.NoticeService;
import org.sang.service.ReplyService;
import org.sang.service.UserService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/article")
public class ArticleController {


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    ArticleService articleService;

    @Autowired
    NoticeService noticeService;

    //获取通知公告列表(所有用户可见)
    @RequestMapping(value = "/allNotice", method = RequestMethod.GET)   //按照editTime进行降序排序
    public RespBean getNotice(@RequestParam(value = "state",defaultValue = "1") Integer state,
                              @RequestParam(value = "page",defaultValue = "1") Integer page,
                              @RequestParam(value = "count", defaultValue = "6") Integer count,
                              String keywords){
        int totalCount = noticeService.getNoticeCountByState(state, keywords);
        List<Notice> notices = noticeService.getNoticeByState(state, page, count,keywords);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", totalCount);
        map.put("notices",notices);
        return RespBean.ok("获取通知公告成功",map);
    }

    @RequestMapping(value = "/getNoticeById", method = RequestMethod.GET)
    public RespBean getNoticeById( Long id) {
        if(id ==null)
            return RespBean.error("输入的通知公告id为空,请重新输入");
        Notice notice = noticeService.getNoticeById(id);
        if(notice != null)
            return RespBean.ok("根据id获取对应通知公告成功",notice);
        else{
            return RespBean.error("根据id获取对应通知公告失败");
        }
    }

    @RequestMapping(value = "/addArticle", method = RequestMethod.POST)
    public RespBean addArticle(String title,String htmlContent,Long cid) {   //0代表更新文章帖子操作，1代表添加文章帖子操作
        if(title == null || title == "" || htmlContent == null || htmlContent =="" || cid == null || cid.intValue() < 0 || cid.intValue() > 100)
            return RespBean.error("文章帖子输入内容为空或不完整，请检查后选择正确的操作！");
        int result = articleService.addArticle(title,htmlContent,cid);  //chooseId=1为添加操作，=0的为更改操作
        if (result == 1) {
            return RespBean.ok("更新/发表文章帖子成功");
        } else {
            return RespBean.ok("更新/发表文章帖子失败!");
        }
    }

    @RequestMapping(value = "/deleteArticle", method = RequestMethod.PUT)
    public  RespBean deleteArticleById(Long aid){
        if(aid == null)
            return RespBean.error("输入的文章帖子id为空，请重新输入");
        if(Util.isShenhe()){
            int result = articleService.deleteArticleById(aid);
            if(result == 1)
                return RespBean.ok("文章帖子已删除！");
            return  RespBean.error("删除文章帖子失败！");
        }else
            return RespBean.error("你的权限不足，请联系管理员修改权限");
    }

    //审核员以上权限角色可以对文章帖子进行审核，将状态更改为1   0表示草稿箱待审核，1表示已发表可展示
    @RequestMapping(value = "/updateArticleState", method = RequestMethod.PUT)
    public  RespBean updateArticleStateById(Long[] aids, Integer state){
        if(aids.length == 0 || aids == null)
            return RespBean.error("输入的文章帖子id为空，请重新输入");
        if(state != 0 && state != 1 )
            return RespBean.error("输入文章帖子的state值有误");
        if(Util.isShenhe()){
            int result = articleService.updateArticleState(aids,state);
            if(result == 1)
                return RespBean.ok("文章帖子状态已修改！");
            return  RespBean.error("修改文章帖子状态失败！");
        }else
            return RespBean.error("你的权限不足，请联系管理员修改权限");
    }

    //更新文章帖子的内容，帖子的创作者进行更改
    @RequestMapping(value = "/updateArticle", method = RequestMethod.POST)
    public RespBean updateArticle(String title,String htmlContent,Long aid) {   //0代表更新文章帖子操作，1代表添加文章帖子操作
        if(title == null || title == ""  || htmlContent == null || htmlContent =="" || aid==null)
            return RespBean.error("文章帖子输入内容为空或不完整，请检查后选择正确的操作！");
        Article article = articleService.getArticleById(aid);
        if(Util.getCurrentUser().getId() == article.getUid() ){
            article.setTitle(title);
            article.setHtmlContent(htmlContent);
            int result = articleService.updateArticle(article);
            if (result == 1) {
                return RespBean.ok("更新/发表文章帖子成功");
            } else {
                return RespBean.ok("更新/发表文章帖子失败!");
            }
        }else
            return RespBean.error("您不是当前文章帖子的创作者，不可以进行修改");
    }
    /**
     * 上传图片
     *
     * @return 返回值为图片的地址
     */
    @RequestMapping(value = "/uploadimg", method = RequestMethod.POST)
    public RespBean uploadImg(HttpServletRequest req, MultipartFile image) {
        StringBuffer url = new StringBuffer();
        String filePath = "/blogimg/" + sdf.format(new Date());
        String imgFolderPath = req.getServletContext().getRealPath(filePath);
        File imgFolder = new File(imgFolderPath);
        if (!imgFolder.exists()) {
            imgFolder.mkdirs();
        }
        url.append(req.getScheme())
                .append("://")
                .append(req.getServerName())
                .append(":")
                .append(req.getServerPort())
                .append(req.getContextPath())
                .append(filePath);
        String imgName = UUID.randomUUID() + "_" + image.getOriginalFilename().replaceAll(" ", "");
        try {
            IOUtils.write(image.getBytes(), new FileOutputStream(new File(imgFolder, imgName)));
            url.append("/").append(imgName);
            return new RespBean("success", url.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new RespBean("error", "上传失败!");
    }

    @RequestMapping(value = "/allArticle", method = RequestMethod.GET)
    public RespBean getArticleByState(@RequestParam(value = "state", defaultValue = "1") Integer state,
                                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                 @RequestParam(value = "count", defaultValue = "10") Integer count,
                                                 String keywords) {
        int totalCount = articleService.getArticleCountByState(state,keywords);
        List<Article> articles = articleService.getArticleByState(state, page, count,keywords);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", totalCount);
        map.put("articles", articles);
        return RespBean.ok("获取文章帖子成功",map);
    }

    //置顶操作
    @RequestMapping(value = "/updateArticleTop",method = RequestMethod.POST)
    public RespBean updateArticleTop(Long aid,Integer isTop){
        if(Util.isShenhe()){
            int i = articleService.updateArticleTop(aid,isTop);
            if (i == 1) {
                return new RespBean("success", "文章帖子设置/取消置顶操作成功!");
            }
            return new RespBean("error", "文章帖子设置/取消置顶操作失败!");
        }else
            return RespBean.error("你的权限不足，请联系管理员修改权限");
    }

    @RequestMapping(value = "/getArticleById", method = RequestMethod.GET)
    public RespBean getArticleById( Long aid) {
        if(aid == null)
            return RespBean.error("输入的文章帖子id为空");
        Article article = articleService.getArticleById(aid);
        return RespBean.ok("获取对应id的文章帖子成功",article);
    }

    @RequestMapping(value = "/dustbin", method = RequestMethod.PUT)
    public RespBean updateArticleState(Long[] aids, Integer state) {
        if (articleService.updateArticleState(aids, state) == aids.length) {
            return  RespBean.ok("操作成功!");
        }
        return RespBean.error( "操作失败!");
    }

    @RequestMapping("/dataStatistics")
    public Map<String,Object> dataStatistics() {
        Map<String, Object> map = new HashMap<>();
        List<String> categories = articleService.getCategories();
        List<Integer> dataStatistics = articleService.getDataStatistics();
        map.put("categories", categories);
        map.put("ds", dataStatistics);
        return map;
    }


    //根据当前用户获取其已发表的文章帖子
    @RequestMapping(value = "/getArticleByCurrentUser",method = RequestMethod.GET)
    public RespBean getArticleByCurrerntUser(@RequestParam(value = "state", defaultValue = "0") Integer state,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "count", defaultValue = "10") Integer count,
                                    String keywords){
        int totalCount = articleService.getArticleCountByUserAndState(state,Util.getCurrentUser().getId(),keywords);
        List<Article> articles = articleService.getArticleByUserAndState(state, page, count,Util.getCurrentUser().getId(),keywords);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", totalCount);
        map.put("articles", articles);
        return RespBean.ok("获取当前用户发表的文章帖子成功",map);
    }

}
