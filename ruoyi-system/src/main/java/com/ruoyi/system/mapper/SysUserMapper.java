package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapperPlus<SysUser> {

    SysUser selectUserByUserName(@Param("userName") String userName);

    List<SysUser> selectAllocatedList(@Param("roleId") Long roleId, @Param("userName") String userName, @Param("phone") String phone);

    List<SysUser> selectUnallocatedList(@Param("roleId") Long roleId, @Param("userName") String userName, @Param("phone") String phone);
}
