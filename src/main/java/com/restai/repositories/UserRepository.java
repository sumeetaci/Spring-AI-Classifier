package com.restai.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restai.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	User findByUsername(String username);

}
