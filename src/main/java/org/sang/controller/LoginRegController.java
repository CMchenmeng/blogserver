package org.sang.controller;

import org.sang.bean.RespBean;
import org.sang.bean.User;
import org.sang.service.UserService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by sang on 2017/12/17.
 */
@RestController
public class LoginRegController {

    @Autowired
    UserService userService;

    @RequestMapping("/login_error")
    public RespBean loginError() {
        return RespBean.loginError( "登录失败!");
    }

    @RequestMapping("/login_success")
    public RespBean loginSuccess() {
        List<GrantedAuthority> authorities = Util.getCurrentUser().getAuthorities();
        String role ="";
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().contains("超级管理员")) {
                role = "admin";
            }
        }
        return RespBean.ok( "登录成功!",role);
    }

    /**
     * 如果自动跳转到这个页面，说明用户未登录，返回相应的提示即可
     * <p>
     * 如果要支持表单登录，可以在这个方法中判断请求的类型，进而决定返回JSON还是HTML页面
     *
     * @return
     */
    @RequestMapping("/login_page")
    public RespBean loginPage() {
        return RespBean.loginError( "尚未登录，请登录!");
    }

    @RequestMapping(value = "/reg",method = RequestMethod.POST)
    public RespBean reg(User user) {
        int result = userService.reg(user);
        if (result == 0) {
            //成功
            return  RespBean.ok("success", "注册成功!");
        } else if (result == 1) {
            return RespBean.error("error", "用户名重复，注册失败!");
        } else {
            //失败
            return RespBean.error("error", "注册失败!");
        }
    }


}
