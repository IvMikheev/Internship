package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class ShipController {
    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    public ResponseEntity<List<Ship>> getShipsList(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "planet", required = false) String planet,
            @RequestParam(name = "shipType", required = false) ShipType shipType,
            @RequestParam(name = "after", required = false) Long after,
            @RequestParam(name = "before", required = false) Long before,
            @RequestParam(name = "isUsed", required = false) Boolean isUsed,
            @RequestParam(name = "minSpeed", required = false) Double minSpeed,
            @RequestParam(name = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "maxRating", required = false) Double maxRating,
            @RequestParam(name = "order", required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        return new ResponseEntity<>(shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping(value = "/ships/count")
    public ResponseEntity<Integer> getShipsCount(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "planet", required = false) String planet,
            @RequestParam(name = "shipType", required = false) ShipType shipType,
            @RequestParam(name = "after", required = false) Long after,
            @RequestParam(name = "before", required = false) Long before,
            @RequestParam(name = "isUsed", required = false) Boolean isUsed,
            @RequestParam(name = "minSpeed", required = false) Double minSpeed,
            @RequestParam(name = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "maxRating", required = false) Double maxRating) {

        return new ResponseEntity<>(shipService.getShipsCount(name, planet, shipType, after, before, isUsed, minSpeed,
                                            maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating), HttpStatus.OK);
    }

    @PostMapping(value = "/ships")
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        return shipService.createShip(ship);
    }

    @GetMapping(value = "ships/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable Long id) {
        return shipService.getShip(id);
    }

    @PostMapping(value = "/ships/{id}")
    public ResponseEntity<Ship> updateShip(@PathVariable Long id, @RequestBody Ship ship) {
        return shipService.updateShip(id, ship);
    }

    @DeleteMapping(value = "/ships/{id}")
    public ResponseEntity<Ship> deleteShip(@PathVariable Long id) {
        return shipService.deleteShip(id);
    }
}
