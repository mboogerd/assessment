package intergamma.stock.api;

import intergamma.stock.repository.StockItem;
import intergamma.stock.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @PostMapping(value = "/product/{productCode}")
    public StockItems addStock(@PathVariable String productCode, @RequestBody StockIncrement stockIncrement) {
        return stockService.addStockItems(productCode, stockIncrement.getStoreCode(), stockIncrement.getQuantity());
    }

    @GetMapping(value = "/stockitem")
    public StockItems getStockItems(@RequestParam(name = "product") String productCode) {
        return stockService.getStockItems(productCode);
    }

    @DeleteMapping(value = "/stockitem/{stockItemId}")
    public void deleteStockItem(@PathVariable Long stockItemId) {
        stockService.removeStockItem(stockItemId);
    }

    @PatchMapping(value = "/stockitem/{stockItemId}")
    public Optional<StockItem> patchStockItem(@PathVariable Long stockItemId, @RequestBody StockItemPatch patch) {
        return stockService.updateStockItem(stockItemId, patch);
    }
}
