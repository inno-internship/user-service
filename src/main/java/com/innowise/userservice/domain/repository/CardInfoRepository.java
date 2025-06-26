package com.innowise.userservice.domain.repository;

import com.innowise.userservice.domain.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardInfoRepository extends JpaRepository<CardInfo, UUID> {

    @Query("SELECT c FROM CardInfo c WHERE c.id = :id")
    Optional<CardInfo> findByIdJPQL(@Param("id") UUID id);

    @Query(value = "SELECT * FROM card_info WHERE id IN (:ids)", nativeQuery = true)
    List<CardInfo> findAllByIdsNative(@Param("ids") List<UUID> ids);
}
