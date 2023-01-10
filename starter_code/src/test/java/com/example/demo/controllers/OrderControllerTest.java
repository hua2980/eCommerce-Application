package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() throws NoSuchFieldException {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    private List<Item> createItems(){
        Item item1 = new Item();
        item1.setId(0L);
        item1.setName("first");

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("first");

        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);
        return itemList;
    }

    private User createUser(){
        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        return user;
    }

    @Test
    public void submitValidUser() throws Exception {
        User user = createUser();
        List<Item> items = createItems();

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(5.0));

        user.setCart(cart);

        String validUsername = user.getUsername();

        when(userRepository.findByUsername(validUsername)).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit(validUsername);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        UserOrder responseOrder = response.getBody();
        Assert.assertNotNull(responseOrder);
        Assert.assertEquals(cart.getItems().size(), responseOrder.getItems().size());
        Assert.assertEquals(cart.getTotal(), responseOrder.getTotal());
    }

    @Test
    public void submitInvalidUser() throws Exception {
        User user = createUser();
        List<Item> items = createItems();

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(5.0));

        user.setCart(cart);

        String invalidUsername = "test2";

        when(userRepository.findByUsername(invalidUsername)).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit(invalidUsername);
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrderValidUser() throws Exception {
        User user = createUser();
        List<Item> items = createItems();

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(5.0));

        user.setCart(cart);

        String validUsername = user.getUsername();

        when(userRepository.findByUsername(validUsername)).thenReturn(user);

        final ResponseEntity<UserOrder> response1 = orderController.submit(validUsername);
        UserOrder responseOrder = response1.getBody();
        List<UserOrder> orders = new ArrayList<>();
        orders.add(responseOrder);

        when(orderRepository.findByUser(user)).thenReturn(orders);
        final ResponseEntity<List<UserOrder>> response2 = orderController.getOrdersForUser(validUsername);
        Assert.assertNotNull(response2);
        Assert.assertEquals(200, response2.getStatusCodeValue());

        List<UserOrder> response2Order = response2.getBody();
        Assert.assertNotNull(response2Order);
        Assert.assertEquals(orders.size(), response2Order.size());
        Assert.assertEquals(orders.get(0).getTotal(), response2Order.get(0).getTotal());
        Assert.assertEquals(orders.get(0).getUser(), response2Order.get(0).getUser());
    }

    @Test
    public void getOrderInvalidUser() throws Exception {
        User user = createUser();
        List<Item> items = createItems();

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(5.0));

        user.setCart(cart);

        String invalidUsername = "test2";

        when(userRepository.findByUsername(invalidUsername)).thenReturn(null);

        final ResponseEntity<List<UserOrder>> response2 = orderController.getOrdersForUser(invalidUsername);
        Assert.assertNotNull(response2);
        Assert.assertEquals(404, response2.getStatusCodeValue());
    }



}
