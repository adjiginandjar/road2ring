package com.r2r.road2ring.modules.user;

import java.util.List;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends DataTablesRepository<User,Integer> {
  User findOneByEmailIgnoreCase(String email);
  User findTopOneByEmailIgnoreCase(String email);
  User findOneByVerificationCode(String code);
  User findOneByVerificationCodePassword(String code);
  User findOneBySocialUid(String uid);
  List<User> findAllByRoleId(int id);
}
