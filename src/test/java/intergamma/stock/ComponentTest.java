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
        ResponseEntity<StockTotals> stockEntity = restTemplate.getForEntity("/product/" + mockProductCode, StockTotals.class);
        assert stockEntity.getStatusCode() == HttpStatus.OK;
        StockTotals stockTotals = stockEntity.getBody();
        assert stockTotals.getAvailable() == 0;
        assert stockTotals.getReserved() == 0;
    }

    @Test
    void StockItemsShouldBeEmptyForMockProduct() {
        ResponseEntity<StockItems> stockEntity = restTemplate.getForEntity("/stockitem/test", StockItems.class);
        assert stockEntity.getStatusCode() == HttpStatus.OK;
        assert !stockEntity.getBody().getStockItems().iterator().hasNext();
    }

    @Test
    void AddedStockIsAvailable() {
        String mockProductCode = this.mockProductCode + "AddedStockTest";
        StockIncrement stockIncrement = new StockIncrement(mockStoreCode, 1);
        ResponseEntity<Void> response = restTemplate.postForEntity("/product/" + mockProductCode, stockIncrement, Void.class);
        assert response.getStatusCode() == HttpStatus.OK;

        ResponseEntity<StockItems> stockEntity = restTemplate.getForEntity("/stockitem/" + mockProductCode, StockItems.class);
        assert stockEntity.getStatusCode() == HttpStatus.OK;
        Collection<StockItem> stockItems = IterableUtil.toCollection(stockEntity.getBody().getStockItems());
        assert stockItems.size() == 1;
        StockItem stockItem = stockItems.stream().findFirst().get();
        assert stockItem.getStore().equals(mockStoreCode);

        ResponseEntity<StockTotals> totalsEntity = restTemplate.getForEntity("/product/" + mockProductCode, StockTotals.class);
        assert totalsEntity.getStatusCode() == HttpStatus.OK;
        StockTotals stockTotals = totalsEntity.getBody();
        assert stockTotals.getAvailable() == 1;
        assert stockTotals.getReserved() == 0;
    }
}
