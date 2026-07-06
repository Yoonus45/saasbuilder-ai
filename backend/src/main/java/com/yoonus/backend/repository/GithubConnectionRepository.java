package com.yoonus.backend.repository;

import com.yoonus.backend.entity.User;
import com.yoonus.backend.entity.github.GithubConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubConnectionRepository extends JpaRepository<GithubConnection, Long> {
    Optional<GithubConnection> findByUser(User user);
}
