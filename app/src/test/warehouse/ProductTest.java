package warehouse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import warehouse.model.Product;
import warehouse.model.RackType;

public class ProductTest {
    
    @Test
    void checkLimits(){
        var test = new Product("0", "test", 0.4, 0.9, 0.4, "0");
        assertEquals(RackType.SMALL, test.getSize());
        test = new Product("0", "test", 0.9, 1, 0.9, "0");
        assertEquals(RackType.MEDIUM, test.getSize());
        test = new Product("0", "test", 1.5, 2.5, 1.5, "0");
        assertEquals(RackType.LARGE, test.getSize());

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("0", "test", 10, 10, 10, "0");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("0", "test", 0, 1, 1, "0");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("0", "test", 1, -1, 1, "0");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("0", "test", 1, 1, -1, "0");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Product("0", "", 1, 1, 1, "0");
        });
    }
}
