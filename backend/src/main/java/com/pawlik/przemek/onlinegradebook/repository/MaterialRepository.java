package com.pawlik.przemek.onlinegradebook.repository;

import com.pawlik.przemek.onlinegradebook.model.Material;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends CrudRepository<Material, Long> {
}