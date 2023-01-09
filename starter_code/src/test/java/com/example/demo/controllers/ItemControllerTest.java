package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private final ItemRepository itemRepository = mock(ItemRepository.class);


    @Before
    public void setUp() throws NoSuchFieldException {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
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

    @Test
    public void findAllItem() throws Exception {
        List<Item> itemList = createItems();
        when(itemRepository.findAll()).thenReturn(itemList);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        Assert.assertNotNull(items);
        Assert.assertEquals(itemList.size(), items.size());
    }

    @Test
    public void getItemByValidId() throws Exception {
        List<Item> itemList = createItems();
        Long validId = 0L;
        when(itemRepository.findById(0L)).thenReturn(Optional.ofNullable(itemList.get(0)));

        final ResponseEntity<Item> response = itemController.getItemById(0L);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        Item item = response.getBody();
        Assert.assertNotNull(item);
        Assert.assertEquals(itemList.get(0).getName(), item.getName());
    }

    @Test
    public void getItemByValidName() throws Exception {
        List<Item> itemList = createItems();
        String validName = "first";
        when(itemRepository.findByName(validName)).thenReturn(itemList);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(validName);
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        Assert.assertNotNull(items);
        Assert.assertEquals(itemList.size(), items.size());
        Assert.assertEquals(itemList.get(0).getName(), items.get(0).getName());
        Assert.assertEquals(itemList.get(1).getName(), items.get(1).getName());
    }

    @Test
    public void getItemByInvalidName() throws Exception {
        List<Item> itemList = createItems();
        String invalidName = "second";
        when(itemRepository.findByName(invalidName)).thenReturn(null);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(invalidName);
        Assert.assertNotNull(response);
        Assert.assertEquals(404, response.getStatusCodeValue());
    }


}
