package com.example.demo.Dao;

import com.example.demo.Entity.BugInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugInfoDao extends JpaRepository<BugInfo, Long> {
    @Query(value = "select b form BugInfo b where b.SourceFile = :filename")
    public List<BugInfo> findbugsByFileName(@Param("filename") String filename);

    @Query(value = "select b from BugInfo b where b.SourceFile = :filename and b.ToolName = :toolname")
    public List<BugInfo> findbugsByFileNameAndToolName(@Param("filename") String filename , @Param("toolname") String toolname);
}
