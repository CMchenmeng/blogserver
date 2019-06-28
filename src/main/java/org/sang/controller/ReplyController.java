package org.sang.controller;

import org.sang.bean.Article;
import org.sang.bean.Reply;
import org.sang.bean.RespBean;
import org.sang.service.ArticleService;
import org.sang.service.ReplyService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    ReplyService replyService;

    @Autowired
    ArticleService articleService;

    //发布文章帖子评论
    @RequestMapping(value = "/addReply", method = RequestMethod.POST)
    public RespBean addNewReply(Reply reply,long aid){
        int result = replyService.addNewReply(aid,reply);
        if (result == 1) {
            //发布文章帖子评论后同时更新对应文章的评论次数
            articleService.pvIncrement(reply.getAid());
            return  RespBean.ok("发布帖子评论成功");
        } else {
            return new RespBean("error", reply.getState() == 0 ? "帖子评论保存失败!" : "帖子评论发表失败!");
        }
    }

    //当前普通用户删除当前发的帖子
   /* 1.判断当前的帖子是不是自己发的
    2.如果是，才可以进行删除操作
    3.删除帖子后，同时进行相应更新操作（文章对应的pageview-1）*/

   //管理员审核员角色后台对评论进行删除
   /* 1.直接对相应评论进行删除
    2.删除帖子后，同时进行相应更新操作（文章对应的pageview-1）*/
   @RequestMapping(value="/deleteReplyById",method = RequestMethod.PUT)
   public RespBean deleteReplyById(Long id){
       if(id == null){
           return RespBean.error("传入参数有误,请重新检查！");
       }
       if(Util.isShenhe()){
           if(replyService.deleteReplyById(id)==1){
               Reply reply = replyService.getReplyById(id);
               //删除帖子的同时，需要将对应文章的回复数量减少（-1）
               Article article = articleService.getArticleById(reply.getAid());
               //判断文章回复数大于0时才可以进行-1操作
               if(article.getPageView()>0){
                   articleService.pvReduce(reply.getAid());
               }
               return RespBean.ok("删除该帖子成功!");
           }
           return RespBean.error("删除该帖子失败！");
       }else
           return RespBean.error("你的权限不足，请联系管理员修改权限");
   }

   //更改帖子的状态（置0表示为可放入草稿箱）  暂时觉得此功能用不上
    @RequestMapping(value = "/updateReplyState",method = RequestMethod.POST)
    public RespBean updateReplyState(Long rid,Integer state){
       if (Util.isShenhe()){
           if(replyService.updateReplyState(rid,state)==1)
               return RespBean.ok("更改帖子状态成功，已放入草稿箱!");
           return RespBean.ok("更改帖子状态失败!");
       }else
           return RespBean.error("你的权限不足，请联系管理员修改权限");


    }

    //通过对应文章帖子的id获取该文章下的所有回复帖子
    @RequestMapping(value = "/getAllReply",method = RequestMethod.GET)
   public RespBean getReplyByActicleId(@RequestParam(value = "state", defaultValue = "1") Integer state,
                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "count", defaultValue = "6") Integer count,
                                       String keywords,Long aid){
        int totalCount = replyService.getReplyCountByActicleId(aid);
        List<Reply> replies = replyService.getAllReplyByActicleId(state, page,count,keywords,aid) ;
        if(replies!=null){
            Map<String, Object> map = new HashMap<>();
            map.put("totalCount", totalCount);
            map.put("replies", replies);

            return RespBean.ok("该文章的回复帖子列表",map);
        }else
            return RespBean.error("该文章暂时没有相关回复！");
    }

}
