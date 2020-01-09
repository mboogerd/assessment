package intergamma.stock;

import intergamma.stock.domain.StockIncrement;
import intergamma.stock.domain.StockItemPatch;
import intergamma.stock.domain.StockItems;
import intergamma.stock.domain.StockTotals;
import intergamma.stock.domain.StockItem;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComponentTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final String mockProductCode = "mock-product";
    private final String mockStoreCode = "mock-store";

    @Test
    void StockTotalsShouldBeAvailableForUnavailableProduct() {
        ResponseEntity<StockTotals> stockEntity = getStockTotals(mockProductCode);
        assert stockEntity.getStatusCode() == HttpStatus.OK;
        StockTotals stockTotals = stockEntity.getBody();
        assert stockTotals.getAvailable() == 0;
        assert stockTotals.getReserved() == 0;
    }

    @Test
    void StockItemsShouldBeEmptyForUnavailableProduct() {
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
        postStockIncrement(mockProductCode, new StockIncrement(mockStoreCode, 1));

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
    void CannotReserveNonExistentStockItem() {
        // When we patch a non existent product
        StockItemPatch stockItemPatch = new StockItemPatch();
        ResponseEntity<StockItem> stockItemEntity = patchStockItem(Long.MAX_VALUE, stockItemPatch);
        // Then it should throw a not found
        assert stockItemEntity.getStatusCode() == HttpStatus.NOT_FOUND;
    }

    @Test
    void ReservingProductsWorks() {
        // Given a product with 1 available stock item
        String mockProductCode = this.mockProductCode + "ReservingProducts";
        postStockIncrement(mockProductCode, new StockIncrement(mockStoreCode, 1));

        // When we retrieve the id of the stock item
        ResponseEntity<StockItems> stockEntity = getStockItems(mockProductCode);
        Collection<StockItem> stockItems = IterableUtil.toCollection(stockEntity.getBody().getStockItems());
        Long stockItemId = stockItems.stream().findFirst().get().getId();

        // And we reserve that item
        StockItemPatch stockItemPatch = new StockItemPatch();
        stockItemPatch.setReserved(true);
        ResponseEntity<StockItem> stockItemEntity = patchStockItem(stockItemId, stockItemPatch);

        // Then the item should appear to be reserved
        assert stockItemEntity.getStatusCode() == HttpStatus.OK;
        assert stockItemEntity.getBody().isReserved();
    }

    @Test
    void DoubleReservationDisallowed() {
        // Given a product with 1 available stock item
        String mockProductCode = this.mockProductCode + "DoubleReservation";
        postStockIncrement(mockProductCode, new StockIncrement(mockStoreCode, 1));

        // When we retrieve the id of the stock item
        ResponseEntity<StockItems> stockEntity = getStockItems(mockProductCode);
        Collection<StockItem> stockItems = IterableUtil.toCollection(stockEntity.getBody().getStockItems());
        Long stockItemId = stockItems.stream().findFirst().get().getId();

        // And we reserve that item
        StockItemPatch stockItemPatch = new StockItemPatch();
        stockItemPatch.setReserved(true);
        patchStockItem(stockItemId, stockItemPatch);

        // And we do so again...
        ResponseEntity<StockItem> stockItemEntityDuplicate = patchStockItem(stockItemId, stockItemPatch);

        // Then the item should appear to be reserved
        assert stockItemEntityDuplicate.getStatusCode() == HttpStatus.CONFLICT;
    }

    @Test
    void ReservedStockIsEventuallyCleaned() {
        // Given a product with 1 available stock item
        String mockProductCode = this.mockProductCode + "PruneReservations";
        postStockIncrement(mockProductCode, new StockIncrement(mockStoreCode, 1));

        // When we retrieve the id of the stock item
        ResponseEntity<StockItems> stockEntity = getStockItems(mockProductCode);
        Collection<StockItem> stockItems = IterableUtil.toCollection(stockEntity.getBody().getStockItems());
        Long stockItemId = stockItems.stream().findFirst().get().getId();

        // And we reserve that item
        StockItemPatch stockItemPatch = new StockItemPatch();
        stockItemPatch.setReserved(true);
        patchStockItem(stockItemId, stockItemPatch);

        // And we then wait a while (sufficient for the given cleanup frequency/validity in test scope)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            fail("Thread interrupted");
        }

        // Then the stock should be available again
        StockTotals stockTotals = getStockTotalsBody(mockProductCode);
        assert stockTotals.getAvailable() == 1;
        assert stockTotals.getReserved() == 0;
    }

    /* API client utility methods */
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

    private ResponseEntity<StockItem> patchStockItem(Long stockItemId, StockItemPatch stockItemPatch) {
        return restTemplate.exchange("/stockitem/" + stockItemId, HttpMethod.PATCH, new HttpEntity<>(stockItemPatch), StockItem.class);
    }

    /**
     * This is required to prevent:
     * "Invalid HTTP method: PATCH; nested exception is java.net.ProtocolException: Invalid HTTP method: PATCH"
     *
     * Caused by: java.net.ProtocolException: Invalid HTTP method: PATCH
     * 	at java.base/java.net.HttpURLConnection.setRequestMethod(HttpURLConnection.java:487)
     * 	at java.base/sun.net.www.protocol.http.HttpURLConnection.setRequestMethod(HttpURLConnection.java:569)
     *
     * Yes... in Java 11, we are still not allowed to use PATCH as a method, which has been standardized TEN years ago...
     *
     */
    @TestConfiguration
    static class RestTemplateConfiguration {
        @Bean
        public RestTemplate restTemplate() {
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setReadTimeout(600000);
            requestFactory.setConnectTimeout(600000);
            return new RestTemplate(requestFactory);
        }
    }
}