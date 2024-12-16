package com.example.deploy.domain.user.repository;


import com.example.deploy.domain.user.exception.UserException;
import com.example.deploy.domain.user.exception.errortype.UserErrorCode;
import com.example.deploy.domain.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNameAndPhone(String name, String phone);

    default User getUserById(Long id) {
        return findById(id).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
