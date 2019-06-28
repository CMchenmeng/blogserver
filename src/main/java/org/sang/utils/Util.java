package org.sang.utils;

import org.sang.bean.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Created by sang on 2017/12/20.
 */
public class Util {

    //Authentication是一个接口，用来表示用户认证信息的，在用户登录认证之前相关信息会封装为一个Authentication具体实现类的对象，
    // 在登录认证成功之后又会生成一个信息更全面，包含用户权限等信息的Authentication对象，然后把它保存在SecurityContextHolder所持有的SecurityContext中，
    // 供后续的程序进行调用，如访问权限的鉴定等。
    public static User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }

    public static Boolean isShenhe(){
        List<GrantedAuthority> authorities = Util.getCurrentUser().getAuthorities();
        for (GrantedAuthority authority : authorities) {  //判断角色是否包含超级管理员，是为了避免审核员的操作超级管理员不能操作
            if (authority.getAuthority().contains("审核人员")||authority.getAuthority().contains("超级管理员")) {
                return true;
            }
        }
        return false;
    }
}
