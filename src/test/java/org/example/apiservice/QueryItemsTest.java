package org.example.apiservice;

import org.example.apiservice.dto.ItemDto;
import org.example.apiservice.service.ItemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryItemsTest {

    @Autowired
    private ItemService itemService;

    private final List<UUID> itemIds = Collections.synchronizedList(new ArrayList<>());

    @BeforeAll
    public void setup() {
        // Создание 1M записей перед выполнением теста выборки
        int total = 1_000_000;
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
            boolean completed = latch.await(60, TimeUnit.MINUTES);
            Assertions.assertTrue(completed, "Не все записи были созданы вовремя");
            Assertions.assertEquals(total, createdCount.get(), "Количество созданных записей не соответствует ожидаемому");
        } catch (InterruptedException e) {
            Assertions.fail("Тест был прерван");
        } finally {
            executor.shutdown();
        }
    }

    @Test
    @DisplayName("Выборка 1M произвольных записей через 100 подключений с сбором статистики")
    public void testQuery1MItems() {
        int totalQueries = 1_000_000;
        int concurrentConnections = 100;
        int queriesPerConnection = totalQueries / concurrentConnections;

        ExecutorService executor = Executors.newFixedThreadPool(concurrentConnections);
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        CountDownLatch latch = new CountDownLatch(concurrentConnections);

        Instant start = Instant.now();

        for (int i = 0; i < concurrentConnections; i++) {
            executor.submit(() -> {
                Random random = new Random();
                for (int j = 0; j < queriesPerConnection; j++) {
                    UUID randomId = getRandomItemId(random);
                    if (randomId != null) {
                        Instant queryStart = Instant.now();
                        try {
                            itemService.getItemById(randomId);
                        } catch (Exception e) {
                            // Обработка исключений, если необходимо
                        }
                        Instant queryEnd = Instant.now();
                        long duration = Duration.between(queryStart, queryEnd).toMillis();
                        responseTimes.add(duration);
                    }
                }
                latch.countDown();
            });
        }

        try {
            boolean completed = latch.await(60, TimeUnit.MINUTES);
            Assertions.assertTrue(completed, "Не все запросы были выполнены вовремя");
        } catch (InterruptedException e) {
            Assertions.fail("Тест был прерван");
        } finally {
            executor.shutdown();
        }

        Instant end = Instant.now();
        Duration totalDuration = Duration.between(start, end);

        // Проверка, что responseTimes не пустой
        Assertions.assertFalse(responseTimes.isEmpty(), "Список времен ответов пуст");

        // Вычисление статистики
        List<Long> sortedTimes = responseTimes.stream().sorted().collect(Collectors.toList());
        long median = sortedTimes.get(sortedTimes.size() / 2);
        long p95 = sortedTimes.get((int) (sortedTimes.size() * 0.95));
        long p99 = sortedTimes.get((int) (sortedTimes.size() * 0.99));

        System.out.println("Общее время выполнения запросов: " + totalDuration.toMillis() + " мс");
        System.out.println("Медианное время: " + median + " мс");
        System.out.println("95-й процентиль: " + p95 + " мс");
        System.out.println("99-й процентиль: " + p99 + " мс");
    }

    private UUID getRandomItemId(Random random) {
        if (itemIds.isEmpty()) return null;
        int index = random.nextInt(itemIds.size());
        return itemIds.get(index);
    }

    private ItemDto generateRandomItemDto() {
        return ItemDto.builder()
                .name(UUID.randomUUID().toString())
                .date(LocalDate.now()).build();
    }
}
