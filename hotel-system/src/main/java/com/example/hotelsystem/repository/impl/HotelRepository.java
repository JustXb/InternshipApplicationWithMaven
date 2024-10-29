package com.example.hotelsystem.repository.impl;

import com.example.hotelsystem.repository.entity.HotelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HotelRepository extends MongoRepository<HotelEntity, Integer> {
}

