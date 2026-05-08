package mypackage.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FarmCollectedItemId implements Serializable {
    @Column(name = "farm_id")
    private Long farmId;

    @Column(name = "item_id")
    private Long itemId;

    public FarmCollectedItemId() {}

    public FarmCollectedItemId(Long farmId, Long itemId) {
        this.farmId = farmId;
        this.itemId = itemId;
    }

    public Long getFarmId() {
        return farmId;
    }
    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public Long getItemId() {
        return itemId;
    }
    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FarmCollectedItemId that = (FarmCollectedItemId) o;
        return Objects.equals(farmId, that.farmId) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(farmId, itemId);
    }
}
