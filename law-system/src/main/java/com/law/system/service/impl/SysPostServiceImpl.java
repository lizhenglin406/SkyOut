package com.law.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.law.common.constant.UserConstants;
import com.law.common.exception.ServiceException;
import com.law.common.utils.StringUtils;
import com.law.system.domain.SysPost;
import com.law.system.domain.SysPostMenu;
import com.law.system.mapper.SysPostMapper;
import com.law.system.mapper.SysPostMenuMapper;
import com.law.system.mapper.SysUserPostMapper;
import com.law.system.service.ISysPostService;

/**
 * 岗位信息 服务层处理
 * 
 * @author law
 */
@Service
public class SysPostServiceImpl implements ISysPostService
{
    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private SysPostMenuMapper postMenuMapper;

    /**
     * 查询岗位信息集合
     * 
     * @param post 岗位信息
     * @return 岗位信息集合
     */
    @Override
    public List<SysPost> selectPostList(SysPost post)
    {
        return postMapper.selectPostList(post);
    }

    /**
     * 查询所有岗位
     * 
     * @return 岗位列表
     */
    @Override
    public List<SysPost> selectPostAll()
    {
        return postMapper.selectPostAll();
    }

    /**
     * 通过岗位ID查询岗位信息
     * 
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    @Override
    public SysPost selectPostById(Long postId)
    {
        return postMapper.selectPostById(postId);
    }

    /**
     * 根据用户ID获取岗位选择框列表
     * 
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    @Override
    public List<Long> selectPostListByUserId(Long userId)
    {
        return postMapper.selectPostListByUserId(userId);
    }

    /**
     * 校验岗位名称是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostNameUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验岗位编码是否唯一
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public boolean checkPostCodeUnique(SysPost post)
    {
        Long postId = StringUtils.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (StringUtils.isNotNull(info) && info.getPostId().longValue() != postId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过岗位ID查询岗位使用数量
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int countUserPostById(Long postId)
    {
        return userPostMapper.countUserPostById(postId);
    }

    /**
     * 删除岗位信息
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    @Override
    public int deletePostById(Long postId)
    {
        return postMapper.deletePostById(postId);
    }

    /**
     * 批量删除岗位信息
     * 
     * @param postIds 需要删除的岗位ID
     * @return 结果
     */
    @Override
    public int deletePostByIds(Long[] postIds)
    {
        for (Long postId : postIds)
        {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", post.getPostName()));
            }
        }
        return postMapper.deletePostByIds(postIds);
    }

    /**
     * 新增保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int insertPost(SysPost post)
    {
        return postMapper.insertPost(post);
    }

    /**
     * 修改保存岗位信息
     * 
     * @param post 岗位信息
     * @return 结果
     */
    @Override
    public int updatePost(SysPost post)
    {
        return postMapper.updatePost(post);
    }

    /**
     * 根据用户ID查询岗位权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId)
    {
        System.out.println("=== 岗位权限获取调试 ===");
        System.out.println("用户ID: " + userId);
        
        List<String> perms = postMenuMapper.selectMenuPermsByUserId(userId);
        System.out.println("从数据库获取的权限数量: " + perms.size());
        
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                System.out.println("处理权限字符串: " + perm);
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        
        System.out.println("最终岗位权限数量: " + permsSet.size());
        System.out.println("岗位权限列表: " + permsSet);
        System.out.println("=== 岗位权限获取调试结束 ===");
        
        return permsSet;
    }

    /**
     * 根据岗位ID查询菜单权限
     * 
     * @param postId 岗位ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByPostId(Long postId)
    {
        List<String> perms = postMenuMapper.selectMenuPermsByPostId(postId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据岗位ID查询菜单树信息
     * 
     * @param postId 岗位ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByPostId(Long postId)
    {
        SysPost post = postMapper.selectPostById(postId);
        return postMenuMapper.selectMenuListByPostId(postId, post.isMenuCheckStrictly());
    }

    /**
     * 分配岗位菜单权限
     * 
     * @param postId 岗位ID
     * @param menuIds 菜单ID列表
     * @return 结果
     */
    @Override
    public int insertPostMenu(Long postId, Long[] menuIds)
    {
        int rows = 1;
        // 删除岗位与菜单关联
        postMenuMapper.deletePostMenuByPostId(postId);
        if (StringUtils.isNotNull(menuIds) && menuIds.length > 0)
        {
            // 新增岗位与菜单关联
            List<SysPostMenu> list = new ArrayList<SysPostMenu>();
            for (Long menuId : menuIds)
            {
                SysPostMenu pm = new SysPostMenu();
                pm.setPostId(postId);
                pm.setMenuId(menuId);
                list.add(pm);
            }
            if (list.size() > 0)
            {
                rows = postMenuMapper.batchPostMenu(list);
            }
        }
        return rows;
    }
}
