package com.example.hotelsystem.repository.impl;

import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface HotelAvailabilityRepository extends MongoRepository<HotelAvailablilityEntity, Integer> {
}
