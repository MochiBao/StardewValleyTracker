package mypackage.repositories;

import mypackage.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    ArrayList<Item> findAllByCategoryId(Long categoryId);
}