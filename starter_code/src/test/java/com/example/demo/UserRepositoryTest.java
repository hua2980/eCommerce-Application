package com.example.demo;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

  @Test
  public void testFindByUsername() {
    User user = userRepository.findByUsername("test");
    Assertions.assertNull(user);
  }
}
