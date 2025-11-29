package com.blog.alcoholblog.repository;

import com.blog.alcoholblog.model.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface WineRepository extends JpaRepository<Wine, UUID>, JpaSpecificationExecutor<Wine> {

    @Modifying
    @Query("DELETE FROM Wine w WHERE w.id = :id")
    int deleteByIdReturningCount(@Param("id") UUID id);

}
