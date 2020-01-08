package intergamma.stock.api;

import intergamma.stock.service.StockService;
import org.springframework.web.bind.annotation.*;

@RestController
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping(value = "/product/{productCode}")
    public StockTotals getStockLevel(@PathVariable String productCode) {
        return stockService.getTotalStock(productCode);
    }

    @GetMapping(value = "/stockitem/{productCode}")
    public StockItems getStockItems(@PathVariable String productCode) {
        return stockService.getStockItems(productCode);
    }

    @PostMapping(value = "/product/{productCode}")
    public void addStock(@PathVariable String productCode, @RequestBody StockIncrement stockIncrement) {
        stockService.addStockItems(productCode, stockIncrement.getStoreCode(), stockIncrement.getQuantity());
    }

    @DeleteMapping(value = "/stockitem/{stockItemId}")
    public void deleteStockItem(@PathVariable Long stockItemId) {
        stockService.removeStockItem(stockItemId);
    }
}
