package com.tay.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import com.tay.model.InvalidatedToken;

@Repository
public interface InvalidatedTokenRepository extends JpaRepositoryImplementation<InvalidatedToken, String> {

}
