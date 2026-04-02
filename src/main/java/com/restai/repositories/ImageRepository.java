package com.restai.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restai.entity.ImageMetadata;

@Repository
public interface ImageRepository extends JpaRepository<ImageMetadata, Long> {
	

}
