package org.sang.controller;

import org.sang.bean.Category;
import org.sang.bean.RespBean;
import org.sang.bean.upFile;
import org.sang.bean.vo.upFileBean;
import org.sang.service.CategoryService;
import org.sang.service.upFileService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upfile")
public class upFileController {

    @Autowired
    upFileService upfileService;

    @Autowired
    CategoryService categoryService;

    //Id =1代表获取法律法规的子类别   20 < Id  < 35的所有category
    //Id =2代表获取学习资料的子类别   36 < Id  < 50的所有category
    @RequestMapping(value = "/getCategories", method = RequestMethod.GET)
    public RespBean getCategoriesById(Integer type){
        if(type == 1 || type == 2){
            List<Category> list = categoryService.getCategoriesById(type);
            if (!list.isEmpty()){
                Map<String, Object> map = new HashMap<>();
                map.put("categories",list);
                return RespBean.ok("获得对应模块的分类类别成功",map);
            }
            return RespBean.error("获得对应模块的分类类别失败");
        }
        return RespBean.error("获取类别的id有误，请检查重新输入!");
    }

    //type=1 代表查询法律法规文件  type=2 代表查询学习资料文件
    @RequestMapping(value = "/allUpfile", method = RequestMethod.GET)
    public RespBean getupFileByState(@RequestParam(value = "state",defaultValue = "1") Integer state,
                                     @RequestParam(value = "page",defaultValue = "1") Integer page,
                                     @RequestParam(value = "count", defaultValue = "6") Integer count,
                                     Integer type ,String keywords){
        if(type  == 1 || type  == 2){
            int totalCount = upfileService.getupFileCountByState(state,type ,keywords);
            List<upFile> upfiles = upfileService.getupFileByState(state, page, count,type ,keywords);
            Map<String, Object> map = new HashMap<>();
            map.put("totalCount", totalCount);
            map.put("upfiles", upfiles);
            return RespBean.ok("获得相关文件成功",map);
        }
        return RespBean.error("获取类别的id有误，请检查重新输入!");
    }

    //根据类别cid获取相应所有文件资料
    @RequestMapping(value = "getUpFileBycid",method = RequestMethod.GET)
    public RespBean getupFileBycid(@RequestParam(value = "state",defaultValue = "1") Integer state,
                                   @RequestParam(value = "page",defaultValue = "1") Integer page,
                                   @RequestParam(value = "count", defaultValue = "6") Integer count,
                                   Long cid,String keywords){
        if(cid != null){
            int totalCount = upfileService.getupFileCountByStateAndcid(state,cid,keywords);
            List<upFile> upfiles = upfileService.getupFileByStateAndcid(state, page, count,cid ,keywords);
            Map<String, Object> map = new HashMap<>();
            map.put("totalCount", totalCount);
            map.put("upfiles", upfiles);
            return RespBean.ok("获得相应类别下的文件资料成功！",map);
        }
        return RespBean.error("获取对应类别的cid有误，请检查重新输入!");
    }

    //审核员模块
    //0代表未审核，1代表审核通过，2代表删除该文件
    @RequestMapping(value = "updateUpfileState" ,method=RequestMethod.POST)
    public RespBean updateFileStateById(Long[] ids,Integer state){
        if(ids.length == 0 || ids== null){
            return RespBean.error("传入的文件id有误，请重新检查!");
        }
        if(state==null ||state < 0 || state > 2 )
            return RespBean.error("输入相关文件的state值有误");
        if(Util.isShenhe()){
            String result = upfileService.updateUpFileState(ids,state);
            if(result != null && result.length() != 0)
                return RespBean.ok(result);
            return  RespBean.error("后台没有对相应资料状态进行处理，请检查传入的ids是否正确！");
        }else
            return RespBean.error("你的权限不足，请联系管理员修改权限");
    }

    //置顶操作
    @RequestMapping(value = "/updateUpfileTop",method = RequestMethod.GET)
    public RespBean upFileToFirst(Long id,Integer isTop){
        if(isTop.intValue()<0 || isTop.intValue() >1)
            return RespBean.error("输入的isTop值有误！请检查后重新输入");
        if(Util.isShenhe()){
            int i = upfileService.upFileToFirst(id,isTop);
            if (i == 1) {
                return new RespBean("success", "资料设置/取消置顶操作成功!");
            }
            return new RespBean("error", "资料设置/取消置顶操作失败!");
        }else
            return RespBean.error("你的权限不足，请联系管理员修改权限");
    }

    @RequestMapping(value = "/getUpfileById", method = RequestMethod.GET)
    public RespBean getupFileById(Long id) {
        if(id != null){
            upFile upfile = upfileService.getupFileById(id);
            Map<String, Object> map = new HashMap<>();
            map.put("upfile", upfile);
            return RespBean.ok("获得相应id下的文件资料成功！",map);
        }
        return RespBean.error("获取对应类别的id有误，请检查重新输入!");
    }

    /**
     * 法律法规类别下前十下载量的文件
     * @return
     */
    @RequestMapping(value = "/lawStatistics",method = RequestMethod.GET)
    public RespBean lowDownload(){
        Map<String,Object> map=new HashMap<>();
        List<upFileBean> list=upfileService.getLawStatistics();
        if(list != null && list.size() > 0){
            map.put("list",list);
            return RespBean.ok("发送成功",map);
        }
        return RespBean.error("数据库中内容为空，没有获得相应数据");
    }

    /**
     * 学习资料类别下前十下载量的文件
     * @return
     */
    @RequestMapping(value = "/studyStatistics",method = RequestMethod.GET)
    public RespBean resourceDownload(){
        Map<String,Object> map=new HashMap<>();
        List<upFileBean> list=upfileService.getStudyStatistics();
        if(list != null && list.size() > 0){
            map.put("list",list);
            return RespBean.ok("发送成功",map);
        }
       return RespBean.error("数据库中内容为空，没有获得相应数据");
    }
}
