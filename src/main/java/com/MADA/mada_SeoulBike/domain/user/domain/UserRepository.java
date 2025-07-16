package com.MADA.mada_SeoulBike.domain.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUserId(String userId);
    Optional<Users> findByEmail(String email);

}
