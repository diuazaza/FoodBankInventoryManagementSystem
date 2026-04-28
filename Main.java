
import java.time.LocalDate;
import java.util.List;

/**
 * Main class demonstrating the Food Bank Inventory Management System - Milestone 2.
 * Showcases both original and newly added features.
 */
public class Main {
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("  Food Bank Inventory Management System     ");
        System.out.println("  MILESTONE 2 - Enhanced Features Demo      ");
        System.out.println("============================================\n");

        // ---- Create Users with Enhanced Features ----
        System.out.println("=== USER MANAGEMENT ===\n");
        
        Employee emp  = new Employee("E001", "Alice Chen", "Warehouse Operations");
        emp.setHireDate(LocalDate.of(2022, 3, 15));
        
        Volunteer vol = new Volunteer("V001", "Bob Martinez");
        vol.setSkills("Food handling, Forklift certified");
        vol.setStartDate(LocalDate.of(2023, 9, 1));
        
        Donor donor   = new Donor("D001", "Carol's Market");
        donor.setContactEmail("carol@market.com");
        donor.setContactPhone("555-0123");

        System.out.println(emp);
        System.out.println(vol);
        System.out.println(donor);
        System.out.println();

        // ---- Donor Activity ----
        System.out.println("=== DONOR ACTIVITY ===\n");
        donor.recordDonation(250.00, "Weekly food donation");
        donor.recordDonation(500.00, "Holiday special donation");
        donor.recordDonation(1000.00, "Anniversary celebration donation");
        System.out.println("\n" + donor);
        System.out.println("Donation History:");
        for (Donor.DonationEntry entry : donor.getDonationHistory()) {
            System.out.println("  " + entry);
        }
        System.out.println();

        // ---- Volunteer Activity ----
        System.out.println("=== VOLUNTEER ACTIVITY ===\n");
        vol.logHours(4.0);
        vol.logHours(3.5);
        vol.logHours(5.0);
        System.out.println(vol);
        System.out.println();

        // ---- Create Enhanced Inventory Manager ----
        InventoryManager manager = new InventoryManager();
        manager.setLowStockThreshold(15);

        // ---- Add Items with Categories and Nutritional Info ----
        System.out.println("=== ADDING INVENTORY WITH ENHANCED FEATURES ===\n");
        
        // Perishable items with detailed info
        PerishableItem milk = new PerishableItem("P001", "Whole Milk", 20, 
            LocalDate.now().plusDays(5), FoodItem.FoodCategory.DAIRY);
        milk.setStorageTemp("Refrigerated (2-4°C)");
        milk.setEstimatedValuePerUnit(4.50);
        milk.setDonationSource("Carol's Market");
        NutritionalInfo milkNutrition = new NutritionalInfo(150, 8, 12, 8);
        milkNutrition.setDairyFree(false);
        milk.setNutritionalInfo(milkNutrition);
        
        PerishableItem yogurt = new PerishableItem("P002", "Greek Yogurt", 15, 
            LocalDate.now().minusDays(2), FoodItem.FoodCategory.DAIRY);
        yogurt.setStorageTemp("Refrigerated (2-4°C)");
        yogurt.setEstimatedValuePerUnit(5.00);
        yogurt.setDonationSource("Dairy Farms Co-op");
        
        PerishableItem eggs = new PerishableItem("P003", "Large Eggs (Dozen)", 30, 
            LocalDate.now().plusDays(14), FoodItem.FoodCategory.PROTEIN);
        eggs.setStorageTemp("Refrigerated");
        eggs.setEstimatedValuePerUnit(3.99);
        eggs.setDonationSource("Carol's Market");
        NutritionalInfo eggsNutrition = new NutritionalInfo(70, 6, 0, 5);
        eggsNutrition.setVegetarian(true);
        eggs.setNutritionalInfo(eggsNutrition);
        
        PerishableItem apples = new PerishableItem("P004", "Gala Apples", 8, 
            LocalDate.now().plusDays(3), FoodItem.FoodCategory.PRODUCE);
        apples.setStorageTemp("Cool, dry place");
        apples.setEstimatedValuePerUnit(1.50);
        apples.setDonationSource("Local Orchards");
        
        // Non-perishable items with packaging conditions
        NonPerishableItem rice = new NonPerishableItem("N001", "Brown Rice", 50, 24, 
            "Sealed plastic bag", FoodItem.FoodCategory.GRAINS);
        rice.setCondition(NonPerishableItem.PackagingCondition.EXCELLENT);
        rice.setEstimatedValuePerUnit(3.25);
        rice.setDonationSource("Grain Suppliers Inc");
        
        NonPerishableItem soup = new NonPerishableItem("N002", "Tomato Soup (Can)", 100, 36, 
            "Steel can", FoodItem.FoodCategory.CANNED);
        soup.setCondition(NonPerishableItem.PackagingCondition.GOOD);
        soup.setEstimatedValuePerUnit(1.99);
        soup.setDonationSource("Carol's Market");
        
        NonPerishableItem pasta = new NonPerishableItem("N003", "Whole Wheat Pasta", 40, 18, 
            "Cardboard box", FoodItem.FoodCategory.GRAINS);
        pasta.setCondition(NonPerishableItem.PackagingCondition.GOOD);
        pasta.setEstimatedValuePerUnit(2.50);
        pasta.setDonationSource("Pasta Factory");
        
        NonPerishableItem beans = new NonPerishableItem("N004", "Black Beans (Can)", 12, 36, 
            "Steel can - minor dent", FoodItem.FoodCategory.CANNED);
        beans.setCondition(NonPerishableItem.PackagingCondition.FAIR);
        beans.setEstimatedValuePerUnit(1.50);
        beans.setDonationSource("Wholesale Mart");
        NutritionalInfo beansNutrition = new NutritionalInfo(110, 7, 20, 0);
        beansNutrition.setFiber(6);
        beansNutrition.setVegetarian(true);
        beans.setNutritionalInfo(beansNutrition);

        // Add all items
        manager.addItem(milk, emp);
        manager.addItem(yogurt, emp);
        manager.addItem(eggs, emp);
        manager.addItem(apples, emp);
        manager.addItem(rice, emp);
        manager.addItem(soup, emp);
        manager.addItem(pasta, vol); // Volunteer can also add
        manager.addItem(beans, vol);

        manager.printInventoryReport();

        // ---- Distribution Demo ----
        System.out.println("=== ITEM DISTRIBUTION ===\n");
        manager.distributeItem("P001", 5, "Johnson Family (4 members)", emp);
        manager.distributeItem("N001", 10, "Community Center", emp);
        manager.distributeItem("N002", 20, "Homeless Shelter", vol);
        System.out.println();

        // ---- Search and Filter Features ----
        System.out.println("=== SEARCH & FILTER FEATURES ===\n");
        
        System.out.println("--- Search by Name: 'rice' ---");
        List<FoodItem> searchResults = manager.searchByName("rice");
        for (FoodItem item : searchResults) {
            System.out.println("  " + item);
        }
        System.out.println();
        
        System.out.println("--- Items by Category: DAIRY ---");
        List<FoodItem> dairyItems = manager.getItemsByCategory(FoodItem.FoodCategory.DAIRY);
        for (FoodItem item : dairyItems) {
            System.out.println("  " + item);
        }
        System.out.println();
        
        System.out.println("--- Items by Priority (Most Urgent First) ---");
        List<FoodItem> priorityItems = manager.getItemsByPriority();
        for (int i = 0; i < Math.min(5, priorityItems.size()); i++) {
            FoodItem item = priorityItems.get(i);
            System.out.printf("  Priority %d: %s\n", item.getDistributionPriority(), item);
        }
        System.out.println();

        // ---- Enhanced Alerts System ----
        manager.printAlertsReport();

        // ---- Statistics Dashboard ----
        System.out.println("=== INVENTORY STATISTICS ===\n");
        System.out.println(manager.getStatistics());

        // ---- Distribution Report ----
        manager.printDistributionReport();

        // ---- Access Control Demo (same as before) ----
        System.out.println("=== ACCESS CONTROL VALIDATION ===\n");
        
        System.out.println("--- Donor attempts inventory update ---");
        try {
            manager.addItem(new NonPerishableItem("N005", "Peanut Butter", 10, 12, 
                "Jar", FoodItem.FoodCategory.CONDIMENTS), donor);
        } catch (SecurityException e) {
            System.out.println("✗ ACCESS DENIED: " + e.getMessage());
        }
        System.out.println();

        System.out.println("--- Volunteer attempts to delete a record ---");
        try {
            manager.deleteItem("N002", vol);
        } catch (SecurityException e) {
            System.out.println("✗ ACCESS DENIED: " + e.getMessage());
        }
        System.out.println();

        System.out.println("--- Employee successfully deletes expired yogurt ---");
        manager.deleteItem("P002", emp);
        System.out.println();

        // ---- Data Integrity Demo ----
        System.out.println("=== DATA INTEGRITY VALIDATION ===\n");
        try {
            manager.removeItem("P003", 999, emp);
        } catch (IllegalArgumentException e) {
            System.out.println("✗ DATA INTEGRITY: " + e.getMessage());
        }
        System.out.println();

        // ---- Final Reports ----
        System.out.println("=== FINAL SYSTEM STATE ===\n");
        manager.printInventoryReport();

        System.out.println("=== TOTAL INVENTORY VALUE ===");
        System.out.printf("Total estimated value: $%.2f\n", manager.getTotalInventoryValue());
        System.out.println();

        // ---- Show Detailed Item Info ----
        System.out.println("=== DETAILED ITEM INFORMATION ===\n");
        FoodItem detailItem = manager.getItem("P001");
        if (detailItem != null) {
            System.out.println(detailItem.toDetailedString());
        }
        System.out.println();

        System.out.println("============================================");
        System.out.println("  Demo Complete - Milestone 2 Features      ");
        System.out.println("  Successfully Demonstrated!                ");
        System.out.println("============================================");
    }
}
