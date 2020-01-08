package intergamma.stock.service;

import intergamma.stock.api.StockItemPatch;
import intergamma.stock.api.StockItems;
import intergamma.stock.api.StockTotals;
import intergamma.stock.repository.StockItem;

import java.util.Optional;

public interface StockService {

    StockTotals getTotalStock(String productCode);

    StockItems getStockItems(String productCode);

    StockItems addStockItems(String productCode, String storeCode, int quantity);

    void removeStockItem(Long stockItemId);

    Optional<StockItem> updateStockItem(long stockItemId, StockItemPatch patch);

    Iterable<Long> reserveProduct(String productCode);

    boolean reserveStockItem(Long stockItemId);
}
