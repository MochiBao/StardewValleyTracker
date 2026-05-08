package mypackage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "farms")
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "farms_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "farm_name", nullable = false, length= 100)
    private String farmName;

    @Column(name = "farm_type", nullable = false, length= 50)
    private String farmType;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FarmCollectedItem> collectedItems;

    public Farm() {};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getFarmType() {
        return farmType;
    }

    public void setFarmType(String farmType) {
        this.farmType = farmType;
    }

    public LocalTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<FarmCollectedItem> getCollectedItems() {
        return collectedItems;
    }

    public void setCollectedItems(List<FarmCollectedItem> collectedItems) {
        this.collectedItems = collectedItems;
    }
}
