package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.LoginUser;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.R;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private ISysUserService userService;

    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @GetMapping("/{userId}")
    public R<SysUser> getInfo(@PathVariable Long userId) {
        return success(userService.getById(userId));
    }

    @GetMapping("/userInfo/{username}")
    public R<LoginUser> getUserInfo(@PathVariable String username) {
        SysUser sysUser = userService.selectUserByUserName(username);
        if (sysUser == null) {
            return R.fail("用户不存在");
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(sysUser.getUserId());
        loginUser.setDeptId(sysUser.getDeptId());
        loginUser.setUsername(sysUser.getUserName());
        loginUser.setNickName(sysUser.getNickName());
        loginUser.setPassword(sysUser.getPassword());
        loginUser.setEmail(sysUser.getEmail());
        loginUser.setPhone(sysUser.getPhone());
        loginUser.setSex(sysUser.getSex());
        loginUser.setAvatar(sysUser.getAvatar());
        loginUser.setStatus(sysUser.getStatus());
        if (sysUser.getDept() != null) {
            loginUser.setDeptName(sysUser.getDept().getDeptName());
        }
        return R.ok(loginUser);
    }

    @GetMapping("/checkToken")
    public R<Boolean> checkToken(@RequestParam String token) {
        return R.ok(true);
    }

    @PostMapping
    public R<Void> add(@RequestBody SysUser user) {
        if (!userService.checkUserNameUnique(user.getUserName())) {
            return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        if (user.getPhone() != null && !user.getPhone().isEmpty() && !userService.checkPhoneUnique(user)) {
            return error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !userService.checkEmailUnique(user)) {
            return error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    @PutMapping
    public R<Void> edit(@RequestBody SysUser user) {
        if (!userService.checkPhoneUnique(user)) {
            return error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (!userService.checkEmailUnique(user)) {
            return error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        return toAjax(userService.updateUser(user));
    }

    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUser user) {
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        userService.resetPwd(user);
        return success();
    }

    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUser user) {
        userService.updateUserStatus(user);
        return success();
    }

    @GetMapping("/authRole/{userId}")
    public R<SysUser> authRole(@PathVariable Long userId) {
        SysUser user = userService.getById(userId);
        return success(user);
    }

    @PutMapping("/authRole")
    public R<Void> insertAuthRole(Long userId, Long[] roleIds) {
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

    private R<Void> toAjax(int rows) {
        return rows > 0 ? success() : error();
    }
}
