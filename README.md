# Food Bank Inventory Management System - Milestone 2

A comprehensive Java-based inventory management system for food banks with advanced features for tracking donations, managing inventory, and ensuring food safety.

## What's New in Milestone 2

### Major Features Added
1. **Food Categories** - 11 predefined categories for better organization
2. **Nutritional Information** - Track calories, protein, dietary restrictions
3. **Distribution Tracking** - Complete audit trail of all distributions
4. **Advanced Search** - Search by name, category, priority
5. **Smart Alerts** - Low stock, expiring soon, damaged items
6. **Donor Tier System** - Bronze, Silver, Gold, Platinum recognition
7. **Volunteer Hours** - Track and recognize volunteer contributions
8. **Statistics Dashboard** - Comprehensive inventory metrics
9. **Value Tracking** - Financial impact measurement
10. **Batch Operations** - Efficient bulk item management

## Quick Start

### Compilation
```bash
javac *.java
```

### Run the Main Demo
```bash
java Main
```

### Run All Tests
```bash
java TestCases
```

## System Architecture

### Core Classes

#### **FoodItem** (Enhanced Abstract Base)
- Categories: Dairy, Produce, Protein, Grains, Canned, etc.
- Nutritional information support
- Donation source tracking
- Estimated value calculation
- Distribution priority scoring

#### **PerishableItem** (Enhanced)
- Days until expiry calculation
- Expiring soon alerts (configurable threshold)
- Storage temperature tracking
- Enhanced display with urgency indicators

#### **NonPerishableItem** (Enhanced)
- 5-tier packaging condition system
- Quality control flags
- Value adjustment based on condition
- Inspection requirements

#### **NutritionalInfo** (NEW)
- Macronutrients: calories, protein, carbs, fat
- Micronutrients: fiber, sodium
- Dietary flags: vegetarian, gluten-free, dairy-free
- Helper methods: isHighProtein(), isLowSodium()

#### **DistributionRecord** (NEW)
- Complete distribution tracking
- Auto-generated unique IDs
- Timestamp recording
- Recipient information
- Notes field

#### **InventoryManager** (Enhanced)
- Distribution tracking
- Advanced search and filter
- Multiple alert systems
- Statistics generation
- Batch operations
- Comprehensive reporting

### User Classes

#### **Employee** (Enhanced)
- Full system access
- Department tracking
- Hire date and employee number
- Can perform all operations

#### **Volunteer** (Enhanced)
- Inventory update access
- Hours tracking
- Skills/certifications
- Start date recording

#### **Donor** (Enhanced)
- 4-tier recognition system
- Complete donation history
- Contact information
- Average donation calculation

## Key Features

### Inventory Management
```java
// Add items with enhanced details
PerishableItem milk = new PerishableItem("P001", "Whole Milk", 20, 
    LocalDate.now().plusDays(5), FoodCategory.DAIRY);
milk.setStorageTemp("Refrigerated (2-4°C)");
milk.setEstimatedValuePerUnit(4.50);
milk.setDonationSource("Local Supermarket");

// Add nutritional information
NutritionalInfo nutrition = new NutritionalInfo(150, 8, 12, 8);
nutrition.setVegetarian(false);
milk.setNutritionalInfo(nutrition);

manager.addItem(milk, employee);
```

### Distribution Tracking
```java
// Distribute items with full audit trail
manager.distributeItem("P001", 5, "Johnson Family (4 members)", employee);

// View distribution history
List<DistributionRecord> history = manager.getDistributionHistory();
```

### Search & Filter
```java
// Search by name (case-insensitive)
List<FoodItem> results = manager.searchByName("rice");

// Filter by category
List<FoodItem> dairy = manager.getItemsByCategory(FoodCategory.DAIRY);

// Get items by priority (most urgent first)
List<FoodItem> priority = manager.getItemsByPriority();
```

### Alerts & Monitoring
```java
// Get expired items
List<PerishableItem> expired = manager.getExpiryAlerts();

// Get items expiring within 7 days
List<PerishableItem> expiringSoon = manager.getExpiringSoonAlerts(7);

// Get low stock items
List<FoodItem> lowStock = manager.getLowStockAlerts();

// Get damaged items
List<NonPerishableItem> damaged = manager.getDamagedItems();

// Print comprehensive alerts report
manager.printAlertsReport();
```

### Statistics & Reporting
```java
// Get inventory statistics
InventoryStatistics stats = manager.getStatistics();
System.out.println(stats);

// Get total value
double totalValue = manager.getTotalInventoryValue();

// Print various reports
manager.printInventoryReport();
manager.printDistributionReport();
manager.printAlertsReport();
```

### Donor Management
```java
// Record donations with tier progression
Donor donor = new Donor("D001", "Community Market");
donor.recordDonation(250.00, "Weekly food donation");
donor.recordDonation(500.00, "Holiday donation");

// Check tier (automatically updated)
DonorTier tier = donor.getTier(); // SILVER after $750

// View donation history
List<DonationEntry> history = donor.getDonationHistory();
double avgDonation = donor.getAverageDonation();
```

### Volunteer Management
```java
// Track volunteer hours
Volunteer volunteer = new Volunteer("V001", "Bob Martinez");
volunteer.setSkills("Food handling, Forklift certified");
volunteer.logHours(4.5);
volunteer.logHours(3.0);

// View total hours
double totalHours = volunteer.getTotalHoursWorked(); // 7.5
```

## Testing

### Test Coverage
- **Total Tests:** 41
- **Original Tests:** 16 (all passing)
- **New Tests:** 25 (all passing)
- **Pass Rate:** 100%

### Test Categories
1. **Core Functionality** (16 tests)
   - Expiry checking
   - Inventory operations
   - Access control
   - Data validation

2. **New Features** (25 tests)
   - Category management
   - Nutritional info
   - Distribution tracking
   - Search and filter
   - Alert systems
   - Donor tier system
   - Volunteer tracking
   - Statistics generation

### Running Tests
```bash
java TestCases
```

Expected output:
```
==============================================
  Food Bank System - Enhanced Test Suite      
  Milestone 2 - Testing New Features          
==============================================

  ✓ PASS: Perishable item with future expiry should NOT be expired
  ✓ PASS: checkFreshness returns true for today
  ...
  ✓ PASS: Donation source should be Local Supermarket

==============================================
  Results: 41 PASSED | 0 FAILED
==============================================
```

## Sample Output

### Main Demo Output
The main demo showcases:
- User creation with enhanced features
- Donor activity and tier progression
- Volunteer hour tracking
- Inventory management with categories
- Distribution tracking
- Search and filter operations
- Comprehensive alerts
- Statistics dashboard
- Access control validation

### Statistics Dashboard Example
```
=== INVENTORY STATISTICS ===
Total Items: 7
  - Perishable: 3
  - Non-Perishable: 4
Total Value: $287.25
Expired Items: 1
Low Stock Items: 2

Category Breakdown:
  Dairy & Eggs: 2
  Grains & Cereals: 2
  Canned Goods: 2
  Fruits & Vegetables: 1
```

## Access Control

### Permission Matrix

**Feature**:
View Inventory: Employee and Volunteers can, Donors cannot.
Add Items: Employees and volunteers can, Donots cannot.

| Feature           | Employee | Volunteer | Donor |
|-------------------|----------|-----------|-------|
| View Inventory    |    ✅    |    ✅    |   ❌  |
| Add Items         |    ✅    |    ✅    |   ❌  |
| Remove Items      |    ✅    |    ✅    |   ❌  |
| Distribute Items  |    ✅    |    ✅    |   ❌  |
| Delete Records    |    ✅    |    ❌    |   ❌  |
| View Donor Records|    ✅    |    ❌    |   ❌  |
| Record Donations  |    ❌    |    ❌    |   ✅  |

## Priority System

Items are automatically prioritized for distribution:

 Priority | Condition | Score Range 

| Highest, Expired, 100
| Urgent, Expiring in 1-3 days, 90 
| High, Expiring in 4-7 days, 70 
| Medium, Expiring in 8-14 days, 50 
| Normal, Expiring in 15+ days, 30 
| Low, Non-perishable, 20

*Adjustments: +10 for high stock (>100 units), -10 for low stock (<10 units)*

## Donor Tier System

| Tier | Range | Benefits 

|Bronze, $0 - $499, Recognition 
|Silver, $500 - $1,499, Enhanced recognition
|Gold, $1,500 - $4,999, Premium recognition
|Platinum, $5,000+, VIP recognition


### Design Principles
-  **Encapsulation:** All fields private with proper accessors
-  **Inheritance:** Proper use of abstract classes
-  **Polymorphism:** Method overriding throughout
-  **Single Responsibility:** Each class has one purpose
-  **Open/Closed:** Extended without modifying original code


## Backward Compatibility

All original Milestone 1 functionality is preserved:
- Legacy constructors still work
- Original methods unchanged
- No breaking changes
- Easy migration path

## Future Enhancements

Potential additions for future milestones:
- Database persistence (PostgreSQL/MySQL)
- REST API with Spring Boot
- Web dashboard with React
- Barcode scanning integration
- Email notification system
- Mobile app (iOS/Android)
- Multi-location support
- Advanced analytics and forecasting

##  Use Cases

### Daily Operations
1. Employee receives donation → adds items with categories
2. System alerts about expiring items
3. Employee distributes urgent items first
4. Distribution automatically recorded

### Weekly Review
1. Manager runs alerts report
2. Identifies low stock items
3. Reviews distribution history
4. Generates statistics for board meeting

### Donor Relations
1. Donor makes contribution
2. System records and tracks total
3. Tier automatically updated
4. Recognition level determined

### Quality Control
1. System flags damaged items
2. Staff inspects flagged items
3. Items removed or condition updated
4. Safety maintained
