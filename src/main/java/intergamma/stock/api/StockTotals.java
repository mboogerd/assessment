package intergamma.stock.api;

import lombok.Value;

@Value
public class StockTotals {
    long available;
    long reserved;
}
