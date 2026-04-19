package com.flow.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flow.admin.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 查询用户的角色编码列表
     */
    @Select("SELECT r.role_code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0 AND r.status = 1")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的权限标识列表
     */
    @Select("SELECT m.permission FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.id = rm.menu_id " +
            "INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND m.deleted = 0 AND m.status = 1")
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询部门ID
     */
    @Select("SELECT dept_id FROM sys_user WHERE id = #{userId} AND deleted = 0")
    Long selectDeptIdByUserId(@Param("userId") Long userId);

}
