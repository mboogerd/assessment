package intergamma.stock.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StockItemPatch {
    String storeCode;
    Boolean reserved;
}
