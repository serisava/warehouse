package warehouse.model;

public class Product {
    private String id;
    private String name;
    private double width;
    private double height;
    private double depth;
    private String rackId;

    public Product(String id, String name, double width, double height, double depth, String rackId) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.rackId = rackId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDepth() {
        return depth;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }
}
