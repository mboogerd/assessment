package intergamma.stock.domain;

import lombok.Value;

@Value
public class StockIncrement {
    String storeCode;
    int quantity;
}
