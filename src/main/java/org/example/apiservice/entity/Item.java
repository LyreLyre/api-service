package org.example.apiservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
@Entity
public class Item {

    /*    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @UuidGenerator*/
    @Id
    @Column(updatable = false)
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "date")
    private LocalDate date;
}
