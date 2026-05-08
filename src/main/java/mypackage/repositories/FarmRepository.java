package mypackage.repositories;

import mypackage.entities.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface FarmRepository extends JpaRepository <Farm, Long> {
    ArrayList<Farm> findAllByUserId(Long userId);
}
