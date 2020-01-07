package intragamma.mboogerd;

import org.springframework.stereotype.Service;

@Service
public class ProductItemStockService implements StockService {

    private final ProductItemRepository productItems;

    public ProductItemStockService(ProductItemRepository productItems) {
        this.productItems = productItems;
    }

    @Override
    public Stock getStockLevel(long productId) {
        long available = productItems.countByProductIdAndIsReservedFalse(productId);
        long reserved = productItems.countByProductIdAndIsReservedTrue(productId);
        return new Stock(available, reserved);
    }
}
