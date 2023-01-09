package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() throws NoSuchFieldException {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUserValidPassword() throws Exception {
        CreateUserRequest r = validUserRequest();
        when(encoder.encode(r.getPassword())).thenReturn("thisIsHashed");

        final ResponseEntity<User> response = userController.createUser(r);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        Assert.assertNotNull(u);
        Assert.assertEquals(0, u.getId());
        Assert.assertEquals(r.getUsername(), u.getUsername());
        Assert.assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void createUserInvalidPassword() throws Exception {
        CreateUserRequest r = invalidUserRequest();

        final ResponseEntity<User> response = userController.createUser(r);
        Assert.assertNotNull(response);
        Assert.assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void GetUserByInValidName() throws Exception {
        String invalidName = "aaa";

        // it is not really storing the user into the database
        // should mock the userRepository response
        when(userRepository.findByUsername(invalidName)).thenReturn(null);

        final ResponseEntity<User> response = userController.findByUserName(invalidName);
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void GetUserByValidName() throws Exception {
        CreateUserRequest r = validUserRequest();

        final ResponseEntity<User> responseUser = userController.createUser(r);
        User user = responseUser.getBody();  // this is what the server will save and return

        // it is not really storing the user into the database
        // should mock the userRepository response
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        User user1 = response.getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Assert.assertEquals(r.getUsername(), user1.getUsername());
    }

    @Test
    public void GetUserByValidId() throws Exception {
        CreateUserRequest r = validUserRequest();

        final ResponseEntity<User> responseUser = userController.createUser(r);
        User user = responseUser.getBody();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(user.getId());
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());
        Assert.assertEquals(0, user.getId());
    }

    private CreateUserRequest validUserRequest(){
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        return r;
    }

    private CreateUserRequest invalidUserRequest(){
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test1");
        r.setPassword("test");
        r.setConfirmPassword("test");
        return r;
    }


}
