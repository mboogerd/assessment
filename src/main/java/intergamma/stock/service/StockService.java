package intergamma.stock.service;

import intergamma.stock.api.StockItems;
import intergamma.stock.api.StockTotals;

public interface StockService {

    StockTotals getTotalStock(String productCode);

    StockItems getStockItems(String productCode);

    void addStockItems(String productCode, String storeCode, int quantity);

    void removeStockItem(Long stockItemId);

    Iterable<Long> reserveProduct(String productCode);

    boolean reserveStockItem(Long stockItemId);
}
