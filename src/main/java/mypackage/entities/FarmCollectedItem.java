package mypackage.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "farm_collected_items")
public class FarmCollectedItem implements Serializable {
    @EmbeddedId
    private FarmCollectedItemId id = new FarmCollectedItemId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("farmId")
    @JoinColumn(name = "farm_id")
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "collected_at", insertable = false, updatable = false)
    private LocalDateTime collectedAt;

    public FarmCollectedItem () {};

    public FarmCollectedItemId getId() {
        return id;
    }

    public void setId(FarmCollectedItemId id) {
        this.id = id;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public LocalDateTime getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(LocalDateTime collectedAt) {
        this.collectedAt = collectedAt;
    }
}
