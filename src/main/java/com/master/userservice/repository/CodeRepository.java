package com.master.userservice.repository;

import com.master.userservice.model.Code;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeRepository extends MongoRepository<Code, String> {
}
