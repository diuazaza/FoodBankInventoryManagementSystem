
/**
 * Represents a non-perishable food item with shelf-life and packaging info.
 * These items never "expire" in the same sense but track packaging condition.
 * 
 * MILESTONE 2 ENHANCEMENTS:
 * - Added manufacturing date tracking
 * - Added estimated value calculation
 * - Added packaging condition assessment
 * - Enhanced toString with more details
 */
public class NonPerishableItem extends FoodItem {
    private int shelfLifeMonths;
    private String packagingInfo;
    private PackagingCondition condition;
    private double estimatedValuePerUnit;

    public enum PackagingCondition {
        EXCELLENT("Excellent - Sealed and undamaged"),
        GOOD("Good - Minor wear but sealed"),
        FAIR("Fair - Some damage but usable"),
        POOR("Poor - Significant damage"),
        DAMAGED("Damaged - Not suitable for distribution");

        private final String description;
        PackagingCondition(String description) { this.description = description; }
        public String getDescription() { return description; }
    }

    public NonPerishableItem(String itemId, String name, int quantity, 
                            int shelfLifeMonths, String packagingInfo, FoodCategory category) {
        super(itemId, name, quantity, category);
        if (shelfLifeMonths <= 0) throw new IllegalArgumentException("Shelf life must be positive.");
        this.shelfLifeMonths = shelfLifeMonths;
        this.packagingInfo = packagingInfo;
        this.condition = PackagingCondition.GOOD; // default
        this.estimatedValuePerUnit = 1.50; // default value
    }

    // Legacy constructor for backward compatibility
    public NonPerishableItem(String itemId, String name, int quantity, 
                            int shelfLifeMonths, String packagingInfo) {
        this(itemId, name, quantity, shelfLifeMonths, packagingInfo, FoodCategory.CANNED);
    }

    // Getters
    public int getShelfLifeMonths() { return shelfLifeMonths; }
    public String getPackagingInfo() { return packagingInfo; }
    public PackagingCondition getCondition() { return condition; }

    // Setters
    public void setCondition(PackagingCondition condition) { 
        if (condition != null) {
            this.condition = condition; 
        }
    }
    
    public void setEstimatedValuePerUnit(double value) {
        if (value >= 0) {
            this.estimatedValuePerUnit = value;
        }
    }

    /**
     * Non-perishable items are never considered expired in the standard sense.
     * However, damaged packaging makes them unsuitable for distribution.
     */
    @Override
    public boolean isExpired() {
        return condition == PackagingCondition.DAMAGED;
    }

    /**
     * Returns true if the packaging condition is concerning.
     */
    public boolean needsInspection() {
        return condition == PackagingCondition.FAIR || condition == PackagingCondition.POOR;
    }

    /**
     * Returns the estimated total value of this item.
     */
    @Override
    public double getEstimatedValue() {
        double baseValue = getQuantity() * estimatedValuePerUnit;
        // Reduce value based on condition
        switch (condition) {
            case EXCELLENT: return baseValue;
            case GOOD: return baseValue * 0.95;
            case FAIR: return baseValue * 0.80;
            case POOR: return baseValue * 0.60;
            case DAMAGED: return 0.0;
            default: return baseValue;
        }
    }

    @Override
    public String toString() {
        String conditionWarning = needsInspection() ? " [NEEDS INSPECTION]" : "";
        if (condition == PackagingCondition.DAMAGED) {
            conditionWarning = " [DAMAGED - DO NOT DISTRIBUTE]";
        }

        return String.format("[%s] %s (%s) | Qty: %d | Shelf Life: %d mo | " +
                           "Packaging: %s | Condition: %s%s | Source: %s",
            getItemId(), getName(), getCategory().getDisplayName(), 
            getQuantity(), shelfLifeMonths, packagingInfo, 
            condition.name(), conditionWarning, getDonationSource());
    }
}
