package com.example.hotelsystem.service;

import com.example.hotelsystem.repository.impl.HotelAvailabilityRepository;
import com.example.hotelsystem.repository.impl.HotelRepository;

import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import com.example.hotelsystem.repository.entity.HotelEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final HotelAvailabilityRepository hotelAvailabilityRepository;
    private final int port;

    private final Logger LOGGER = Logger.getLogger(HotelService.class.getName());

    @Autowired
    public HotelService(HotelAvailabilityRepository hotelAvailabilityRepository, HotelRepository hotelRepository,
                         @Value("${processor.port}") int port) {
        this.hotelAvailabilityRepository = hotelAvailabilityRepository;
        this.hotelRepository = hotelRepository;
        this.port = port;
    }

    public void responseHotels() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            List<HotelEntity> hotels = hotelRepository.findAll();
            String result = getString(hotels);

            LOGGER.info(ServiceMessages.WAITING_CONNECT.getMessage());

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    out.println(result);

                    int hotelId = Integer.parseInt(in.readLine());


                    Optional<HotelAvailablilityEntity> hotel = Optional.empty();
                    HotelAvailablilityEntity hotelAvailability = null;
                    if(hotelAvailabilityRepository.existsById(hotelId)){
                        hotel = hotelAvailabilityRepository.findById(hotelId);
                        hotelAvailability = hotel.get();
                    }
                    else{
                        out.println(ServiceMessages.UNAVAILABLE.getMessage());
                    }
                    LOGGER.info(ServiceMessages.REQUEST_HOTEL_AVAILABILITY.getMessage() + hotelId);

                    if (isHotelAvailable(hotelId)) {
                        out.println(ServiceMessages.AVAILABLE.getMessage());
                    } else {
                        if (hotel == null) {
                            out.println(ServiceMessages.UNAVAILABLE.getMessage());
                        } else
                            if (!hotelAvailability.decreaseAvailableRooms()) {
                                out.println(ServiceMessages.LACK_OF_PLACES.getMessage());
                            } else {
                                hotelAvailabilityRepository.save(hotelAvailability);
                                out.println(ServiceMessages.AVAILABLE.getMessage());
                            }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getString(List<HotelEntity> hotels) {
        StringBuilder sb = new StringBuilder();
        for (HotelEntity hotelString : hotels) {
            sb.append(hotelString.toString());
            sb.append('\t');
        }
        return sb.toString();
    }




    public boolean isHotelAvailable(int id) {
        Optional<HotelEntity> hotelOpt = hotelRepository.findById(id);

        if (hotelOpt.isEmpty()) {
            LOGGER.warning(ServiceMessages.WRONG_HOTEL.getMessage());
            return true;
        }

        Optional<HotelAvailablilityEntity> hotelAvailabilityOpt = hotelAvailabilityRepository.findById(id);

        if (hotelAvailabilityOpt.isEmpty()) {
            LOGGER.warning(ServiceMessages.UNAVAILABLE.getMessage());
            return true;
        }

        return false;
    }

    public String checkHotelAvailability(int hotelId) {
        Optional<HotelAvailablilityEntity> hotelAvailability = hotelAvailabilityRepository.findById(hotelId);
        if (hotelAvailability.isEmpty()) {
            return ServiceMessages.UNAVAILABLE.getMessage();
        }

        HotelAvailablilityEntity availability = hotelAvailability.get();
        if (!availability.decreaseAvailableRooms()) {
            return ServiceMessages.LACK_OF_PLACES.getMessage();
        }

        hotelAvailabilityRepository.save(availability);
        return ServiceMessages.AVAILABLE.getMessage();
    }

    public String increaseAvailability(int hotelId){
        Optional<HotelAvailablilityEntity> hotelAvailability = hotelAvailabilityRepository.findById(hotelId);
        if (hotelAvailability.isEmpty()) {
            return ServiceMessages.UNAVAILABLE.getMessage();
        }
        HotelAvailablilityEntity availability = hotelAvailability.get();
        availability.setAvailability(availability.getAvailability() + 1);
        hotelAvailabilityRepository.save(availability);
        return ServiceMessages.INCREASE_HOTEL_AVAILABILITY.getMessage();
    }

    public String checkRoomAvailability(int hotelId) {
        if (!isHotelAvailable(hotelId)) {
            return ServiceMessages.AVAILABLE.getMessage();
        } else {
            return ServiceMessages.UNAVAILABLE.getMessage();
        }
    }


    public List<HotelEntity> getAllHotels() {
        return hotelRepository.findAll();
    }
}
