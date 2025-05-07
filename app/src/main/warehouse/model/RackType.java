package warehouse.model;

public enum RackType {
    SMALL(0.5, 1.0, 0.5, 0),  // Для маленьких товаров
    MEDIUM(1.0, 2.0, 1.0, 1), // Для средних товаров
    LARGE(2.0, 3.0, 2.0, 2);  // Для больших товаров

    private final double maxWidth;
    private final double maxHeight;
    private final double maxDepth;
    private final double id;

    RackType(double maxWidth, double maxHeight, double maxDepth, int id) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.maxDepth = maxDepth;
        this.id = id;
    }

    public boolean canStore(Product product) {
        return product.getWidth() <= maxWidth &&
               product.getHeight() <= maxHeight &&
               product.getDepth() <= maxDepth;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public double getMaxDepth() {
        return maxDepth;
    }
    
    public double getId() {
        return id;
    }
}