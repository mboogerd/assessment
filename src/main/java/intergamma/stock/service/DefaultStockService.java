package intergamma.stock.service;

import intergamma.stock.api.StockItems;
import intergamma.stock.api.StockTotals;
import intergamma.stock.repository.StockItem;
import intergamma.stock.repository.StockItemRepository;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class DefaultStockService implements StockService {

    private final StockItemRepository stockItemRepository;

    public DefaultStockService(StockItemRepository stockItemRepository) {
        this.stockItemRepository = stockItemRepository;
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
    public void addStockItems(String productCode, String storeCode, int quantity) {
        Stream<StockItem> stockItemStream = IntStream.range(0, quantity).mapToObj(n -> {
            StockItem stockItem = new StockItem(productCode);
            stockItem.setStore(storeCode);
            return stockItem;
        });

        stockItemRepository.saveAll(stockItemStream.collect(Collectors.toList()));
    }

    @Override
    public void removeStockItem(Long stockItemId) {
        stockItemRepository.deleteById(stockItemId);
    }

    @Override
    public Iterable<Long> reserveProduct(String productCode) {
        return null;
    }

    @Override
    public boolean reserveStockItem(Long stockItemId) {
        return false;
    }
}