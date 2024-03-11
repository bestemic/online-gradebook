package com.bestemic.onlinegradebook.repository;

import com.bestemic.onlinegradebook.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
}
