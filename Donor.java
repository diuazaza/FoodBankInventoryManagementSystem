
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Donor: read-only contribution record access, no inventory or record management.
 * 
 * MILESTONE 2 ENHANCEMENTS:
 * - Added donation history tracking
 * - Added contact information
 * - Added donor preferences
 * - Added recognition/tier system
 */
public class Donor extends User {
    private double totalDonationValue;
    private List<DonationEntry> donationHistory;
    private String contactEmail;
    private String contactPhone;
    private DonorTier tier;

    public enum DonorTier {
        BRONZE("Bronze", 0, 499),
        SILVER("Silver", 500, 1499),
        GOLD("Gold", 1500, 4999),
        PLATINUM("Platinum", 5000, Double.MAX_VALUE);

        private final String name;
        private final double minValue;
        private final double maxValue;

        DonorTier(String name, double minValue, double maxValue) {
            this.name = name;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public String getName() { return name; }
        public static DonorTier getTierForValue(double value) {
            for (DonorTier tier : values()) {
                if (value >= tier.minValue && value <= tier.maxValue) {
                    return tier;
                }
            }
            return BRONZE;
        }
    }

    public Donor(String userId, String name) {
        super(userId, name, "Donor");
        this.totalDonationValue = 0.0;
        this.donationHistory = new ArrayList<>();
        this.tier = DonorTier.BRONZE;
    }

    public double getTotalDonationValue() { return totalDonationValue; }
    public DonorTier getTier() { return tier; }
    public List<DonationEntry> getDonationHistory() { return new ArrayList<>(donationHistory); }
    
    public void setContactEmail(String email) { this.contactEmail = email; }
    public void setContactPhone(String phone) { this.contactPhone = phone; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }

    public void recordDonation(double value) {
        recordDonation(value, "General donation");
    }

    public void recordDonation(double value, String description) {
        if (value <= 0) throw new IllegalArgumentException("Donation value must be positive.");
        
        this.totalDonationValue += value;
        this.donationHistory.add(new DonationEntry(value, description));
        
        // Update tier based on new total
        this.tier = DonorTier.getTierForValue(totalDonationValue);
        
        System.out.println("Donation recorded: $" + String.format("%.2f", value) + 
                         " from " + getName() + " | Total: $" + 
                         String.format("%.2f", totalDonationValue) + 
                         " | Tier: " + tier.getName());
    }

    public int getDonationCount() {
        return donationHistory.size();
    }

    public double getAverageDonation() {
        if (donationHistory.isEmpty()) return 0.0;
        return totalDonationValue / donationHistory.size();
    }

    @Override public boolean canDeleteRecords()    { return false; }
    @Override public boolean canUpdateInventory()  { return false; }
    @Override public boolean canViewDonorRecords() { return false; }

    @Override
    public String toString() {
        return String.format("User[%s] %s (Donor - %s Tier) | Total Donations: $%.2f | Count: %d",
            getUserId(), getName(), tier.getName(), totalDonationValue, getDonationCount());
    }

    // Inner class to track individual donation entries.
    public static class DonationEntry {
        private double amount;
        private String description;
        private LocalDate date;

        public DonationEntry(double amount, String description) {
            this.amount = amount;
            this.description = description;
            this.date = LocalDate.now();
        }

        public double getAmount() { return amount; }
        public String getDescription() { return description; }
        public LocalDate getDate() { return date; }

        @Override
        public String toString() {
            return String.format("%s: $%.2f - %s", date, amount, description);
        }
    }
}
