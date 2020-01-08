package intergamma.stock.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Embedded;
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

    public boolean unreserve() {
        if (reservationTimestamp != null) {
            reservationTimestamp = null;
            return true;
        }
        return false;
    }

    public boolean reserve() {
        if (reservationTimestamp == null) {
            reservationTimestamp = LocalDateTime.now();
            return true;
        }
        return false;
    }
}
