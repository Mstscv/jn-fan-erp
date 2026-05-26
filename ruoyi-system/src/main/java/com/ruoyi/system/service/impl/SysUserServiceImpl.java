package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Override
    public SysUser selectUserByUserName(String userName) {
        return baseMapper.selectUserByUserName(userName);
    }

    @Override
    public List<SysUser> selectUserList(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDelFlag, "0");
        wrapper.orderByAsc(SysUser::getUserId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SysUser> selectAllocatedList(Long roleId, String userName, String phone) {
        return baseMapper.selectAllocatedList(roleId, userName, phone);
    }

    @Override
    public List<SysUser> selectUnallocatedList(Long roleId, String userName, String phone) {
        return baseMapper.selectUnallocatedList(roleId, userName, phone);
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, userName);
        wrapper.eq(SysUser::getDelFlag, "0");
        wrapper.last("limit 1");
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, user.getPhone());
        wrapper.eq(SysUser::getDelFlag, "0");
        if (user.getUserId() != null) {
            wrapper.ne(SysUser::getUserId, user.getUserId());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, user.getEmail());
        wrapper.eq(SysUser::getDelFlag, "0");
        if (user.getUserId() != null) {
            wrapper.ne(SysUser::getUserId, user.getUserId());
        }
        return baseMapper.selectCount(wrapper) == 0;
    }

    @Override
    @Transactional
    public int insertUser(SysUser user) {
        return baseMapper.insert(user);
    }

    @Override
    @Transactional
    public int updateUser(SysUser user) {
        return baseMapper.updateById(user);
    }

    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        int count = 0;
        for (Long userId : userIds) {
            SysUser user = new SysUser();
            user.setUserId(userId);
            user.setDelFlag("1");
            count += baseMapper.updateById(user);
        }
        return count;
    }

    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds) {
        SysUser user = baseMapper.selectById(userId);
        if (user != null && roleIds != null) {
            user.setRoleIds(roleIds);
        }
    }

    @Override
    public String importUser(List<SysUser> userList, boolean updateSupport, String operName) {
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (SysUser user : userList) {
            try {
                SysUser existUser = selectUserByUserName(user.getUserName());
                if (existUser != null) {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName()).append(" 已存在");
                } else {
                    insertUser(user);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 导入成功");
                }
            } catch (Exception e) {
                failureNum++;
                failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(user.getUserName()).append(" 导入失败：").append(e.getMessage());
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条。");
        }
        return successMsg.toString();
    }

    @Override
    @Transactional
    public void resetPwd(SysUser user) {
        baseMapper.updateById(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(SysUser user) {
        baseMapper.updateById(user);
    }
}
