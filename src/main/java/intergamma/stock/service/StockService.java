package intergamma.stock.service;

import intergamma.stock.domain.StockItemPatch;
import intergamma.stock.domain.StockItems;
import intergamma.stock.domain.StockTotals;
import intergamma.stock.domain.StockItem;

import java.util.Optional;

public interface StockService {

    StockTotals getTotalStock(String productCode);

    StockItems getStockItems(String productCode);

    StockItems addStockItems(String productCode, String storeCode, int quantity);

    void removeStockItem(Long stockItemId);

    Optional<StockItem> updateStockItem(long stockItemId, StockItemPatch patch);

    void revokeStaleReservations();
}
