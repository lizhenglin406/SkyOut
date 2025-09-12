package com.law.framework.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.law.common.constant.UserConstants;
import com.law.common.core.domain.entity.SysRole;
import com.law.common.core.domain.entity.SysUser;
import com.law.common.utils.StringUtils;
import com.law.system.service.ISysMenuService;
import com.law.system.service.ISysRoleService;
import com.law.system.service.ISysPostService;

/**
 * 用户权限处理
 * 
 * @author law
 */
@Component
public class SysPermissionService
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysPostService postService;

    /**
     * 获取角色数据权限
     * 
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            roles.add("admin");
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     * 
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        Set<String> perms = new HashSet<String>();
        System.out.println("=== 权限获取调试信息 ===");
        System.out.println("用户ID: " + user.getUserId());
        System.out.println("用户名: " + user.getUserName());
        System.out.println("是否管理员: " + user.isAdmin());
        
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            perms.add("*:*:*");
            System.out.println("管理员用户，赋予所有权限");
        }
        else
        {
            // 1. 获取角色权限
            List<SysRole> roles = user.getRoles();
            System.out.println("用户角色数量: " + (roles != null ? roles.size() : 0));
            if (!CollectionUtils.isEmpty(roles))
            {
                // 多角色设置permissions属性，以便数据权限匹配权限
                for (SysRole role : roles)
                {
                    System.out.println("处理角色: " + role.getRoleName() + " (ID: " + role.getRoleId() + ")");
                    if (StringUtils.equals(role.getStatus(), UserConstants.ROLE_NORMAL) && !role.isAdmin())
                    {
                        Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId());
                        System.out.println("角色权限数量: " + rolePerms.size());
                        role.setPermissions(rolePerms);
                        perms.addAll(rolePerms);
                    }
                }
            }
            
            // 2. 获取岗位权限（与角色权限并集）
            System.out.println("开始获取岗位权限...");
            Set<String> postPerms = postService.selectMenuPermsByUserId(user.getUserId());
            System.out.println("岗位权限数量: " + postPerms.size());
            if (!postPerms.isEmpty()) {
                System.out.println("岗位权限示例: " + postPerms.stream().limit(5).toArray());
            }
            perms.addAll(postPerms);
            
            System.out.println("最终权限总数: " + perms.size());
            System.out.println("最终权限示例: " + perms.stream().limit(10).toArray());
        }
        System.out.println("=== 权限获取调试信息结束 ===");
        return perms;
    }
}
