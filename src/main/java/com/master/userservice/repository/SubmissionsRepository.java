package com.master.userservice.repository;

import com.master.userservice.model.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionsRepository extends MongoRepository<Submission, String> {

}
