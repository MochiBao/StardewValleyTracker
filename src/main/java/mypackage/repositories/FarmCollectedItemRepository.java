package mypackage.repositories;

import mypackage.entities.FarmCollectedItem;
import mypackage.entities.FarmCollectedItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FarmCollectedItemRepository extends JpaRepository<FarmCollectedItem, FarmCollectedItemId> {
    List<FarmCollectedItem> findAllByFarmId(Long farmId);
    long countByFarmId(Long farmId);
}
