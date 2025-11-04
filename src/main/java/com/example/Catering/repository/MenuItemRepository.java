package com.example.Catering.repository;

import com.example.Catering.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
List<MenuItem> findByCategoryOrderByPriceAsc(String category);
List<MenuItem> findAllByOrderByNameAsc();
}
