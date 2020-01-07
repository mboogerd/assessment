package intragamma.mboogerd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class ProductItemStockRepositoryTest {

    private TestEntityManager entityManager;
    private ProductItemRepository repository;

    @Autowired
    public ProductItemStockRepositoryTest(
            TestEntityManager entityManager,
            ProductItemRepository repository) {

        this.entityManager = entityManager;
        this.repository = repository;
    }

    @Test
    void unknownProductImpliesNoStock() {
        assert repository.countByProductIdAndIsReservedFalse(1) == 0;
    }

    @Test
    void unknownProductImpliesNoReservations() {
        assert repository.countByProductIdAndIsReservedTrue(1) == 0;
    }

    @Test
    void nonReservedProductItemIsAvailable() {
        Long productId = 1L;
        ProductItem productItem = new ProductItem(productId);
        entityManager.persist(productItem);
        entityManager.flush();

        assert repository.countByProductIdAndIsReservedFalse(productId) == 1;
        assert repository.countByProductIdAndIsReservedTrue(productId) == 0;
    }

    @Test
    void reservedProductItemIsUnavailable() {
        Long productId = 1L;
        ProductItem productItem = new ProductItem(productId);
        productItem.setIsReserved(true);
        entityManager.persist(productItem);
        entityManager.flush();

        assert repository.countByProductIdAndIsReservedFalse(productId) == 0;
        assert repository.countByProductIdAndIsReservedTrue(productId) == 1;
    }

    @Test
    void reservingAProductMakesItUnavailable() {
        // Given a single ProductItem for a given productId
        Long productId = 1L;
        ProductItem productItem = new ProductItem(productId);
        entityManager.persist(productItem);
        entityManager.flush();

        // When we reserve the ProductItem
        assert repository.reserveProductItem(productItem.getId()) == 1;

        // Then no product items should be available for the given productId
        assert repository.countByProductIdAndIsReservedFalse(productId) == 0;

    }

}
