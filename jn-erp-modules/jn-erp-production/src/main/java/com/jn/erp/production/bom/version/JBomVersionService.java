package com.jn.erp.production.bom.version;

import com.jn.erp.production.bom.domain.JnBomVersion;

import java.util.List;

public interface IJnBomVersionService {

    List<JnBomVersion> getVersionsByBomId(Long bomId);

    JnBomVersion getVersionById(Long versionId);

    JnBomVersion createVersion(Long bomId, String changeLog);

    void rollback(Long bomId, Long targetVersion);

    void deleteVersion(Long versionId);
}
