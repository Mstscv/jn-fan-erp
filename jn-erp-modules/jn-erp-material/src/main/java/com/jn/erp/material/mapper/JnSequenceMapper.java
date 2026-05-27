package com.jn.erp.material.mapper;

import com.jn.erp.material.domain.JnSequence;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface JnSequenceMapper extends BaseMapperPlus<JnSequence> {

    @Select("SELECT * FROM jn_sequence WHERE seq_key = #{seqKey} FOR UPDATE")
    JnSequence selectBySeqKeyForUpdate(@Param("seqKey") String seqKey);

    @Update("UPDATE jn_sequence SET current_value = current_value + 1, update_time = NOW() WHERE seq_key = #{seqKey}")
    int incrementValue(@Param("seqKey") String seqKey);
}
