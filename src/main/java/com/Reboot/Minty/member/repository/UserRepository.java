package com.Reboot.Minty.member.repository;

import com.Reboot.Minty.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    int countByEmail(String email);
}
