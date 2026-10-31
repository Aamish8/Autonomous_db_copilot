package com.aamish.db_copilot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aamish.db_copilot.entity.DbConnection;

@Repository
public interface DbConnectionRepository extends JpaRepository<DbConnection,Long>{

}
