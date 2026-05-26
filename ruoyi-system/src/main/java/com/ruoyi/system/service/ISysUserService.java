package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.SysUser;

import java.util.List;

public interface ISysUserService extends IService<SysUser> {

    SysUser selectUserByUserName(String userName);

    List<SysUser> selectUserList(SysUser user);

    List<SysUser> selectAllocatedList(Long roleId, String userName, String phone);

    List<SysUser> selectUnallocatedList(Long roleId, String userName, String phone);

    boolean checkUserNameUnique(String userName);

    boolean checkPhoneUnique(SysUser user);

    boolean checkEmailUnique(SysUser user);

    int insertUser(SysUser user);

    int updateUser(SysUser user);

    int deleteUserByIds(Long[] userIds);

    void insertUserAuth(Long userId, Long[] roleIds);

    String importUser(List<SysUser> userList, boolean updateSupport, String operName);

    void resetPwd(SysUser user);

    void updateUserStatus(SysUser user);
}
