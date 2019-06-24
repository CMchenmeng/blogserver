package org.sang.controller.admin;

import org.sang.bean.RespBean;
import org.sang.bean.Role;
import org.sang.bean.User;
import org.sang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by sang on 2017/12/24.
 */
@RestController
@RequestMapping("/admin")
public class UserManaController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public RespBean getUserByNickname(String nickname) {
        List<User> user = userService.getUserByNickname(nickname);
        if(user!=null)
            return RespBean.ok("获取用户信息成功！",user);
        return RespBean.error("获取用户信息失败！");
    }

    @RequestMapping(value = "/userById", method = RequestMethod.GET)
    public RespBean getUserById( Long id) {
        User user =userService.getUserById(id);
        if(user!=null)
            return RespBean.ok("获取用户信息成功！",user);
        return RespBean.error("获取用户信息失败！");
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public RespBean getAllRole() {
        List<Role> roles = userService.getAllRole();
        if(roles!=null)
            return RespBean.ok("获取角色信息成功！",roles);
        return RespBean.error("获取角色信息失败！");
    }

    @RequestMapping(value = "/user/enabled", method = RequestMethod.PUT)
    public RespBean updateUserEnabled(Boolean enabled, Long uid) {
        if (userService.updateUserEnabled(enabled, uid) == 1) {
            return RespBean.ok("更新成功!");
        } else {
            return RespBean.error("更新失败!");
        }
    }

    //常用的操作时更新用户的密码，同时也可以更新用户的其他个人信息
    @RequestMapping(value = "/user/updatePassword",method = RequestMethod.POST)
    public RespBean updatePassword(User user){
        int result = userService.updateUserPassword(user);
        if(result == 1){
            return RespBean.ok("更改用户密码信息成功");
        } else if(result == 2){
            return RespBean.error("已不存在该用户，如需添加用户请重新注册！");
        } else if(result == 3){
            return RespBean.error("用户的密码不为空" );
        } else{
            return RespBean.error("更改用户密码信息失败");
        }
    }

    @RequestMapping(value = "/user/delete", method = RequestMethod.DELETE)
    public RespBean deleteUserById(Long uid) {
        if (userService.deleteUserById(uid) == 1) {
            return RespBean.ok( "删除成功!");
        } else {
            return RespBean.error("删除失败!");
        }
    }

    @RequestMapping(value = "/user/role", method = RequestMethod.PUT)
    public RespBean updateUserRoles(Long[] rids, Long id) {
        if (userService.updateUserRoles(rids, id) == rids.length) {
            return RespBean.ok( "更新成功!");
        } else {
            return RespBean.error("更新失败!");
        }
    }
}
