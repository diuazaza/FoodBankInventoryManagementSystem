
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * NEW CLASS: Records information about food distributions to recipients.
 * Provides audit trail and helps track inventory movements.
 */
public class DistributionRecord {
    private static int nextRecordId = 1;
    
    private String recordId;
    private String itemId;
    private String itemName;
    private int quantityDistributed;
    private LocalDateTime distributionTime;
    private String recipientInfo;
    private String distributedBy;
    private String notes;

    public DistributionRecord(String itemId, String itemName, int quantity, 
                            String recipientInfo, String distributedBy) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Distribution quantity must be positive.");
        }
        
        this.recordId = String.format("DIST-%04d", nextRecordId++);
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantityDistributed = quantity;
        this.distributionTime = LocalDateTime.now();
        this.recipientInfo = recipientInfo;
        this.distributedBy = distributedBy;
        this.notes = "";
    }

    // Getters
    public String getRecordId() { return recordId; }
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getQuantityDistributed() { return quantityDistributed; }
    public LocalDateTime getDistributionTime() { return distributionTime; }
    public String getRecipientInfo() { return recipientInfo; }
    public String getDistributedBy() { return distributedBy; }
    public String getNotes() { return notes; }

    public void setNotes(String notes) {
        if (notes != null) {
            this.notes = notes;
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("[%s] %s | Item: %s (%s) | Qty: %d | To: %s | By: %s | Time: %s",
            recordId, itemName, itemId, itemName, quantityDistributed, 
            recipientInfo, distributedBy, distributionTime.format(formatter));
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder(toString());
        if (!notes.isEmpty()) {
            sb.append("\n    Notes: ").append(notes);
        }
        return sb.toString();
    }
}
