package com.law.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.law.system.domain.SysPostMenu;

/**
 * 岗位和菜单关联 数据层
 * 
 * @author law
 */
public interface SysPostMenuMapper
{
    /**
     * 查询岗位使用数量
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    public int checkPostExistMenu(Long menuId);

    /**
     * 通过岗位ID删除岗位和菜单关联
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    public int deletePostMenuByPostId(Long postId);

    /**
     * 通过菜单ID删除岗位和菜单关联
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    public int deletePostMenuByMenuId(Long menuId);

    /**
     * 批量删除岗位和菜单关联
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePostMenu(Long[] ids);

    /**
     * 批量新增岗位菜单信息
     * 
     * @param postMenuList 岗位菜单列表
     * @return 结果
     */
    public int batchPostMenu(List<SysPostMenu> postMenuList);

    /**
     * 根据岗位ID查询菜单权限
     * 
     * @param postId 岗位ID
     * @return 权限列表
     */
    public List<String> selectMenuPermsByPostId(Long postId);

    /**
     * 根据岗位ID查询菜单树信息
     * 
     * @param postId 岗位ID
     * @param menuCheckStrictly 菜单树选择项是否关联显示
     * @return 选中菜单列表
     */
    public List<Long> selectMenuListByPostId(@Param("postId") Long postId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    /**
     * 根据用户ID查询岗位权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<String> selectMenuPermsByUserId(Long userId);
}
