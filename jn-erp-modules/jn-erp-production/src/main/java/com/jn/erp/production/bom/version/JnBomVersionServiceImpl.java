package com.jn.erp.production.bom.version;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jn.erp.production.bom.domain.JnBom;
import com.jn.erp.production.bom.domain.JnBomLine;
import com.jn.erp.production.bom.domain.JnBomVersion;
import com.jn.erp.production.bom.mapper.JnBomLineMapper;
import com.jn.erp.production.bom.mapper.JnBomMapper;
import com.jn.erp.production.bom.mapper.JnBomVersionMapper;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JnBomVersionServiceImpl implements IJnBomVersionService {

    @Autowired
    private JnBomVersionMapper bomVersionMapper;

    @Autowired
    private JnBomMapper bomMapper;

    @Autowired
    private JnBomLineMapper bomLineMapper;

    @Override
    public List<JnBomVersion> getVersionsByBomId(Long bomId) {
        LambdaQueryWrapper<JnBomVersion> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JnBomVersion::getBomId, bomId);
        wrapper.orderByDesc(JnBomVersion::getCreatedTime);
        return bomVersionMapper.selectList(wrapper);
    }

    @Override
    public JnBomVersion getVersionById(Long versionId) {
        return bomVersionMapper.selectById(versionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JnBomVersion createVersion(Long bomId, String changeLog) {
        JnBom bom = bomMapper.selectById(bomId);
        if (bom == null) {
            throw new RuntimeException("BOM不存在: " + bomId);
        }

        List<JnBomLine> lines = bomLineMapper.selectByBomId(bomId);

        String snapshotJson = JSON.toJSONString(lines);

        String newVersionNo = incrementVersion(bom.getVersion());

        JnBomVersion version = new JnBomVersion();
        version.setBomId(bomId);
        version.setVersion(newVersionNo);
        version.setBomSnapshot(snapshotJson);
        version.setChangeLog(changeLog);
        version.setCreatedBy(SecurityUtils.getUsername());
        version.setCreatedTime(LocalDateTime.now());
        bomVersionMapper.insert(version);

        bom.setVersion(newVersionNo);
        bomMapper.updateById(bom);

        return version;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(Long bomId, Long targetVersion) {
        JnBom bom = bomMapper.selectById(bomId);
        if (bom == null) {
            throw new RuntimeException("BOM不存在: " + bomId);
        }

        JnBomVersion version = bomVersionMapper.selectById(targetVersion);
        if (version == null) {
            throw new RuntimeException("版本记录不存在: " + targetVersion);
        }
        if (!bomId.equals(version.getBomId())) {
            throw new RuntimeException("版本记录不属于当前BOM");
        }

        JSONArray snapshotArray = JSON.parseArray(version.getBomSnapshot());
        List<JnBomLine> snapshotLines = snapshotArray.toList(JnBomLine.class);

        bomLineMapper.deleteByBomId(bomId);

        for (JnBomLine line : snapshotLines) {
            line.setLineId(null);
            line.setBomId(bomId);
            bomLineMapper.insert(line);
        }

        bom.setVersion(version.getVersion());
        bomMapper.updateById(bom);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVersion(Long versionId) {
        bomVersionMapper.deleteById(versionId);
    }

    private String incrementVersion(String currentVersion) {
        if (currentVersion == null || currentVersion.isEmpty()) {
            return "v1.0";
        }
        String versionStr = currentVersion.replace("v", "").replace("V", "");
        String[] parts = versionStr.split("\\.");
        if (parts.length == 2) {
            try {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                minor++;
                if (minor >= 10) {
                    major++;
                    minor = 0;
                }
                return "v" + major + "." + minor;
            } catch (NumberFormatException e) {
                return "v1.0";
            }
        }
        return "v1.0";
    }
}
