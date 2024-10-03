package org.example.apiservice;

import org.example.apiservice.dto.ItemDto;
import org.example.apiservice.service.ItemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateItemsTest {

    @Autowired
    private ItemService itemService;

    private final List<UUID> itemIds = Collections.synchronizedList(new ArrayList<>());

    @Test
    @DisplayName("Создание 100k новых записей")
    public void testCreate100kItems() {
        int total = 100_000;
        int batchSize = 10_000;
        int batches = total / batchSize;

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(batches);
        AtomicInteger createdCount = new AtomicInteger(0);

        for (int i = 0; i < batches; i++) {
            executor.submit(() -> {
                for (int j = 0; j < batchSize; j++) {
                    ItemDto dto = generateRandomItemDto();
                    UUID id = itemService.createItem(dto);
                    itemIds.add(id);
                    createdCount.incrementAndGet();
                }
                latch.countDown();
            });
        }

        try {
            boolean completed = latch.await(30, TimeUnit.MINUTES);
            Assertions.assertTrue(completed, "Не все записи были созданы вовремя");
            Assertions.assertEquals(total, createdCount.get(), "Количество созданных записей не соответствует ожидаемому");
        } catch (InterruptedException e) {
            Assertions.fail("Тест был прерван");
        } finally {
            executor.shutdown();
        }
    }

    private ItemDto generateRandomItemDto() {
        return ItemDto.builder()
                .name(UUID.randomUUID().toString())
                .date(LocalDate.now()).build();
    }
}
