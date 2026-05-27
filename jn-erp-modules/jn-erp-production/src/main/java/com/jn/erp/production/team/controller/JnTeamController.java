package com.jn.erp.production.team.controller;

import com.jn.erp.production.team.domain.JnTeam;
import com.jn.erp.production.team.service.IJnTeamService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erp/team")
public class JnTeamController extends BaseController {

    @Autowired
    private IJnTeamService teamService;

    @RequiresPermissions("erp:team:list")
    @GetMapping("/list")
    public TableDataInfo list(JnTeam team) {
        startPage();
        List<JnTeam> list = teamService.selectList(team);
        return getDataTable(list);
    }

    @RequiresPermissions("erp:team:query")
    @GetMapping("/{teamId}")
    public AjaxResult getInfo(@PathVariable Long teamId) {
        return success(teamService.selectById(teamId));
    }

    @RequiresPermissions("erp:team:add")
    @Log(title = "班组管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JnTeam team) {
        return toAjax(teamService.insert(team));
    }

    @RequiresPermissions("erp:team:edit")
    @Log(title = "班组管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JnTeam team) {
        return toAjax(teamService.update(team));
    }

    @RequiresPermissions("erp:team:remove")
    @Log(title = "班组管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{teamIds}")
    public AjaxResult remove(@PathVariable Long[] teamIds) {
        return toAjax(teamService.deleteByIds(teamIds));
    }
}
