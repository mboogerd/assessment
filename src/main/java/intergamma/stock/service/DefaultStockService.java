package intergamma.stock.service;

import intergamma.stock.domain.StockItemPatch;
import intergamma.stock.domain.StockItems;
import intergamma.stock.domain.StockTotals;
import intergamma.stock.domain.StockItem;
import intergamma.stock.repository.StockItemRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DefaultStockService implements StockService {

    private final StockItemRepository stockItemRepository;
    private ReservationProperties reservationProperties;

    public DefaultStockService(StockItemRepository stockItemRepository, ReservationProperties reservationProperties) {
        this.stockItemRepository = stockItemRepository;
        this.reservationProperties = reservationProperties;
    }

    @Override
    public StockTotals getTotalStock(String productCode) {
        long available = stockItemRepository.countByProductCodeAndReservationTimestampNull(productCode);
        long reserved = stockItemRepository.countByProductCodeAndReservationTimestampNotNull(productCode);
        return new StockTotals(available, reserved);
    }

    @Override
    public StockItems getStockItems(String productCode) {
        return new StockItems(stockItemRepository.findByProductCode(productCode));
    }

    @Override
    public StockItems addStockItems(String productCode, String storeCode, int quantity) {
        List<StockItem> stockItems = IntStream
                .range(0, quantity)
                .mapToObj(n -> new StockItem(productCode, storeCode))
                .collect(Collectors.toList());

        stockItemRepository.saveAll(stockItems);
        return new StockItems(stockItems);
    }

    @Override
    public void removeStockItem(Long stockItemId) {
        stockItemRepository.deleteById(stockItemId);
    }

    @Override
    public Optional<StockItem> updateStockItem(long stockItemId, StockItemPatch patch) {
        Optional<StockItem> updated = stockItemRepository.findById(stockItemId);

        updated.ifPresent(stockItem -> {
            stockItem.apply(patch);
            stockItemRepository.save(stockItem);
        });

        return updated;
    }

    @Override
    @Scheduled(fixedRate = 1000) // Cleanup once every second
    public void revokeStaleReservations() {
        LocalDateTime staleReservationThreshold = LocalDateTime.now().minus(reservationProperties.getReservationDuration());
        Iterable<StockItem> staleEntries = stockItemRepository.findByReservationTimestampLessThan(staleReservationThreshold);

        stockItemRepository.deleteAll(staleEntries);
    }
}
