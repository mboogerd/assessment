package intergamma.stock.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import intergamma.stock.api.StockItemPatch;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * This represents a unique instance of a particular `product`
 */
@Entity
@Data
public class StockItem {

    @Id
    @GeneratedValue
    Long id;
    String productCode;
    String store;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    LocalDateTime reservationTimestamp;

    public StockItem() {}

    public StockItem(String productCode) {
        this.productCode = productCode;
    }

    public StockItem(String productCode, String storeCode) {
        this.productCode = productCode;
        this.store = storeCode;
    }

    public boolean isReserved() {
        return reservationTimestamp != null;
    }

    public void setReservationState(boolean newReservationState) {
        if(newReservationState && isReserved())
            throw new IllegalStateException("Cannot make a double reservation for a single StockItem");

        if(newReservationState)
            reservationTimestamp = LocalDateTime.now();
        else
            reservationTimestamp = null;
    }

    public StockItem apply(StockItemPatch patch) {
        if(patch.getReserved() != null)
            setReservationState(patch.getReserved());

        if(patch.getStoreCode() != null)
            store = patch.getStoreCode();

        return this;
    }
}
