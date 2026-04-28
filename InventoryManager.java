
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the food bank's inventory of FoodItems.
 * Provides methods to add, remove, and query items, as well as expiry alerts.
 * 
 * MILESTONE 2 ENHANCEMENTS:
 * - Added distribution tracking with DistributionRecord
 * - Added search and filter capabilities
 * - Added inventory statistics and reporting
 * - Added low stock alerts
 * - Added category-based queries
 * - Added batch operations for efficiency
 */
public class InventoryManager {
    private Map<String, FoodItem> inventory;
    private List<DistributionRecord> distributionHistory;
    private int lowStockThreshold;

    public InventoryManager() {
        this.inventory = new HashMap<>();
        this.distributionHistory = new ArrayList<>();
        this.lowStockThreshold = 10; // default
    }

    public void setLowStockThreshold(int threshold) {
        if (threshold >= 0) {
            this.lowStockThreshold = threshold;
        }
    }

    /**
     * Adds a new FoodItem to the inventory.
     * If an item with the same ID already exists, its quantity is incremented.
     *
     * @param item the FoodItem to add
     * @param requestingUser the user performing the action
     * @throws SecurityException if the user lacks inventory update permission
     */
    public void addItem(FoodItem item, User requestingUser) {
        checkInventoryPermission(requestingUser);
        if (inventory.containsKey(item.getItemId())) {
            inventory.get(item.getItemId()).updateQuantity(item.getQuantity());
            System.out.println("Updated existing item: " + item.getItemId()
                + " | New Qty: " + inventory.get(item.getItemId()).getQuantity());
        } else {
            inventory.put(item.getItemId(), item);
            System.out.println("Added new item: " + item);
        }
    }

    /**
     * Adds multiple items at once (batch operation).
     */
    public void addItems(List<FoodItem> items, User requestingUser) {
        checkInventoryPermission(requestingUser);
        for (FoodItem item : items) {
            addItem(item, requestingUser);
        }
    }

    /**
     * Removes a specified quantity of a food item from the inventory.
     *
     * @param itemId          the ID of the item to remove stock from
     * @param quantity        the quantity to remove
     * @param requestingUser  the user performing the action
     * @return true if removal succeeded, false if item not found
     * @throws SecurityException        if user lacks permission
     * @throws IllegalArgumentException if there is insufficient stock
     */
    public boolean removeItem(String itemId, int quantity, User requestingUser) {
        checkInventoryPermission(requestingUser);
        FoodItem item = inventory.get(itemId);
        if (item == null) {
            System.out.println("ERROR: Item not found: " + itemId);
            return false;
        }
        item.updateQuantity(-quantity);
        System.out.println("Removed " + quantity + " of '" + item.getName()
            + "' | Remaining: " + item.getQuantity());
        return true;
    }

    /**
     * Distributes an item and records the transaction.
     */
    public boolean distributeItem(String itemId, int quantity, String recipientInfo, 
                                 User requestingUser) {
        if (removeItem(itemId, quantity, requestingUser)) {
            FoodItem item = inventory.get(itemId);
            DistributionRecord record = new DistributionRecord(
                itemId, item.getName(), quantity, recipientInfo, requestingUser.getName()
            );
            distributionHistory.add(record);
            System.out.println("Distribution recorded: " + record.getRecordId());
            return true;
        }
        return false;
    }

    /**
     * Deletes an item record entirely from the inventory.
     * Only Employees (who canDeleteRecords) may call this.
     */
    public boolean deleteItem(String itemId, User requestingUser) {
        if (!requestingUser.canDeleteRecords()) {
            throw new SecurityException("User '" + requestingUser.getName()
                + "' (" + requestingUser.getRole() + ") is not authorized to delete records.");
        }
        if (!inventory.containsKey(itemId)) {
            System.out.println("ERROR: Item not found for deletion: " + itemId);
            return false;
        }
        FoodItem removed = inventory.remove(itemId);
        System.out.println("Deleted item: " + removed.getName());
        return true;
    }

    /**
     * Returns a list of all PerishableItems that are currently expired.
     */
    public List<PerishableItem> getExpiryAlerts() {
        List<PerishableItem> alerts = new ArrayList<>();
        for (FoodItem item : inventory.values()) {
            if (item instanceof PerishableItem && item.isExpired()) {
                alerts.add((PerishableItem) item);
            }
        }
        return alerts;
    }

    /**
     * NEW: Returns items expiring within specified days.
     */
    public List<PerishableItem> getExpiringSoonAlerts(int daysThreshold) {
        List<PerishableItem> alerts = new ArrayList<>();
        for (FoodItem item : inventory.values()) {
            if (item instanceof PerishableItem) {
                PerishableItem p = (PerishableItem) item;
                if (p.isExpiringSoon(daysThreshold) && !p.isExpired()) {
                    alerts.add(p);
                }
            }
        }
        // Sort by days until expiry (most urgent first)
        alerts.sort((a, b) -> Long.compare(a.getDaysUntilExpiry(), b.getDaysUntilExpiry()));
        return alerts;
    }

    /**
     * NEW: Returns items with low stock.
     */
    public List<FoodItem> getLowStockAlerts() {
        return inventory.values().stream()
            .filter(item -> item.getQuantity() <= lowStockThreshold)
            .collect(Collectors.toList());
    }

    /**
     * NEW: Returns items by category.
     */
    public List<FoodItem> getItemsByCategory(FoodItem.FoodCategory category) {
        return inventory.values().stream()
            .filter(item -> item.getCategory() == category)
            .collect(Collectors.toList());
    }

    /**
     * NEW: Search items by name (case-insensitive partial match).
     */
    public List<FoodItem> searchByName(String searchTerm) {
        String lowerSearch = searchTerm.toLowerCase();
        return inventory.values().stream()
            .filter(item -> item.getName().toLowerCase().contains(lowerSearch))
            .collect(Collectors.toList());
    }

    /**
     * NEW: Get items sorted by distribution priority.
     */
    public List<FoodItem> getItemsByPriority() {
        List<FoodItem> items = new ArrayList<>(inventory.values());
        items.sort((a, b) -> Integer.compare(b.getDistributionPriority(), a.getDistributionPriority()));
        return items;
    }

    /**
     * NEW: Returns damaged non-perishable items.
     */
    public List<NonPerishableItem> getDamagedItems() {
        List<NonPerishableItem> damaged = new ArrayList<>();
        for (FoodItem item : inventory.values()) {
            if (item instanceof NonPerishableItem) {
                NonPerishableItem np = (NonPerishableItem) item;
                if (np.getCondition() == NonPerishableItem.PackagingCondition.DAMAGED ||
                    np.getCondition() == NonPerishableItem.PackagingCondition.POOR) {
                    damaged.add(np);
                }
            }
        }
        return damaged;
    }

    /**
     * Returns all items in the inventory.
     */
    public List<FoodItem> getAllItems() {
        return new ArrayList<>(inventory.values());
    }

    /**
     * Looks up an item by its ID.
     */
    public FoodItem getItem(String itemId) {
        return inventory.get(itemId);
    }

    /**
     * NEW: Get distribution history.
     */
    public List<DistributionRecord> getDistributionHistory() {
        return new ArrayList<>(distributionHistory);
    }

    /**
     * NEW: Get distributions for a specific item.
     */
    public List<DistributionRecord> getDistributionsByItem(String itemId) {
        return distributionHistory.stream()
            .filter(d -> d.getItemId().equals(itemId))
            .collect(Collectors.toList());
    }

    /**
     * NEW: Calculate total inventory value.
     */
    public double getTotalInventoryValue() {
        return inventory.values().stream()
            .mapToDouble(FoodItem::getEstimatedValue)
            .sum();
    }

    /**
     * NEW: Get inventory statistics.
     */
    public InventoryStatistics getStatistics() {
        return new InventoryStatistics(this);
    }

    /** Prints a formatted report of the entire inventory to stdout. */
    public void printInventoryReport() {
        System.out.println("\n===== INVENTORY REPORT =====");
        if (inventory.isEmpty()) {
            System.out.println("  (No items in inventory)");
        } else {
            System.out.println("  Total Items: " + inventory.size());
            System.out.println("  Total Value: $" + String.format("%.2f", getTotalInventoryValue()));
            System.out.println();
            for (FoodItem item : inventory.values()) {
                System.out.println("  " + item);
            }
        }
        System.out.println("============================\n");
    }

    /**
     * NEW: Prints detailed alerts report.
     */
    public void printAlertsReport() {
        System.out.println("\n===== ALERTS REPORT =====");
        
        List<PerishableItem> expired = getExpiryAlerts();
        System.out.println("EXPIRED ITEMS: " + expired.size());
        for (PerishableItem item : expired) {
            System.out.println("  ⚠ " + item);
        }
        
        List<PerishableItem> expiringSoon = getExpiringSoonAlerts(7);
        System.out.println("\nEXPIRING SOON (7 days): " + expiringSoon.size());
        for (PerishableItem item : expiringSoon) {
            System.out.println("  ⏰ " + item);
        }
        
        List<FoodItem> lowStock = getLowStockAlerts();
        System.out.println("\nLOW STOCK: " + lowStock.size());
        for (FoodItem item : lowStock) {
            System.out.println("  📦 " + item);
        }
        
        List<NonPerishableItem> damaged = getDamagedItems();
        System.out.println("\nDAMAGED ITEMS: " + damaged.size());
        for (NonPerishableItem item : damaged) {
            System.out.println("  💥 " + item);
        }
        
        System.out.println("========================\n");
    }

    /**
     * NEW: Prints distribution history report.
     */
    public void printDistributionReport() {
        System.out.println("\n===== DISTRIBUTION REPORT =====");
        System.out.println("Total Distributions: " + distributionHistory.size());
        int totalQuantity = distributionHistory.stream()
            .mapToInt(DistributionRecord::getQuantityDistributed)
            .sum();
        System.out.println("Total Items Distributed: " + totalQuantity);
        System.out.println();
        
        if (distributionHistory.isEmpty()) {
            System.out.println("  (No distributions recorded)");
        } else {
            for (DistributionRecord record : distributionHistory) {
                System.out.println("  " + record);
            }
        }
        System.out.println("==============================\n");
    }

    // ---- Private Helpers ----

    private void checkInventoryPermission(User user) {
        if (!user.canUpdateInventory()) {
            throw new SecurityException("User '" + user.getName()
                + "' (" + user.getRole() + ") does not have permission to update inventory.");
        }
    }

    /**
     * NEW: Inner class for inventory statistics.
     */
    public static class InventoryStatistics {
        private int totalItems;
        private int perishableCount;
        private int nonPerishableCount;
        private int expiredCount;
        private int lowStockCount;
        private double totalValue;
        private Map<FoodItem.FoodCategory, Integer> categoryBreakdown;

        InventoryStatistics(InventoryManager manager) {
            this.totalItems = manager.inventory.size();
            this.totalValue = manager.getTotalInventoryValue();
            this.expiredCount = manager.getExpiryAlerts().size();
            this.lowStockCount = manager.getLowStockAlerts().size();
            this.categoryBreakdown = new HashMap<>();
            
            for (FoodItem item : manager.inventory.values()) {
                if (item instanceof PerishableItem) {
                    perishableCount++;
                } else if (item instanceof NonPerishableItem) {
                    nonPerishableCount++;
                }
                
                categoryBreakdown.merge(item.getCategory(), 1, Integer::sum);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== INVENTORY STATISTICS ===\n");
            sb.append("Total Items: ").append(totalItems).append("\n");
            sb.append("  - Perishable: ").append(perishableCount).append("\n");
            sb.append("  - Non-Perishable: ").append(nonPerishableCount).append("\n");
            sb.append("Total Value: $").append(String.format("%.2f", totalValue)).append("\n");
            sb.append("Expired Items: ").append(expiredCount).append("\n");
            sb.append("Low Stock Items: ").append(lowStockCount).append("\n");
            sb.append("\nCategory Breakdown:\n");
            categoryBreakdown.forEach((cat, count) -> 
                sb.append("  ").append(cat.getDisplayName()).append(": ").append(count).append("\n")
            );
            return sb.toString();
        }
    }
}
