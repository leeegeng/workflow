package com.flow.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flow.admin.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询所有菜单树
     */
    @Select("SELECT * FROM sys_menu WHERE deleted = 0 AND status = 1 ORDER BY sort_order")
    List<SysMenu> selectMenuTree();

    /**
     * 根据用户ID查询菜单列表
     */
    @Select("SELECT DISTINCT m.* FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.deleted = 0 AND m.status = 1 " +
            "ORDER BY m.sort_order")
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据父菜单ID查询子菜单列表
     */
    @Select("SELECT * FROM sys_menu WHERE parent_id = #{parentId} AND deleted = 0")
    List<SysMenu> selectByParentId(@Param("parentId") Long parentId);

}
