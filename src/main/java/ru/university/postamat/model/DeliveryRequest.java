package ru.university.postamat.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Заявка на доставку в постамат — основная сущность системы.
 * userFullName и postamatAddress — служебные поля ТОЛЬКО для отображения,
 * заполняются при чтении из БД через JOIN, в базе не хранятся.
 */
public class DeliveryRequest {

    private Long id;
    private String trackingNumber;
    private Long userId;
    private Long postamatId;
    private String itemDescription;
    private BigDecimal weightKg;
    private DeliverySize size;
    private DeliveryStatus status;
    private String pickupCode;          // выдаётся, когда посылка попадает в постамат
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;  // null, пока посылка не выдана

    // служебные поля для отображения
    private String userFullName;
    private String postamatAddress;

    public DeliveryRequest() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getPostamatId() { return postamatId; }
    public void setPostamatId(Long postamatId) { this.postamatId = postamatId; }
    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public DeliverySize getSize() { return size; }
    public void setSize(DeliverySize size) { this.size = size; }
    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
    public String getPickupCode() { return pickupCode; }
    public void setPickupCode(String pickupCode) { this.pickupCode = pickupCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    public String getPostamatAddress() { return postamatAddress; }
    public void setPostamatAddress(String postamatAddress) { this.postamatAddress = postamatAddress; }

    @Override
    public String toString() {
        return String.format("%s: %s (%.2f кг, %s) — %s",
                trackingNumber, itemDescription, weightKg, size, status.getDisplayName());
    }
}