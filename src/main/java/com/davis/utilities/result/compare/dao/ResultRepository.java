package com.davis.utilities.result.compare.dao;

import com.davis.utilities.result.compare.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ResultRepository extends JpaRepository<Result, Long> {
  List<Result> findByNumericName(String numericName);
  Result findFirstByNumericName(String numericName);
  List<Result> findByAlgoProcessedIsTrue();

  /* @Query("SELECT () from dataset where s.NUMERIC_ID IS NOT NULL")
  List<Image> getAllNumerics();*/

  //@Query("SELECT COUNT(u) FROM DATASET u WHERE u.id IS NOT NULL")
  //@Query("SELECT COUNT(u) FROM User u WHERE u.name=?1")
  //@Query("SELECT COUNT(u) FROM User u WHERE u.name=:name")
  //@Modifying
  //@Query("update User u set u.firstname = ?1, u.lastname = ?2 where u.id = ?3")
  //@Query("select u from User u")
  //Stream<User> findAllByCustomQueryAndStream();
  //Stream<User> readAllByFirstnameNotNull();
  //@Query("select u from User u")
  //Stream<User> streamAllPaged(Pageable pageable);
}
