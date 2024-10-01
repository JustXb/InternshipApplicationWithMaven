package com.example.intershipapplicationwithmaven.repository.impl;

import com.example.intershipapplicationwithmaven.repository.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
}