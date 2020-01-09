package intergamma.stock.api;

import intergamma.stock.domain.*;
import intergamma.stock.service.StockService;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @SneakyThrows
    @PatchMapping(value = "/stockitem/{stockItemId}")
    public StockItem patchStockItem(@PathVariable Long stockItemId, @RequestBody StockItemPatch patch) {
        return stockService.updateStockItem(stockItemId, patch)
                .orElseThrow(() -> new NotFoundException("Could not find StockItem"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handle(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> handle(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
