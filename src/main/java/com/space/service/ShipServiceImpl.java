package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {
    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    private Predicate toPredicate(Root<Ship> root, CriteriaBuilder criteriaBuilder, String name,
                                  String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                                  Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                                  Double minRating, Double maxRating) {

        List<Predicate> predicates = new ArrayList<>();

        if (name != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + name + "%")));
        if (planet != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("planet"), "%" + planet + "%")));
        if (shipType != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("shipType"), shipType)));
        if (after != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), new Date(after))));
        if (before != null) {
            Date dateBefore = new Date(new Date(new Date(before).getYear(), Calendar.JANUARY, 1).getTime() - 1L);
            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), dateBefore)));
        }
        if (isUsed != null) predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("isUsed"), isUsed)));
        if (minSpeed != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed)));
        if (maxSpeed != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed)));
        if (minCrewSize != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize)));
        if (maxCrewSize != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize)));
        if (minRating != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating)));
        if (maxRating != null)
            predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating)));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating, ShipOrder order,
                                   Integer pageNumber, Integer pageSize) {

        return shipRepository.findAll((Specification<Ship>) (root, query, criteriaBuilder) ->
                        toPredicate(
                                root, criteriaBuilder, name, planet, shipType, after, before, isUsed, minSpeed,
                                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating),
                PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()))).getContent();
    }

    @Override
    public Integer getShipsCount(String name, String planet, ShipType shipType, Long after, Long before,
                                 Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                 Integer maxCrewSize, Double minRating, Double maxRating) {

        return (int) shipRepository.count((Specification<Ship>) (root, query, criteriaBuilder) ->
                toPredicate(
                        root, criteriaBuilder, name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                        minCrewSize, maxCrewSize, minRating, maxRating));
    }


    @Override
    public ResponseEntity<?> createShip(Ship ship) {
        return null;
    }

    @Override
    public ResponseEntity<?> getShip(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<?> updateShip(Long id, Ship ship) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteShip(Long id) {
        return null;
    }
}
