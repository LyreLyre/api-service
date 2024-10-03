package org.example.apiservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apiservice.dto.ItemDto;
import org.example.apiservice.entity.Item;
import org.example.apiservice.exception.ItemNotFoundException;
import org.example.apiservice.mapper.ItemMapper;
import org.example.apiservice.repository.ItemJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Transactional
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemJpaRepository repository;
    private final ItemMapper mapper;

    /**
     * Получение объекта по id
     */
    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(UUID id) {
        Item item = this.repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Could not find Item with tracking id: " + id));
        return mapper.toDto(item);
    }

    /**
     * Сохранение новой записи
     */
    @Override
    public UUID createItem(ItemDto newItem) {
        Item item = new Item();
        mapper.toEntity(newItem, item);
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
        }
        var saved = repository.save(item);
        return saved.getId();
    }

    /**
     * Обновление записи по id
     */
    @Override
    public UUID updateItem(UUID id, ItemDto itemDto) {
        Item item = this.repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Could not find Item with tracking id: " + id));
        mapper.toEntity(itemDto, item);
        return repository.save(item).getId();
    }

    /**
     * Удаление записи
     */
    @Override
    public boolean deleteItem(UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
