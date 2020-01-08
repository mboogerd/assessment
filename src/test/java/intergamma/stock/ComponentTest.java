package intergamma.stock;

import intergamma.stock.api.StockIncrement;
import intergamma.stock.api.StockItems;
import intergamma.stock.api.StockTotals;
import intergamma.stock.repository.StockItem;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComponentTest {
    @Autowired
    TestRestTemplate restTemplate;

    private final String mockProductCode = "mock-product";
    private final String mockStoreCode = "mock-store";

    @Test
    void StockTotalsShouldBeAvailableForMockProduct() {
        ResponseEntity<StockTotals> stockEntity = getStockTotals(mockProductCode);
        assert stockEntity.getStatusCode() == HttpStatus.OK;
        StockTotals stockTotals = stockEntity.getBody();
        assert stockTotals.getAvailable() == 0;
        assert stockTotals.getReserved() == 0;
    }

    @Test
    void StockItemsShouldBeEmptyForMockProduct() {
        ResponseEntity<StockItems> stockEntity = getStockItems(mockProductCode);
        assert stockEntity.getStatusCode() == HttpStatus.OK;
        assert !stockEntity.getBody().getStockItems().iterator().hasNext();
    }

    @Test
    void AddedStockIsAvailable() {
        String mockProductCode = this.mockProductCode + "AddedStockTest";
        int quantity = 10;

        StockIncrement stockIncrement = new StockIncrement(mockStoreCode, quantity);
        ResponseEntity<Void> response = postStockIncrement(mockProductCode, stockIncrement);
        assert response.getStatusCode() == HttpStatus.OK;

        Collection<StockItem> stockItems = getStockItemsCollection(mockProductCode);
        assert stockItems.size() == quantity;

        StockTotals stockTotals = getStockTotalsBody(mockProductCode);
        assert stockTotals.getAvailable() == quantity;
        assert stockTotals.getReserved() == 0;
    }

    @Test
    void RemovedStockIsUnavailable() {
        // Given a product with 1 available stock item
        String mockProductCode = this.mockProductCode + "RemovedStockTest";
        StockIncrement stockIncrement = new StockIncrement(mockStoreCode, 1);
        ResponseEntity<Void> response = postStockIncrement(mockProductCode, stockIncrement);

        // When we retrieve the id of the stock item
        ResponseEntity<StockItems> stockEntity = getStockItems(mockProductCode);
        Collection<StockItem> stockItems = IterableUtil.toCollection(stockEntity.getBody().getStockItems());
        Long stockItemId = stockItems.stream().findFirst().get().getId();

        // And we remove that stock item by its id
        ResponseEntity<Void> removal = deleteStockItem(stockItemId);

        // Then the removal should have been successful
        assert removal.getStatusCode() == HttpStatus.OK;

        // And the product no longer available
        Collection<StockItem> newStockItems = getStockItemsCollection(mockProductCode);
        assert newStockItems.size() == 0;

        StockTotals stockTotals = getStockTotalsBody(mockProductCode);
        assert stockTotals.getAvailable() == 0;
        assert stockTotals.getReserved() == 0;
    }


    @Test
    void ReservedStockCannotBeRemoved() {

    }

    @Test
    void ReservingProductsWorks() {

    }

    /* API utility methods */
    private ResponseEntity<StockTotals> getStockTotals(String productCode) {
        return restTemplate.getForEntity("/product/" + productCode, StockTotals.class);
    }
    private StockTotals getStockTotalsBody(String productCode) {
        return getStockTotals(productCode).getBody();
    }

    private ResponseEntity<StockItems> getStockItems(String productCode) {
        return restTemplate.getForEntity("/stockitem?product=" + productCode, StockItems.class);
    }
    private Collection<StockItem> getStockItemsCollection(String productCode) {
        StockItems stockItems = getStockItems(productCode).getBody();
        return IterableUtil.toCollection(stockItems.getStockItems());
    }

    private ResponseEntity<Void> postStockIncrement(String productCode, StockIncrement stockIncrement) {
        return restTemplate.postForEntity("/product/" + productCode, stockIncrement, Void.class);
    }

    private ResponseEntity<Void> deleteStockItem(Long stockItemId) {
        return restTemplate.exchange("/stockitem/" + stockItemId, HttpMethod.DELETE, null, Void.class);
    }
}
