package org.example.apiservice.mapper;

import org.example.apiservice.dto.ItemDto;
import org.example.apiservice.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .date(item.getDate())
                .build();
    }

    public void toEntity(ItemDto itemDto, Item entity) {
        entity.setId(itemDto.getId());
        entity.setName(itemDto.getName());
        entity.setDate(itemDto.getDate());
    }

}
