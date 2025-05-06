package warehouse;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import warehouse.model.Rack;
import warehouse.repository.DatabaseManager;

public class DatabaseManagerTest {
    @Test
    void checkFreeRack() throws SQLException{
        DatabaseManager db = new DatabaseManager();
        var test = db.getFreeRacks();
        for (Rack rack : test) {
            System.out.printf("ID: %s   TYPE: %s    ZONE_ID: %s \n", rack.getId(), rack.getType(), rack.getZoneId());
        }
    }
}
