package com.hcx.hcxPayor.repository;


import com.hcx.hcxPayor.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User,Integer> {

    User findByUserName (String userName);
}
