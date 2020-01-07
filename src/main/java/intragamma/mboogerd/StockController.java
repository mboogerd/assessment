package intragamma.mboogerd;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping(value = "/stock/{productId}")
    public Stock getStockLevel(@PathVariable long productId) {
        return stockService.getStockLevel(productId);
    }
}
