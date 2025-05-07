package warehouse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import warehouse.model.Product;
import warehouse.model.Rack;
import warehouse.model.RackType;
import warehouse.model.Product.Builder;
import warehouse.repository.DatabaseManager;

public class DatabaseManagerTest {
    @Test
    void checkFreeRack() throws SQLException{
        DatabaseManager db = new DatabaseManager();
        var test = db.getFreeRacks();
        for (Rack rack : test) {
            System.out.printf("ID: %s   TYPE: %s    ZONE_ID: %s     CNT: %s\n", rack.getId(), rack.getType(), rack.getZoneId(), rack.getCount());
        }
    }

    @Test
    void checkRackType(){
        Builder builder = new Builder();
                              builder.setDepth(0.2);
                              builder.setWidth(0.2);
                              builder.setHeight(0.2);
                              builder.setName("test");
                              builder.setId("213");
                              builder.build();
        Product test = new Product(builder);
        assertTrue(test.getSize().toString() instanceof String);
        System.out.println(test.getSize().toString());
    }

    @Test
    void checkBalanceProduct() throws SQLException{
        DatabaseManager dbManager = new DatabaseManager();
        var list = dbManager.balanceProduct();
        assertEquals(2, list.size());
        for (Product product : list) {
            System.out.printf("ID: %s   name: %s\n", product.getId(), product.getName());
        }
    }
}
