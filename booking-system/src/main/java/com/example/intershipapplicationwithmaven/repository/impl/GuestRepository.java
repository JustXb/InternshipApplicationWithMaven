package com.example.intershipapplicationwithmaven.repository.impl;


import com.example.intershipapplicationwithmaven.repository.entity.GuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<GuestEntity, Integer> {
}

