package org.example.apiservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.apiservice.dto.ItemDto;
import org.example.apiservice.entity.Item;
import org.example.apiservice.repository.ItemJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemJpaRepository itemJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        itemJpaRepository.deleteAll();
    }

    @Test
    void testGetById() throws Exception {
        UUID id = UUID.randomUUID();
        Item item = Item.builder()
                .id(id)
                .name("Test name")
                .date(LocalDate.of(2024, 10, 1))
                .build();
        itemJpaRepository.save(item);

        mockMvc.perform(get("/test/item/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Test name")))
                .andExpect(jsonPath("$.date", is(String.valueOf(LocalDate.of(2024, 10, 1)))));
    }

    @Test
    void testCreateItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("To be created")
                .date(LocalDate.of(2024, 10, 1))
                .build();

        String response = mockMvc.perform(post("/test/item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())))
                .andReturn().getResponse().getContentAsString();

        UUID id = objectMapper.readValue(response, UUID.class);
        assertNotNull(id);
        boolean isExists = itemJpaRepository.existsById(id);
        assertTrue(isExists, "Record with ID must be exist in DB");
    }

    @Test
    void testUpdateById() throws Exception {
        UUID id = UUID.randomUUID();
        Item item = Item.builder()
                .id(id)
                .name("To be updated")
                .date(LocalDate.of(2024, 10, 1))
                .build();
        itemJpaRepository.save(item);

        ItemDto updatedDto = ItemDto.builder()
                .id(id)
                .name("Updated")
                .date(LocalDate.of(2024, 10, 15))
                .build();

        mockMvc.perform(patch("/test/item/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + id.toString() + "\""));

        mockMvc.perform(get("/test/item/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Updated")))
                .andExpect(jsonPath("$.date", is(String.valueOf(LocalDate.of(2024, 10, 15)))));
    }

    @Test
    void deleteById() throws Exception {
        UUID id = UUID.randomUUID();
        Item item = Item.builder()
                .id(id)
                .name("To be deleted")
                .date(LocalDate.of(2024, 10, 1))
                .build();
        itemJpaRepository.save(item);

        mockMvc.perform(delete("/test/item/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/test/item/{id}", id))
                .andExpect(status().isNotFound());
    }

}