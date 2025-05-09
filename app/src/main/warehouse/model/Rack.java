package warehouse.model;

public class Rack {
    private String id;
    private String zoneId;
    private RackType type;
    public Integer count;

    public Rack(String id, RackType type, String zoneId) {
        this.id = id;
        this.type = type;
        this.zoneId = zoneId;
    }

    public String getId() {
        return id;
    }

    public RackType getType() {
        return type;
    }

    public String getZoneId() {
        return zoneId;
    }
}
