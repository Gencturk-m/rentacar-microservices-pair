package com.kodlamaio.inventoryservice.repository;

import com.kodlamaio.inventoryservice.entities.Car;
import com.kodlamaio.inventoryservice.entities.enums.CarState;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
    @Modifying
    @Transactional
    @Query(value = "update Car set carState =:carState where id =:id")
    void changeStateByCarId(CarState carState, UUID id);
}
