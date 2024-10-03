package org.example.apiservice.repository;

import org.example.apiservice.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, UUID> {


}
