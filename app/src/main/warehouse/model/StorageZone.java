package warehouse.model;

public class StorageZone {
    private String id;
    private String name;


    public StorageZone(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}