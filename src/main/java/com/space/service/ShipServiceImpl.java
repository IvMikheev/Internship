package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {
    private ShipRepository shipRepository;
    private static final int CURRENT_YEAR = 3019;
    private static final ResponseEntity<Ship> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

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
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(before);
            calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);
            Date dateBefore = new Date(calendar.getTimeInMillis() - 1L);
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

    private double calcShipRating(double speed, double coefficient, Date shipProdYear) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(shipProdYear.getTime());
        double rating = (80 * speed * coefficient) / (CURRENT_YEAR - date.get(Calendar.YEAR) + 1);
        return new BigDecimal(rating).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
    }

    private boolean isValidId(Long id) {
        if (id != null) {
            return id > 0;
        }
        return false;
    }

    private boolean isValidParams(Object param) {
        if (param instanceof String) {
            String str = (String) param;
            return !str.isEmpty() & str.length() <= 50;
        }

        if (param instanceof Enum) return true;

        if (param instanceof Date) {
            Date date = (Date) param;
            if (date.getTime() > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(2800, Calendar.JANUARY, 1, 0, 0, 0);
                if (date.after(calendar.getTime())) {
                    calendar.set(CURRENT_YEAR, Calendar.getInstance().get(Calendar.MONTH), Calendar.DATE);
                    return date.before(calendar.getTime());
                }
            }
        }

        if (param instanceof Boolean) return true;

        if (param instanceof Double) {
            double d = (double) param;
            return d >= 0.01D & d <= 0.99D;
        }

        if (param instanceof Integer) {
            int i = (int) param;
            return i >= 1 & i <= 9999;
        }

        return false;
    }

    private double getCoefficient(Ship ship) {
        double coefficient = 1;
        if (!isValidParams(ship.getUsed())) {
            ship.setUsed(false);
        } else if (ship.getUsed()) coefficient = 0.5D;
        return coefficient;
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
    public ResponseEntity<Ship> createShip(Ship ship) {
        if (!(isValidParams(ship.getName()) & isValidParams(ship.getPlanet()) &
                isValidParams(ship.getShipType()) & isValidParams(ship.getProdDate()) &
                isValidParams(ship.getSpeed()) & isValidParams(ship.getCrewSize()))) {

            return BAD_REQUEST;
        }

        ship.setRating(calcShipRating(ship.getSpeed(), getCoefficient(ship), ship.getProdDate()));

        shipRepository.saveAndFlush(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Ship> getShip(Long id) {
        if (isValidId(id)) {
            Optional<Ship> ship = shipRepository.findById(id);
            return ship.map(value ->
                    new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() ->
                    new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }

        return BAD_REQUEST;
    }

    @Override
    public ResponseEntity<Ship> updateShip(Long id, Ship ship) {
        if (!isValidId(id)) return BAD_REQUEST;
        ResponseEntity<Ship> response = getShip(id);

        if (response.getBody() != null) {
            Ship shipFromDb = response.getBody();
            if (ship.getName() == null & ship.getPlanet() == null & ship.getShipType() == null &
                    ship.getProdDate() == null & ship.getUsed() == null &
                    ship.getSpeed() == null & ship.getCrewSize() == null) {

                return response;
            }

            if (ship.getName() != null) {
                if (!isValidParams(ship.getName())) {
                    return BAD_REQUEST;
                } else shipFromDb.setName(ship.getName());
            }

            if (ship.getPlanet() != null) {
                if (!isValidParams(ship.getPlanet())) {
                    return BAD_REQUEST;
                } else shipFromDb.setPlanet(ship.getPlanet());
            }

            if (ship.getShipType() != null) {
                if (!isValidParams(ship.getShipType())) {
                    return BAD_REQUEST;
                } else shipFromDb.setShipType(ship.getShipType());
            }

            if (ship.getProdDate() != null) {
                if (!isValidParams(ship.getProdDate())) {
                    return BAD_REQUEST;
                } else shipFromDb.setProdDate(new Date(ship.getProdDate().getTime()));
            }

            if (ship.getUsed() != null) {
                if (!isValidParams(ship.getUsed())) {
                    return BAD_REQUEST;
                } else shipFromDb.setUsed(ship.getUsed());
            }

            if (ship.getSpeed() != null) {
                if (!isValidParams(ship.getSpeed())) {
                    return BAD_REQUEST;
                } else shipFromDb.setSpeed(ship.getSpeed());
            }

            if (ship.getCrewSize() != null) {
                if (!isValidParams(ship.getCrewSize())) {
                    return BAD_REQUEST;
                } else shipFromDb.setCrewSize(ship.getCrewSize());
            }

            if (ship.getRating() == null) {
                double speed;
                Date date;
                if (ship.getSpeed() != null) {
                    speed = ship.getSpeed();
                } else speed = shipFromDb.getSpeed();
                if (ship.getProdDate() != null) {
                    date = ship.getProdDate();
                } else date = shipFromDb.getProdDate();

                shipFromDb.setRating(calcShipRating(speed, getCoefficient(ship), date));
            } else {
                shipFromDb.setRating(calcShipRating(shipFromDb.getSpeed(), getCoefficient(ship), shipFromDb.getProdDate()));
            }

            shipRepository.saveAndFlush(shipFromDb);
        }
        return response;
    }

    @Override
    public ResponseEntity<Ship> deleteShip(Long id) {
        ResponseEntity<Ship> response = getShip(id);
        if (response.getBody() != null) shipRepository.deleteById(id);
        return response;
    }
}
