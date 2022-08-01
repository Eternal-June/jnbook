package com.jn.admin.shiro;

import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.jn.core.util.bcrypt.BCryptPasswordEncoder;
import com.jn.db.domain.JnAdmin;
import com.jn.db.service.JnAdminService;
import com.jn.db.service.JnPermissionService;
import com.jn.db.service.JnRoleService;

/**
 * 授权相关服务-shiro
 *
 * @author qiguliuxing
 * @since 1.0.0
 */
public class AdminAuthorizingRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(AdminAuthorizingRealm.class);
    @Autowired
    /*
     * 自动按照类型注入。当使用注解注入属性时，set方法可以省略。
     * 它只能注入其他 bean 类型。当有多个类型匹配时，
     * 使用要注入的对象变量名称作为 bean 的 id，在 spring 容器查找，
     * 找到了也可以注入成功。找不到就报错。
     * */
    private JnAdminService adminService;
    @Autowired
    private JnRoleService roleService;
    @Autowired
    private JnPermissionService permissionService;

    @Override
    /*
    * @Override的注解的话，表示子类重写了父类的方法。
    * */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        JnAdmin admin = (JnAdmin) getAvailablePrincipal(principals);
        Integer[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.queryByIds(roleIds);
        Set<String> permissions = permissionService.queryByRoleIds(roleIds);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        if (StringUtils.isEmpty(username)) {
            throw new AccountException("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            throw new AccountException("密码不能为空");
        }

        List<JnAdmin> adminList = adminService.findAdmin(username);
        Assert.state(adminList.size() < 2, "同一个用户名存在两个账户");
        if (adminList.size() == 0) {
            logger.error("找不到用户（" + username + "）的帐号信息");
            throw new UnknownAccountException("找不到用户（" + username + "）的帐号信息");
        }
        JnAdmin admin = adminList.get(0);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, admin.getPassword())) {
            logger.error("找不到用户（" + username + "）的帐号信息");
            throw new UnknownAccountException("找不到用户（" + username + "）的帐号信息");
        }

        return new SimpleAuthenticationInfo(admin, password, getName());
    }

}
