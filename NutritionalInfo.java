
// NEW CLASS: Represents nutritional information for food items.
// Helps food bank staff make informed distribution decisions based on dietary needs.
public class NutritionalInfo {
    private int calories;
    private double protein;    // grams
    private double carbs;      // grams
    private double fat;        // grams
    private double fiber;      // grams
    private double sodium;     // milligrams
    private boolean isVegetarian;
    private boolean isGlutenFree;
    private boolean isDairyFree;

    public NutritionalInfo(int calories, double protein, double carbs, double fat) {
        if (calories < 0 || protein < 0 || carbs < 0 || fat < 0) {
            throw new IllegalArgumentException("Nutritional values cannot be negative.");
        }
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = 0;
        this.sodium = 0;
        this.isVegetarian = false;
        this.isGlutenFree = false;
        this.isDairyFree = false;
    }

    // Getters
    public int getCalories() { return calories; }
    public double getProtein() { return protein; }
    public double getCarbs() { return carbs; }
    public double getFat() { return fat; }
    public double getFiber() { return fiber; }
    public double getSodium() { return sodium; }
    public boolean isVegetarian() { return isVegetarian; }
    public boolean isGlutenFree() { return isGlutenFree; }
    public boolean isDairyFree() { return isDairyFree; }

    // Setters for optional fields
    public void setFiber(double fiber) { this.fiber = Math.max(0, fiber); }
    public void setSodium(double sodium) { this.sodium = Math.max(0, sodium); }
    public void setVegetarian(boolean vegetarian) { this.isVegetarian = vegetarian; }
    public void setGlutenFree(boolean glutenFree) { this.isGlutenFree = glutenFree; }
    public void setDairyFree(boolean dairyFree) { this.isDairyFree = dairyFree; }

    // Returns true if this item is high in protein (>10g per serving).
    public boolean isHighProtein() {
        return protein >= 10.0;
    }

    // Returns true if this item is low sodium (<140mg per serving).
    public boolean isLowSodium() {
        return sodium < 140.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d cal, %.1fg protein, %.1fg carbs, %.1fg fat", 
            calories, protein, carbs, fat));
        
        if (fiber > 0) sb.append(String.format(", %.1fg fiber", fiber));
        if (sodium > 0) sb.append(String.format(", %.0fmg sodium", sodium));
        
        if (isVegetarian) sb.append(", Vegetarian");
        if (isGlutenFree) sb.append(", Gluten-Free");
        if (isDairyFree) sb.append(", Dairy-Free");
        
        return sb.toString();
    }
}
