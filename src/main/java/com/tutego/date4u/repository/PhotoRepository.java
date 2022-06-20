package com.tutego.date4u.repository;

import com.tutego.date4u.repository.enities.Photo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PhotoRepository extends CrudRepository<Photo, Long> {

}
