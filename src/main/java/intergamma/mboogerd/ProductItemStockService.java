package intergamma.mboogerd;

import org.springframework.stereotype.Service;

@Service
public class ProductItemStockService implements StockService {

    private final StockItemRepository productItems;

    public ProductItemStockService(StockItemRepository productItems) {
        this.productItems = productItems;
    }

    @Override
    public Stock getStockLevel(String productCode) {
        long available = productItems.countByProductCodeAndReservationTimestampNull(productCode);
        long reserved = productItems.countByProductCodeAndReservationTimestampNotNull(productCode);
        return new Stock(available, reserved);
    }

    @Override
    public Iterable<Long> reserveProductItems(String productCode) {
        return null;
    }
}
