package org.example.apiservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
public class ItemDto {

    private UUID id;
    private String name;
    private LocalDate date;
}
