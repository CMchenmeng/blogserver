package org.sang.controller;

import org.sang.bean.RespBean;
import org.sang.bean.upFile;
import org.sang.service.upFileService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/upFile")
public class upFileController {

    @Autowired
    upFileService upfileService;

    //上传文件内容需包含除当前提交用户，上传时间外的所有upFile类型数据
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public RespBean addupFile(upFile upfile){
        int result = upfileService.addupFile(upfile);
        if(result==1)
            return new RespBean("success", "添加文件成功!");
        return new RespBean("error", "添加文件失败!");
    }

    @RequestMapping(value = "/{fid}", method = RequestMethod.GET)
    public upFile getupFileById(@PathVariable long fid) {
        return upfileService.getupFileById(fid);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Map<String,Object> getupFileByState(@RequestParam(value = "state",defaultValue = "1") Integer state,
                                               @RequestParam(value = "page",defaultValue = "1") Integer page,
                                               @RequestParam(value = "count", defaultValue = "6") Integer count,
                                               String keywords){
        int totalCount = upfileService.getupFileCountByState(state, Util.getCurrentUser().getId().intValue(),keywords);
        List<upFile> upfiles = upfileService.getupFileByState(state, page, count,keywords);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", totalCount);
        map.put("upfiles", upfiles);
        return map;
    }
}
