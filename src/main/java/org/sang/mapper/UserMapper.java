package org.sang.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.sang.bean.Role;
import org.sang.bean.User;
import org.sang.bean.UserCount;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by sang on 2017/12/17.
 */
@Mapper
public interface UserMapper {

    User loadUserByUsername(@Param("username") String username);

    long reg(User user);

    int updateUserPassword(User user);

    int updateUserEmail(@Param("email") String email, @Param("id") Long id);

    List<User> getUserByNickname(@Param("nickname") String nickname);

    List<Role> getAllRole();

    int updateUserEnabled(@Param("enabled") Boolean enabled, @Param("uid") Long uid);

    int deleteUserById(Long uid);

    int deleteUserRolesByUid(Long id);

    int setUserRoles(@Param("rids") Long[] rids, @Param("id") Long id);

    User getUserById(@Param("id") Long id);

    int getUserCountByRole(@Param("rids") List rids,@Param("keywords") String keywords);

    List<User> getUserByRole(@Param("rids") List rids,@Param("start") Integer start, @Param("count") Integer count,@Param("keywords") String keywords);

    void insertUserCount(@Param("date") String date, @Param("total") Long total);

    UserCount getUserCountByDate(@Param("date") String date);

    void updateUserCountByDate(@Param("date") String date);

    List<UserCount> getLoginCount();
}
