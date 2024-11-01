package com.internshipbooking.webclient;

import com.example.request.HotelDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class HotelsWebClient {

    private final RestTemplate restTemplate;
    private final String hotelServiceUrl;
    private final String getAllHotelsPath;
    private final String checkAvailabilityPath;
    private final String increaseAvailabilityPath;

    public HotelsWebClient(
            RestTemplate restTemplate,
            @Value("${hotel.service.url}") String hotelServiceUrl,
            @Value("${hotel.service.getAllHotels}") String getAllHotelsPath,
            @Value("${hotel.service.checkAvailability}") String checkAvailabilityPath,
            @Value("${hotel.service.increaseAvailability}") String increaseAvailabilityPath) {
        this.restTemplate = restTemplate;
        this.hotelServiceUrl = hotelServiceUrl;
        this.getAllHotelsPath = getAllHotelsPath;
        this.checkAvailabilityPath = checkAvailabilityPath;
        this.increaseAvailabilityPath = increaseAvailabilityPath;
    }


        public List<HotelDTO> getAllHotels() {
            ResponseEntity<List<HotelDTO>> response = restTemplate.exchange(
                    hotelServiceUrl + getAllHotelsPath,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<HotelDTO>>() {}
            );
            return response.getBody();
        }

    public ResponseEntity<String> checkAvailability(int hotelId) {
        return restTemplate.postForEntity(hotelServiceUrl + checkAvailabilityPath + hotelId, null, String.class);
    }

    public ResponseEntity<String> increaseAvailability(int hotelId) {
        return restTemplate.postForEntity(hotelServiceUrl + increaseAvailabilityPath + hotelId, null, String.class);
    }
}
