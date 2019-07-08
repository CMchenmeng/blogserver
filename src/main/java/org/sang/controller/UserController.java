package org.sang.controller;

import org.sang.bean.RespBean;
import org.sang.bean.UserCount;
import org.sang.service.UserService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Timestamp;
import java.util.*;

/**
 * Created by sang on 2017/12/24.
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/currentUserName")
    public String currentUserName() {
        return Util.getCurrentUser().getNickname();
    }

    @RequestMapping("/currentUserId")
    public Long currentUserId() {
        return Util.getCurrentUser().getId();
    }

    @RequestMapping("/currentUserEmail")
    public String currentUserEmail() {
        return Util.getCurrentUser().getEmail();
    }

    @RequestMapping("/isAdmin")
    public Boolean isAdmin() {
        List<GrantedAuthority> authorities = Util.getCurrentUser().getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().contains("超级管理员")) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = "/updateUserEmail",method = RequestMethod.PUT)
    public RespBean updateUserEmail(String email) {
        if (userService.updateUserEmail(email) == 1) {
            return new RespBean("success", "开启成功!");
        }
        return new RespBean("error", "开启失败!");
    }

    @RequestMapping(value ="/loginCount",method = RequestMethod.GET)
    public RespBean getLoginCount(){
        Map<String,Object> map=new HashMap<>();
        List<String>date=new ArrayList<>();
        List<Long> count=new ArrayList<>();
        List<UserCount> list=userService.getLoginCount();
        for(UserCount uc:list){
            System.out.println(uc.getDate());
            count.add(uc.getTotal());
            date.add(uc.getDate());
        }
        map.put("count",count);
        map.put("date",date);

        return RespBean.ok("获得日期和数量",map);
    }



}
