package org.sang.controller.admin;

import org.sang.bean.RespBean;
import org.sang.bean.Role;
import org.sang.bean.User;
import org.sang.service.UserService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sang on 2017/12/24.
 */
@RestController
@RequestMapping("/admin")
public class UserManaController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/getUserByNickname", method = RequestMethod.GET)
    public RespBean getUserByNickname(String nickname) {
        List<User> user = userService.getUserByNickname(nickname);
        if(user!=null)
            return RespBean.ok("获取用户信息成功！",user);
        return RespBean.error("获取用户信息失败！");
    }

    @RequestMapping(value = "/getUserById", method = RequestMethod.GET)
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

    @RequestMapping(value = "/user/enabled", method = RequestMethod.GET)
    public RespBean updateUserEnabled(Boolean enabled, Long uid) {
        if (userService.updateUserEnabled(enabled, uid) == 1) {
            return RespBean.ok("启用/禁用用户成功!");
        } else {
            return RespBean.error("启用/禁用用户失败!");
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

    @RequestMapping(value = "/user/updateRole", method = RequestMethod.GET)
    public RespBean updateUserRoles(Long[] rids, Long id) {
        if (userService.updateUserRoles(rids, id) == rids.length) {
            return RespBean.ok( "更新用户角色成功!");
        } else {
            return RespBean.error("更新用户角色失败!");
        }
    }

    //获取所有角色的用户
    @RequestMapping(value = "/getUserByRole", method = RequestMethod.GET)
    public RespBean getUserByRole(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                  @RequestParam(value = "count", defaultValue = "6") Integer count,
                                  String keywords,String role){
        if(role.contains("全部用户")){
            List<Long> rids = null;
            int totalCount = userService.getUserCountByRole(rids, keywords);
            List<User> users = userService.getUserByRole(rids, page, count, keywords);
            Map<String, Object> map = new HashMap<>();
            map.put("totalCount", totalCount);
            map.put("users", users);
            return RespBean.ok("获取全部用户成功", map);
        }
        else if(role.contains("审核人员") ){
            List<Long> rids = new ArrayList<Long>();
            rids.add(new Long(2));
            int totalCount = userService.getUserCountByRole(rids,keywords);
            List<User> users = userService.getUserByRole(rids, page, count,keywords);
            Map<String, Object> map = new HashMap<>();
            map.put("totalCount", totalCount);
            map.put("users", users);
            return RespBean.ok("获取审核人员角色的用户成功",map);
        }else if(role.contains("普通用户")){
            List<Long> rids = new ArrayList<Long>();//默认为空
            rids.add(new Long(3));
            rids.add(new Long(4));
            rids.add(new Long(5));
            int totalCount = userService.getUserCountByRole(rids,keywords);
            System.out.println("totalCount:   "+ totalCount);
            List<User> users = userService.getUserByRole(rids, page, count,keywords);
            Map<String, Object> map = new HashMap<>();
            map.put("totalCount", totalCount);
            map.put("users", users);
            return RespBean.ok("获取普通用户角色的用户成功",map);
        }else
            return RespBean.error("传入参数有误，没有相应角色的用户，请检查后重试");
    }
}
