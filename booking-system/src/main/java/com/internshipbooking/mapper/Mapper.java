package com.internshipbooking.mapper;

import com.internshipbooking.repository.entity.GuestEntity;
import com.internshipbooking.transport.dto.request.GuestDTO;

import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public GuestDTO toDto(GuestEntity guestEntity){
        GuestDTO dto = new GuestDTO();
        dto.setId(guestEntity.getId());
        dto.setName(guestEntity.getName());
        dto.setAge(guestEntity.getAge());
        dto.setAddress(guestEntity.getAddress());
        dto.setPassportNumber(guestEntity.getPassportNumber());
        return dto;
    }

    public GuestEntity toEntity(GuestDTO dto){
        GuestEntity guestEntity = new GuestEntity();
        guestEntity.setName(dto.getName());
        guestEntity.setAge(dto.getAge());
        guestEntity.setAddress(dto.getAddress());
        guestEntity.setPassportNumber(dto.getPassportNumber());
        return guestEntity;
    }
}
