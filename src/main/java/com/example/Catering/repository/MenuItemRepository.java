package com.example.Catering.repository;

import com.example.Catering.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findAllByOrderByNameAsc();

    // Игнорируем регистр при сравнении строк из БД и параметров URL
    List<MenuItem> findByCategoryIgnoreCaseOrderByNameAsc(String category);
}