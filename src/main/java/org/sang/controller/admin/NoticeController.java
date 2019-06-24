package org.sang.controller.admin;

import org.sang.bean.Notice;
import org.sang.bean.RespBean;
import org.sang.service.NoticeService;
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
@RequestMapping("/admin")
public class NoticeController {

    @Autowired
    NoticeService noticeService;

    @RequestMapping(value = "/addNotice",method = RequestMethod.POST)
    public RespBean addNotice(Notice notice){
        if(notice == null)
            return RespBean.error("添加的通知公告内容为空！");
        int result = noticeService.addNotice(notice);
        if(result==1)
            return RespBean.ok("success", "添加通知公告成功!");
        return  RespBean.error("添加通知公告失败!");
    }


    //置顶操作，由于时按照editTime进行降序排序，只需要更改Notice中的editTime为当前时间即可
    @RequestMapping(value = "/upNoticeToFirst",method = RequestMethod.POST)
    public RespBean updateNoticeToFirst(Long id){
        int i = noticeService.upNoticeToFirst(id);
        if (i == 1) {
            return new RespBean("success", "置顶操作成功!");
        }
        return new RespBean("error", "置顶操作失败!");
    }

    //将通知公告放入草稿箱  即设置state为0  state由前端传入
    @RequestMapping(value = "/updateNoticeState",method = RequestMethod.POST)
    public RespBean updateNoticeState(Long id,Integer state){
        if(state == null)
            return RespBean.error("请设置要更改的state值!");
        int i = noticeService.updateNoticeStateById(id,state);
        if (i == 1) {
            return  RespBean.ok("通知公告状态已更改");
        }
        return RespBean.error( "操作失败!");
    }

    //删除通知公告
    @RequestMapping(value = "/deleteNotice", method = RequestMethod.PUT)
    public RespBean deleteNoticeById( Long id) {
        if (noticeService.deleteNoticeById(id)==1) {
            return new RespBean("success", "删除通知公告成功!");
        }
        return new RespBean("error", "删除通知公告失败!");
    }
}
