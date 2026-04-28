
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a perishable food item that has an expiration date.
 * Extends FoodItem with freshness-checking logic.
 * 
 * MILESTONE 2 ENHANCEMENTS:
 * - Added storage temperature tracking
 * - Added days until expiry calculation
 * - Added estimated value calculation
 * - Added warning threshold for near-expiry items
 */
public class PerishableItem extends FoodItem {
    private LocalDate expiryDate;
    private String storageTemp; // e.g., "Refrigerated", "Frozen", "Room temp"
    private double estimatedValuePerUnit;

    public PerishableItem(String itemId, String name, int quantity, 
                         LocalDate expiryDate, FoodCategory category) {
        super(itemId, name, quantity, category);
        if (expiryDate == null) throw new IllegalArgumentException("Expiry date cannot be null.");
        this.expiryDate = expiryDate;
        this.storageTemp = "Refrigerated"; // default
        this.estimatedValuePerUnit = 2.50; // default value
    }

    // Legacy constructor for backward compatibility
    public PerishableItem(String itemId, String name, int quantity, LocalDate expiryDate) {
        this(itemId, name, quantity, expiryDate, FoodCategory.OTHER);
    }

    // Getters
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getStorageTemp() {
        return storageTemp;
    }

    // Setters
    public void setStorageTemp(String temp) { 
        if (temp != null && !temp.isEmpty()) {
            this.storageTemp = temp; 
        }
    }
    
    public void setEstimatedValuePerUnit(double value) {
        if (value >= 0) {
            this.estimatedValuePerUnit = value;
        }
    }

    /**
     * Compares the given date to the expiry date.
     * Returns true (fresh/safe) if currentDate is before or on the expiry date.
     */
    public boolean checkFreshness(LocalDate currentDate) {
        return !currentDate.isAfter(expiryDate);
    }

    // Returns the number of days until expiry (negative if expired).
    public long getDaysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    // Returns true if the item is expired as of today.
    @Override
    public boolean isExpired() {
        return !checkFreshness(LocalDate.now());
    }

    
    //Returns true if item is expiring within the warning threshold (default 7 days).
    
    public boolean isExpiringSoon() {
        return isExpiringSoon(7);
    }

    
    // Returns true if item is expiring within the specified number of days.
    
    public boolean isExpiringSoon(int daysThreshold) {
        long days = getDaysUntilExpiry();
        return days >= 0 && days <= daysThreshold;
    }

    
    // Returns the estimated total value of this item.
    
    @Override
    public double getEstimatedValue() {
        return getQuantity() * estimatedValuePerUnit;
    }

    @Override
    public String toString() {
        long daysLeft = getDaysUntilExpiry();
        String expiryStatus;
        if (isExpired()) {
            expiryStatus = "EXPIRED (" + Math.abs(daysLeft) + " days ago)";
        } else if (daysLeft <= 3) {
            expiryStatus = "URGENT: " + daysLeft + " days left";
        } else if (daysLeft <= 7) {
            expiryStatus = "Soon: " + daysLeft + " days left";
        } else {
            expiryStatus = daysLeft + " days left";
        }

        return String.format("[%s] %s (%s) | Qty: %d | Expires: %s | %s | Storage: %s | Source: %s",
            getItemId(), getName(), getCategory().getDisplayName(), 
            getQuantity(), expiryDate, expiryStatus, storageTemp, getDonationSource());
    }
}
