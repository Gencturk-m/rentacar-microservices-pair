package com.kodlamaio.inventoryservice.repository;

import com.kodlamaio.inventoryservice.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
}
