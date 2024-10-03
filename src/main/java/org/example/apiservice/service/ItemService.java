package org.example.apiservice.service;

import org.example.apiservice.dto.ItemDto;

import java.util.UUID;

public interface ItemService {

    ItemDto getItemById(UUID id);

    UUID createItem(ItemDto newItem);

    UUID updateItem(UUID id, ItemDto itemDto);

    boolean deleteItem(UUID id);
}
