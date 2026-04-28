
/**
 * Abstract base class representing a food item in the food bank inventory.
 * All food items have a name, unique ID, quantity, category, and optional nutritional info.
 * 
 * MILESTONE 2 ENHANCEMENTS:
 * - Added FoodCategory enum for better organization
 * - Added nutritional information tracking
 * - Added donation source tracking
 * - Enhanced toString with more details
 */
public abstract class FoodItem {
    private String itemId;
    private String name;
    private int quantity;
    private FoodCategory category;
    private NutritionalInfo nutritionalInfo;
    private String donationSource;

    public enum FoodCategory {
        DAIRY("Dairy & Eggs"),
        PRODUCE("Fruits & Vegetables"),
        PROTEIN("Meat & Protein"),
        GRAINS("Grains & Cereals"),
        CANNED("Canned Goods"),
        BEVERAGES("Beverages"),
        SNACKS("Snacks & Treats"),
        CONDIMENTS("Condiments & Sauces"),
        FROZEN("Frozen Foods"),
        BAKERY("Bakery Items"),
        OTHER("Other");

        private final String displayName;
        FoodCategory(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    public FoodItem(String itemId, String name, int quantity, FoodCategory category) {
        if (itemId == null || itemId.isEmpty()) throw new IllegalArgumentException("Item ID cannot be null or empty.");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name cannot be null or empty.");
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
        if (category == null) throw new IllegalArgumentException("Category cannot be null.");
        
        this.itemId = itemId;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.nutritionalInfo = null;
        this.donationSource = "Unknown";
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public FoodCategory getCategory() { return category; }
    public NutritionalInfo getNutritionalInfo() { return nutritionalInfo; }
    public String getDonationSource() { return donationSource; }

    // Setters
    public void setNutritionalInfo(NutritionalInfo info) { this.nutritionalInfo = info; }
    public void setDonationSource(String source) {
        if (source != null && !source.isEmpty()) {
            this.donationSource = source;
        }
    }

    /**
     * Adjusts the current quantity by the given amount (positive to add, negative to subtract).
     * Throws IllegalArgumentException if the result would be negative.
     */
    public void updateQuantity(int amount) {
        if (this.quantity + amount < 0) {
            throw new IllegalArgumentException(
                "Insufficient stock for '" + name + "'. Available: " + quantity + ", Requested: " + (-amount));
        }
        this.quantity += amount;
    }

    /**
     * Abstract method: returns true if the item is expired or otherwise unsafe.
     */
    public abstract boolean isExpired();

    /**
     * Returns the estimated value of this item for reporting purposes.
     */
    public abstract double getEstimatedValue();

    /**
     * Returns a priority score for distribution (higher = more urgent to distribute).
     * Based on expiry status and quantity.
     */
    public int getDistributionPriority() {
        int priority = 0;
        if (isExpired()) {
            priority = 100; // Highest priority - needs immediate attention
        } else if (this instanceof PerishableItem) {
            PerishableItem p = (PerishableItem) this;
            long daysUntilExpiry = p.getDaysUntilExpiry();
            if (daysUntilExpiry <= 3) priority = 90;
            else if (daysUntilExpiry <= 7) priority = 70;
            else if (daysUntilExpiry <= 14) priority = 50;
            else priority = 30;
        } else {
            priority = 20; // Non-perishables have lower priority
        }
        
        // Adjust based on stock level
        if (quantity > 100) priority += 10;
        else if (quantity < 10) priority -= 10;
        
        return priority;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Qty: %d | Expired: %s | Source: %s", 
            itemId, name, category.getDisplayName(), quantity, isExpired(), donationSource);
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder(toString());
        if (nutritionalInfo != null) {
            sb.append("\n    Nutrition: ").append(nutritionalInfo);
        }
        return sb.toString();
    }
}
