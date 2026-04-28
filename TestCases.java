
import java.time.LocalDate;
import java.util.List;

/**
 * Enhanced test suite for the Food Bank Inventory Management System (Milestone 2).
 * Tests both original functionality and new features.
 */
public class TestCases {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  Food Bank System - Enhanced Test Suite      ");
        System.out.println("  Milestone 2 - Testing New Features          ");
        System.out.println("==============================================\n");

        // Original tests
        testPerishableItemFreshness();
        testPerishableItemExpired();
        testNonPerishableNeverExpires();
        testAddItemIncreasesInventory();
        testAddDuplicateItemMergesQuantity();
        testRemoveItemDecreasesStock();
        testRemoveItemPreventsNegativeStock();
        testRemoveNonExistentItemReturnsFalse();
        testGetExpiryAlertsOnlyExpired();
        testEmployeeCanDelete();
        testVolunteerCannotDelete();
        testDonorCannotUpdateInventory();
        testDonorRecordDonation();
        testDonorCannotRecordNegativeDonation();
        testUserRolePermissions();
        testFoodItemConstructorValidation();

        // NEW TESTS FOR MILESTONE 2
        testFoodCategories();
        testNutritionalInfo();
        testPerishableDaysUntilExpiry();
        testPerishableExpiringSoon();
        testNonPerishablePackagingCondition();
        testItemEstimatedValue();
        testDistributionPriority();
        testDistributionRecording();
        testSearchByName();
        testGetItemsByCategory();
        testLowStockAlerts();
        testExpiringSoonAlerts();
        testDamagedItemsDetection();
        testInventoryStatistics();
        testDonorTierSystem();
        testDonorHistory();
        testVolunteerHoursTracking();
        testEmployeeDepartment();
        testBatchAddItems();
        testDonationSource();

        System.out.println("\n==============================================");
        System.out.printf("  Results: %d PASSED | %d FAILED%n", passed, failed);
        System.out.println("==============================================");
    }

    // -------- Original Test Methods --------

    static void testPerishableItemFreshness() {
        PerishableItem item = new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(3), FoodItem.FoodCategory.DAIRY);
        assertTrue("Perishable item with future expiry should NOT be expired", !item.isExpired());
        assertTrue("checkFreshness returns true for today", item.checkFreshness(LocalDate.now()));
    }

    static void testPerishableItemExpired() {
        PerishableItem item = new PerishableItem("P002", "Old Yogurt", 5, 
            LocalDate.now().minusDays(1), FoodItem.FoodCategory.DAIRY);
        assertTrue("Perishable item with past expiry SHOULD be expired", item.isExpired());
        assertFalse("checkFreshness returns false for yesterday's date", item.checkFreshness(LocalDate.now()));
    }

    static void testNonPerishableNeverExpires() {
        NonPerishableItem item = new NonPerishableItem("N001", "Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS);
        assertFalse("Non-perishable item should never be expired", item.isExpired());
    }

    static void testAddItemIncreasesInventory() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        FoodItem item = new NonPerishableItem("N001", "Soup", 20, 36, "Can", 
            FoodItem.FoodCategory.CANNED);
        mgr.addItem(item, emp);
        assertEquals("Inventory should contain 1 item after add", 1, mgr.getAllItems().size());
        assertEquals("Quantity should be 20", 20, mgr.getItem("N001").getQuantity());
    }

    static void testAddDuplicateItemMergesQuantity() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        mgr.addItem(new NonPerishableItem("N001", "Soup", 20, 36, "Can", 
            FoodItem.FoodCategory.CANNED), emp);
        mgr.addItem(new NonPerishableItem("N001", "Soup", 10, 36, "Can", 
            FoodItem.FoodCategory.CANNED), emp);
        assertEquals("Duplicate add should merge quantity to 30", 30, mgr.getItem("N001").getQuantity());
        assertEquals("Should still have 1 inventory entry", 1, mgr.getAllItems().size());
    }

    static void testRemoveItemDecreasesStock() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        mgr.addItem(new NonPerishableItem("N001", "Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS), emp);
        boolean result = mgr.removeItem("N001", 15, emp);
        assertTrue("removeItem should return true on success", result);
        assertEquals("Remaining stock should be 35", 35, mgr.getItem("N001").getQuantity());
    }

    static void testRemoveItemPreventsNegativeStock() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        mgr.addItem(new NonPerishableItem("N001", "Rice", 10, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS), emp);
        try {
            mgr.removeItem("N001", 999, emp);
            fail("Should have thrown IllegalArgumentException for negative stock");
        } catch (IllegalArgumentException e) {
            pass("IllegalArgumentException thrown when removing more than available stock");
        }
    }

    static void testRemoveNonExistentItemReturnsFalse() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        boolean result = mgr.removeItem("FAKE99", 5, emp);
        assertFalse("removeItem should return false for unknown item ID", result);
    }

    static void testGetExpiryAlertsOnlyExpired() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        mgr.addItem(new PerishableItem("P001", "Fresh Milk", 10, 
            LocalDate.now().plusDays(5), FoodItem.FoodCategory.DAIRY), emp);
        mgr.addItem(new PerishableItem("P002", "Expired Bread", 5, 
            LocalDate.now().minusDays(1), FoodItem.FoodCategory.BAKERY), emp);
        mgr.addItem(new NonPerishableItem("N001", "Beans", 20, 24, "Can", 
            FoodItem.FoodCategory.CANNED), emp);

        List<PerishableItem> alerts = mgr.getExpiryAlerts();
        assertEquals("Only 1 item should trigger expiry alert", 1, alerts.size());
        assertEquals("Expired item should be 'Expired Bread'", "Expired Bread", alerts.get(0).getName());
    }

    static void testEmployeeCanDelete() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        mgr.addItem(new NonPerishableItem("N001", "Soup", 20, 36, "Can", 
            FoodItem.FoodCategory.CANNED), emp);
        boolean result = mgr.deleteItem("N001", emp);
        assertTrue("Employee should be able to delete an item", result);
        assertEquals("Inventory should be empty after deletion", 0, mgr.getAllItems().size());
    }

    static void testVolunteerCannotDelete() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        Volunteer vol = new Volunteer("V001", "Bob");
        mgr.addItem(new NonPerishableItem("N001", "Soup", 20, 36, "Can", 
            FoodItem.FoodCategory.CANNED), emp);
        try {
            mgr.deleteItem("N001", vol);
            fail("Volunteer should NOT be able to delete records");
        } catch (SecurityException e) {
            pass("SecurityException thrown when Volunteer tries to delete");
        }
    }

    static void testDonorCannotUpdateInventory() {
        InventoryManager mgr = new InventoryManager();
        Donor donor = new Donor("D001", "Carol");
        try {
            mgr.addItem(new NonPerishableItem("N001", "Soup", 5, 36, "Can", 
                FoodItem.FoodCategory.CANNED), donor);
            fail("Donor should NOT be able to update inventory");
        } catch (SecurityException e) {
            pass("SecurityException thrown when Donor tries to add inventory");
        }
    }

    static void testDonorRecordDonation() {
        Donor donor = new Donor("D001", "Carol");
        donor.recordDonation(150.0);
        donor.recordDonation(75.50);
        assertEquals("Donor total should be 225.50", 225.50, donor.getTotalDonationValue(), 0.001);
    }

    static void testDonorCannotRecordNegativeDonation() {
        Donor donor = new Donor("D001", "Carol");
        try {
            donor.recordDonation(-50.0);
            fail("Should have thrown IllegalArgumentException for negative donation");
        } catch (IllegalArgumentException e) {
            pass("IllegalArgumentException thrown for negative donation value");
        }
    }

    static void testUserRolePermissions() {
        Employee emp  = new Employee("E001", "Alice");
        Volunteer vol = new Volunteer("V001", "Bob");
        Donor donor   = new Donor("D001", "Carol");

        assertTrue("Employee can update inventory",   emp.canUpdateInventory());
        assertTrue("Employee can delete records",     emp.canDeleteRecords());
        assertTrue("Employee can view donor records", emp.canViewDonorRecords());

        assertTrue ("Volunteer can update inventory",       vol.canUpdateInventory());
        assertFalse("Volunteer cannot delete records",      vol.canDeleteRecords());
        assertFalse("Volunteer cannot view donor records",  vol.canViewDonorRecords());

        assertFalse("Donor cannot update inventory",       donor.canUpdateInventory());
        assertFalse("Donor cannot delete records",         donor.canDeleteRecords());
        assertFalse("Donor cannot view donor records",     donor.canViewDonorRecords());
    }

    static void testFoodItemConstructorValidation() {
        try {
            new NonPerishableItem("", "Rice", 10, 12, "Bag", FoodItem.FoodCategory.GRAINS);
            fail("Empty itemId should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("IllegalArgumentException for empty item ID");
        }
        try {
            new NonPerishableItem("N001", "Rice", -5, 12, "Bag", FoodItem.FoodCategory.GRAINS);
            fail("Negative quantity should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("IllegalArgumentException for negative initial quantity");
        }
        try {
            new PerishableItem("P001", "Milk", 10, null, FoodItem.FoodCategory.DAIRY);
            fail("Null expiry date should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass("IllegalArgumentException for null expiry date");
        }
    }

    // -------- NEW TEST METHODS FOR MILESTONE 2 --------

    static void testFoodCategories() {
        PerishableItem milk = new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(7), FoodItem.FoodCategory.DAIRY);
        assertEquals("Milk should be in DAIRY category", FoodItem.FoodCategory.DAIRY, milk.getCategory());
        
        NonPerishableItem rice = new NonPerishableItem("N001", "Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS);
        assertEquals("Rice should be in GRAINS category", FoodItem.FoodCategory.GRAINS, rice.getCategory());
    }

    static void testNutritionalInfo() {
        NutritionalInfo info = new NutritionalInfo(150, 8.0, 12.0, 5.0);
        assertEquals("Calories should be 150", 150, info.getCalories());
        assertEquals("Protein should be 8.0g", 8.0, info.getProtein(), 0.01);
        
        info.setVegetarian(true);
        assertTrue("Should be marked as vegetarian", info.isVegetarian());
        
        NutritionalInfo highProtein = new NutritionalInfo(200, 15.0, 10.0, 3.0);
        assertTrue("Should be high protein", highProtein.isHighProtein());
    }

    static void testPerishableDaysUntilExpiry() {
        PerishableItem item = new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(5), FoodItem.FoodCategory.DAIRY);
        assertEquals("Should have 5 days until expiry", 5, item.getDaysUntilExpiry());
        
        PerishableItem expired = new PerishableItem("P002", "Old Yogurt", 5, 
            LocalDate.now().minusDays(2), FoodItem.FoodCategory.DAIRY);
        assertEquals("Should have -2 days (expired 2 days ago)", -2, expired.getDaysUntilExpiry());
    }

    static void testPerishableExpiringSoon() {
        PerishableItem soon = new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(3), FoodItem.FoodCategory.DAIRY);
        assertTrue("Item expiring in 3 days should trigger 7-day alert", soon.isExpiringSoon(7));
        assertFalse("Item expiring in 3 days should NOT trigger 2-day alert", soon.isExpiringSoon(2));
    }

    static void testNonPerishablePackagingCondition() {
        NonPerishableItem item = new NonPerishableItem("N001", "Soup", 20, 36, "Can", 
            FoodItem.FoodCategory.CANNED);
        item.setCondition(NonPerishableItem.PackagingCondition.DAMAGED);
        assertTrue("Damaged packaging should make item 'expired'", item.isExpired());
        
        item.setCondition(NonPerishableItem.PackagingCondition.FAIR);
        assertTrue("Fair condition should need inspection", item.needsInspection());
    }

    static void testItemEstimatedValue() {
        PerishableItem perishable = new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(7), FoodItem.FoodCategory.DAIRY);
        perishable.setEstimatedValuePerUnit(3.50);
        assertEquals("10 units at $3.50 each should be $35.00", 35.0, perishable.getEstimatedValue(), 0.01);
        
        NonPerishableItem nonPerish = new NonPerishableItem("N001", "Rice", 20, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS);
        nonPerish.setEstimatedValuePerUnit(2.00);
        nonPerish.setCondition(NonPerishableItem.PackagingCondition.FAIR);
        double expectedValue = 20 * 2.00 * 0.80; // 80% due to FAIR condition
        assertEquals("Fair condition should reduce value to 80%", expectedValue, nonPerish.getEstimatedValue(), 0.01);
    }

    static void testDistributionPriority() {
        PerishableItem urgent = new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(2), FoodItem.FoodCategory.DAIRY);
        PerishableItem lessUrgent = new PerishableItem("P002", "Cheese", 10, 
            LocalDate.now().plusDays(30), FoodItem.FoodCategory.DAIRY);
        
        assertTrue("Item expiring in 2 days should have higher priority than 30 days", 
            urgent.getDistributionPriority() > lessUrgent.getDistributionPriority());
    }

    static void testDistributionRecording() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        mgr.addItem(new NonPerishableItem("N001", "Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS), emp);
        
        boolean result = mgr.distributeItem("N001", 10, "Family of 4", emp);
        assertTrue("Distribution should succeed", result);
        assertEquals("Remaining stock should be 40", 40, mgr.getItem("N001").getQuantity());
        assertEquals("Should have 1 distribution record", 1, mgr.getDistributionHistory().size());
    }

    static void testSearchByName() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        mgr.addItem(new NonPerishableItem("N001", "Brown Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS), emp);
        mgr.addItem(new NonPerishableItem("N002", "White Rice", 30, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS), emp);
        mgr.addItem(new NonPerishableItem("N003", "Beans", 20, 36, "Can", 
            FoodItem.FoodCategory.CANNED), emp);
        
        List<FoodItem> results = mgr.searchByName("rice");
        assertEquals("Should find 2 rice items", 2, results.size());
    }

    static void testGetItemsByCategory() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        mgr.addItem(new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(7), FoodItem.FoodCategory.DAIRY), emp);
        mgr.addItem(new PerishableItem("P002", "Cheese", 5, 
            LocalDate.now().plusDays(14), FoodItem.FoodCategory.DAIRY), emp);
        mgr.addItem(new NonPerishableItem("N001", "Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS), emp);
        
        List<FoodItem> dairy = mgr.getItemsByCategory(FoodItem.FoodCategory.DAIRY);
        assertEquals("Should have 2 dairy items", 2, dairy.size());
    }

    static void testLowStockAlerts() {
        InventoryManager mgr = new InventoryManager();
        mgr.setLowStockThreshold(15);
        Employee emp = new Employee("E001", "Alice");
        
        mgr.addItem(new NonPerishableItem("N001", "Rice", 5, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS), emp);
        mgr.addItem(new NonPerishableItem("N002", "Beans", 50, 24, "Can", 
            FoodItem.FoodCategory.CANNED), emp);
        
        List<FoodItem> lowStock = mgr.getLowStockAlerts();
        assertEquals("Should have 1 low stock item", 1, lowStock.size());
        assertEquals("Low stock item should be Rice", "Rice", lowStock.get(0).getName());
    }

    static void testExpiringSoonAlerts() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        
        mgr.addItem(new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(3), FoodItem.FoodCategory.DAIRY), emp);
        mgr.addItem(new PerishableItem("P002", "Cheese", 5, 
            LocalDate.now().plusDays(30), FoodItem.FoodCategory.DAIRY), emp);
        
        List<PerishableItem> expiringSoon = mgr.getExpiringSoonAlerts(7);
        assertEquals("Should have 1 item expiring soon", 1, expiringSoon.size());
    }

    static void testDamagedItemsDetection() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        
        NonPerishableItem item1 = new NonPerishableItem("N001", "Soup", 20, 36, "Can", 
            FoodItem.FoodCategory.CANNED);
        item1.setCondition(NonPerishableItem.PackagingCondition.DAMAGED);
        mgr.addItem(item1, emp);
        
        NonPerishableItem item2 = new NonPerishableItem("N002", "Beans", 30, 36, "Can", 
            FoodItem.FoodCategory.CANNED);
        item2.setCondition(NonPerishableItem.PackagingCondition.GOOD);
        mgr.addItem(item2, emp);
        
        List<NonPerishableItem> damaged = mgr.getDamagedItems();
        assertEquals("Should have 1 damaged item", 1, damaged.size());
    }

    static void testInventoryStatistics() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        
        mgr.addItem(new PerishableItem("P001", "Milk", 10, 
            LocalDate.now().plusDays(7), FoodItem.FoodCategory.DAIRY), emp);
        mgr.addItem(new NonPerishableItem("N001", "Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS), emp);
        
        InventoryManager.InventoryStatistics stats = mgr.getStatistics();
        assertTrue("Statistics should contain information", stats.toString().contains("Total Items"));
    }

    static void testDonorTierSystem() {
        Donor donor = new Donor("D001", "Carol");
        assertEquals("New donor should be BRONZE tier", Donor.DonorTier.BRONZE, donor.getTier());
        
        donor.recordDonation(600.0);
        assertEquals("$600 donation should upgrade to SILVER", Donor.DonorTier.SILVER, donor.getTier());
        
        donor.recordDonation(1000.0);
        assertEquals("Total $1600 should be GOLD tier", Donor.DonorTier.GOLD, donor.getTier());
    }

    static void testDonorHistory() {
        Donor donor = new Donor("D001", "Carol");
        donor.recordDonation(100.0, "Food drive");
        donor.recordDonation(50.0, "Holiday donation");
        
        assertEquals("Should have 2 donations", 2, donor.getDonationCount());
        assertEquals("Average should be $75", 75.0, donor.getAverageDonation(), 0.01);
    }

    static void testVolunteerHoursTracking() {
        Volunteer vol = new Volunteer("V001", "Bob");
        vol.logHours(4.5);
        vol.logHours(3.0);
        
        assertEquals("Total hours should be 7.5", 7.5, vol.getTotalHoursWorked(), 0.01);
    }

    static void testEmployeeDepartment() {
        Employee emp = new Employee("E001", "Alice", "Warehouse Operations");
        assertEquals("Department should be Warehouse Operations", 
            "Warehouse Operations", emp.getDepartment());
    }

    static void testBatchAddItems() {
        InventoryManager mgr = new InventoryManager();
        Employee emp = new Employee("E001", "Alice");
        
        List<FoodItem> items = new java.util.ArrayList<>();
        items.add(new NonPerishableItem("N001", "Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS));
        items.add(new NonPerishableItem("N002", "Beans", 30, 36, "Can", 
            FoodItem.FoodCategory.CANNED));
        
        mgr.addItems(items, emp);
        assertEquals("Should have 2 items in inventory", 2, mgr.getAllItems().size());
    }

    static void testDonationSource() {
        NonPerishableItem item = new NonPerishableItem("N001", "Rice", 50, 24, "Bag", 
            FoodItem.FoodCategory.GRAINS);
        item.setDonationSource("Local Supermarket");
        assertEquals("Donation source should be Local Supermarket", 
            "Local Supermarket", item.getDonationSource());
    }

    // -------- Assertion Helpers --------

    static void assertTrue(String msg, boolean condition) {
        if (condition) pass(msg); else fail(msg);
    }

    static void assertFalse(String msg, boolean condition) {
        assertTrue(msg, !condition);
    }

    static void assertEquals(String msg, int expected, int actual) {
        if (expected == actual) pass(msg);
        else fail(msg + " [expected=" + expected + ", actual=" + actual + "]");
    }

    static void assertEquals(String msg, long expected, long actual) {
        if (expected == actual) pass(msg);
        else fail(msg + " [expected=" + expected + ", actual=" + actual + "]");
    }

    static void assertEquals(String msg, String expected, String actual) {
        if (expected.equals(actual)) pass(msg);
        else fail(msg + " [expected='" + expected + "', actual='" + actual + "']");
    }

    static void assertEquals(String msg, double expected, double actual, double delta) {
        if (Math.abs(expected - actual) <= delta) pass(msg);
        else fail(msg + " [expected=" + expected + ", actual=" + actual + "]");
    }

    static void assertEquals(String msg, FoodItem.FoodCategory expected, FoodItem.FoodCategory actual) {
        if (expected == actual) pass(msg);
        else fail(msg + " [expected=" + expected + ", actual=" + actual + "]");
    }

    static void assertEquals(String msg, Donor.DonorTier expected, Donor.DonorTier actual) {
        if (expected == actual) pass(msg);
        else fail(msg + " [expected=" + expected + ", actual=" + actual + "]");
    }

    static void pass(String msg) {
        System.out.println("  ✓ PASS: " + msg);
        passed++;
    }

    static void fail(String msg) {
        System.out.println("  ✗ FAIL: " + msg);
        failed++;
    }
}
