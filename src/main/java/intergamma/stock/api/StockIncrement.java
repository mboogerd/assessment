package intergamma.stock.api;

import lombok.Value;

@Value
public class StockIncrement {
    String storeCode;
    int quantity;
}
