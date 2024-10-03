package org.example.apiservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apiservice.dto.ItemDto;
import org.example.apiservice.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/test/item")
public class ItemController {

    private final ItemService service;

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemDto> getById(@PathVariable final UUID id) {
        log.info("Received request to get Item by id: {}", id);
        final var response = this.service.getItemById(id);
        log.info("Returning Item by tracking id {}", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "")
    public ResponseEntity<UUID> createItem(@RequestBody final ItemDto dto) {
        log.info("Received request to save new Item");
        final var response = this.service.createItem(dto);
        log.info("Successful saving of Item with id {}", response);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/update/{id}")
    public ResponseEntity<UUID> updateById(@PathVariable final UUID id, @RequestBody ItemDto dto) {
        log.info("Received request to update Item with id: {}", id);
        final UUID response = this.service.updateItem(id, dto);
        log.info("Successful updating of Item with id {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable final UUID id) {
        log.info("Received request to delete Item with id: {}", id);
        final var response = this.service.deleteItem(id);
        if (response) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
