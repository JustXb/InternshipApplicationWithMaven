package com.example.hotelsystem.service;

import com.example.hotelsystem.repository.impl.HotelAvailabilityRepository;
import com.example.hotelsystem.repository.impl.HotelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.hotelsystem.repository.entity.HotelAvailablilityEntity;
import com.example.hotelsystem.repository.entity.HotelEntity;
import com.example.hotelsystem.repository.impl.HotelJsonRepository;
import com.example.hotelsystem.transport.server.HotelServer;
import com.example.hotelsystem.util.Config;
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
    private final HotelJsonRepository hotelJsonRepository;
    private final HotelServer hotelServer;
    private final Logger LOGGER = Logger.getLogger(HotelService.class.getName());
    private final Config config;

    @Autowired
    public HotelService(HotelAvailabilityRepository hotelAvailabilityRepository, HotelRepository hotelRepository,HotelJsonRepository hotelJsonRepository, HotelServer hotelServer, Config config,
                        @Value("${processor.port}") int port) {
        this.hotelAvailabilityRepository = hotelAvailabilityRepository;
        this.hotelRepository = hotelRepository;
        this.hotelJsonRepository = hotelJsonRepository;
        this.hotelServer = hotelServer;
        this.config = config;
        this.port = port;
    }

    public void responseHotels() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            List<HotelEntity> hotels = hotelJsonRepository.loadHotelsFromFile();
            hotelRepository.saveAll(hotels);
            ObjectMapper objectMapper = new ObjectMapper();
            List<HotelAvailablilityEntity> hotelsAvailability = objectMapper.readValue
                    (new File("HotelsAvailability.json"), new TypeReference<List<HotelAvailablilityEntity>>() {});
            hotelAvailabilityRepository.saveAll(hotelsAvailability);
            String result = getString(hotels);

            LOGGER.info(ServiceMessages.WAITING_CONNECT.getMessage());

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    out.println(result);

                    int hotelId = Integer.parseInt(in.readLine());
//                    Optional<HotelAvailablilityEntity> hotel = null;
//                    if(hotelAvailabilityRepository.findById(hotelId).isPresent()){
//                        hotel = hotelAvailabilityRepository.findById(hotelId);
//                    }
                    HotelAvailablilityEntity hotel = hotelJsonRepository.readHotelFromFile(hotelId);
                    LOGGER.info(ServiceMessages.REQUEST_HOTEL_AVAILABILITY.getMessage() + hotelId);

                    if (isHotelAvailable(hotelId)) {
                        out.println(ServiceMessages.AVAILABLE.getMessage());
                    } else {
                        if (hotel == null) {
                            out.println(ServiceMessages.UNAVAILABLE.getMessage());
                        } else {
                            if (!hotel.decreaseAvailableRooms()) {
                                out.println(ServiceMessages.UNAVAILABLE_NOAVAILABILITY.getMessage());
                            } else {
                                out.println(ServiceMessages.AVAILABLE.getMessage());
                            }
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

    private boolean isHotelAvailable(int id) throws IOException {
        boolean isExist = false;
        List<HotelEntity> hotels = hotelJsonRepository.loadHotelsFromFile();
        for (HotelEntity hotel : hotels) {
            if (id == hotel.getId()) {
                isExist = true;
                break;
            }
        }

        if(!isExist){
            LOGGER.warning(ServiceMessages.WRONG_HOTEL.getMessage());
            return isExist;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        hotels = objectMapper.readValue(new File(config.getHotelsPath()), new TypeReference<List<HotelEntity>>() {});
        List<HotelAvailablilityEntity> hotelsAvailability = objectMapper.readValue(new File(config.getHotelsAvailabilityPath()),
                new TypeReference<List<HotelAvailablilityEntity>>() {});
        for (HotelEntity hotel : hotels) {
            if (hotel.getId() == id) {
                for (HotelAvailablilityEntity hotelAvailability : hotelsAvailability) {
                    if (hotel.getId() == hotelAvailability.getId()) {
                        if(hotelAvailability.decreaseAvailableRooms()){
                            objectMapper.writeValue(new File(config.getHotelsAvailabilityPath()), hotelsAvailability);
                            return true;
                        }
                        else{
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }
}
