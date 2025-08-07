package com.MADA.mada_SeoulBike.user.repository;

import com.MADA.mada_SeoulBike.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //이메일로 유저 찾기 (회원가입/로그인 중복 검사에 사용)
    Optional<User> findByEmail(String email);
}
