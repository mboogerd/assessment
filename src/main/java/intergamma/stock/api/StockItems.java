package intergamma.stock.api;

import intergamma.stock.repository.StockItem;
import lombok.Value;

import static java.util.Collections.EMPTY_LIST;

@Value
public class StockItems {
    Iterable<StockItem> stockItems;

    public StockItems() {
        this(EMPTY_LIST);
    }

    public StockItems(Iterable<StockItem> stockItems) {
        this.stockItems = stockItems;
    }
}
