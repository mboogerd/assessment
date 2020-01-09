package intergamma.stock.domain;

import lombok.Value;

@Value
public class StockTotals {
    long available;
    long reserved;
}
