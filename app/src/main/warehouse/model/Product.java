package warehouse.model;

public class Product {
    private String id;
    private String name;
    private double width;
    private double height;
    private double depth;

    private String rackId;
    private RackType size;

    public static class Builder{
        private String id;
        private String name;
        private double width;
        private double height;
        private double depth;
        private String rackId;

        public void setId(String id) {
            this.id = id;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setWidth(double width) {
            this.width = width;
        }
        public void setHeight(double height) {
            this.height = height;
        }
        public void setDepth(double depth) {
            this.depth = depth;
        }
        public void setRackId(String rackId) {
            this.rackId = rackId;
        }
        public Product build(){
            return new Product(this);
        }
    }
 
    public Product(Builder builder) {
        this.id = builder.id;
        setName(builder.name);
        setWidth(builder.width);
        setHeight(builder.height);
        setDepth(builder.depth);
        size = calculateSize();
        this.rackId = builder.rackId;
    }

    public Product(String id, String name, double height, double width, double depth, String rackId){
        this.id = id;
        setName(name);
        setDepth(depth);
        setHeight(height);
        setWidth(width);
        this.size = calculateSize();
        this.rackId = rackId;
    }

    private RackType calculateSize(){
        RackType[] arrayTypes = new RackType[] {RackType.SMALL, RackType.MEDIUM, RackType.LARGE};
        for (RackType sizes : arrayTypes) {
            if (width < sizes.getMaxWidth() && height < sizes.getMaxHeight() && depth < sizes.getMaxDepth()){
                return sizes;
            }
        }
        throw new IllegalArgumentException("Размеры товара превышают размеры стеллажей!");
    }

    public void setName(String name){
        if (name.isEmpty()){
            throw new IllegalArgumentException("Имя не может быть пустым!");
        }
        this.name = name;
    }

    public void setWidth(double width) {
        if (width <= 0){
            throw new IllegalArgumentException("Неверное значение ширины!");
        }
        this.width = width;
    }

    public void setHeight(double height) {
        if (height <= 0){
            throw new IllegalArgumentException("Неверное значение высоты!");
        }
        this.height = height;
    }

    public void setDepth(double depth) {
        if (depth <= 0){
            throw new IllegalArgumentException("Неверное значение глубины!");
        }
        this.depth = depth;
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

    public RackType getSize() {
        return size;
    }


    public void setRackId(String rackId) {
        this.rackId = rackId;
    }
}
