package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShipService {

    List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                            Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                            Integer maxCrewSize, Double minRating, Double maxRating, ShipOrder order,
                            Integer pageNumber, Integer pageSize);

    Integer getShipsCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                          Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                          Double minRating, Double maxRating);

    ResponseEntity<?> createShip(Ship ship);

    ResponseEntity<?> getShip(Long id);

    ResponseEntity<?> updateShip(Long id, Ship ship);

    ResponseEntity<?> deleteShip(Long id);
}
