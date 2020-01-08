package intergamma.mboogerd;

public interface StockService {

    Stock getStockLevel(String productCode);

    Iterable<Long> reserveProductItems(String productCode);
}
