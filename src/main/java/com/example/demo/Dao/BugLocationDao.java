package com.example.demo.Dao;

import com.example.demo.Entity.FileBugLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugLocationDao extends JpaRepository<FileBugLocation , Long> {
}
