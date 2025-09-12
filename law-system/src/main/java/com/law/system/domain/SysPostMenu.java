package com.law.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 岗位和菜单关联 sys_post_menu
 * 
 * @author law
 */
public class SysPostMenu
{
    /** 岗位ID */
    private Long postId;
    
    /** 菜单ID */
    private Long menuId;

    public Long getPostId()
    {
        return postId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    public Long getMenuId()
    {
        return menuId;
    }

    public void setMenuId(Long menuId)
    {
        this.menuId = menuId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("postId", getPostId())
            .append("menuId", getMenuId())
            .toString();
    }
}
