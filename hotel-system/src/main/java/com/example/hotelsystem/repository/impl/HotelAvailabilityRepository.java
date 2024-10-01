package com.example.hotelsystem.repository.impl;

import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HotelAvailabilityRepository extends MongoRepository<HotelAvailablilityEntity, Integer> {
}
