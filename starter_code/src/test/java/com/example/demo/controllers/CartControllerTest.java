package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() throws NoSuchFieldException {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addCartValid() throws Exception {
        Item item = new Item();
        item.setId(0L);
        item.setName("first");
        item.setPrice(BigDecimal.valueOf(1.00));

        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);

        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(0L);
        r.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(r);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Cart responseCart = response.getBody();
        Assert.assertNotNull(responseCart);
        Assert.assertEquals(user.getId(), responseCart.getUser().getId());
        Assert.assertEquals(r.getQuantity(), responseCart.getItems().size());
        Assert.assertEquals(item.getPrice().add(item.getPrice()), responseCart.getTotal());
    }

    @Test
    public void addCartInvalidUser() throws Exception {
        String invalidName = "test2";

        Item item = new Item();
        item.setId(0L);
        item.setName("first");
        item.setPrice(BigDecimal.valueOf(1.00));

        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);

        user.setCart(cart);

        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));
        when (userRepository.findByUsername(invalidName)).thenReturn(null);

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername(invalidName);
        r.setItemId(0L);
        r.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(r);
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addCartInvalidItem() throws Exception {
        Long invalidItem = 2L;

        Item item = new Item();
        item.setId(0L);
        item.setName("first");
        item.setPrice(BigDecimal.valueOf(1.00));

        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);

        user.setCart(cart);

        when(itemRepository.findById(invalidItem)).thenReturn(Optional.empty());
        when (userRepository.findByUsername("test")).thenReturn(user);

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(invalidItem);
        r.setQuantity(2);

        final ResponseEntity<Cart> response = cartController.addTocart(r);
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartValid() throws Exception {
        Item item = new Item();
        item.setId(0L);
        item.setName("first");
        item.setPrice(BigDecimal.valueOf(1.00));

        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.addItem(item);

        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(0L);
        r.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(r);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Cart responseCart = response.getBody();
        Assert.assertNotNull(responseCart);
        Assert.assertEquals(user.getId(), responseCart.getUser().getId());
        Assert.assertEquals(0, responseCart.getItems().size());
        Assert.assertEquals(BigDecimal.valueOf(0.0), responseCart.getTotal());
    }

    @Test
    public void removeFromCartInvalidUser() throws Exception {
        String invalidName = "test2";
        Item item = new Item();
        item.setId(0L);
        item.setName("first");
        item.setPrice(BigDecimal.valueOf(1.00));

        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.addItem(item);

        user.setCart(cart);

        when(userRepository.findByUsername(invalidName)).thenReturn(null);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername(invalidName);
        r.setItemId(0L);
        r.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(r);
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromCartInvalidItem() throws Exception {
        Long invalidId = 2L;
        Item item = new Item();
        item.setId(0L);
        item.setName("first");
        item.setPrice(BigDecimal.valueOf(1.00));

        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.addItem(item);

        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(invalidId)).thenReturn(Optional.empty());

        ModifyCartRequest r = new ModifyCartRequest();
        r.setUsername("test");
        r.setItemId(invalidId);
        r.setQuantity(1);

        final ResponseEntity<Cart> response = cartController.removeFromcart(r);
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }

}
