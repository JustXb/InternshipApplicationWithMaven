package com.internshipbooking.repository.impl;


import com.internshipbooking.repository.entity.GuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<GuestEntity, Integer> {
    boolean existsByPassportNumberAndIdNot(String passportNumber, int id);
    List<GuestEntity> findAllByOrderByIdAsc();
}

