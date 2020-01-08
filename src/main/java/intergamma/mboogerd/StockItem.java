package intergamma.mboogerd;

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
    final String productCode;
    LocalDateTime reservationTimestamp;

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
