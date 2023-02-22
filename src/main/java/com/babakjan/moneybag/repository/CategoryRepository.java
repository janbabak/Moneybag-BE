package com.babakjan.moneybag.repository;

import com.babakjan.moneybag.entity.Category;
import com.babakjan.moneybag.entity.CategoryAnalytic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select new com.babakjan.moneybag.entity.CategoryAnalytic(c, abs(sum(r.amount)), count(r)) " +
            "from Record r join Category c on r.category.id = c.id " +
            "where (r.account.user.id = :userId or :userId is null)" +
            "and (r.account.includeInStatistic) " +
            "and (:dateGe is null or r.date >= :dateGe) " +
            "and (:dateLt is null or r.date < :dateLt) " +
            "group by r.category.id"
    )
    List<CategoryAnalytic> findCategoriesAnalytic(
            @Param("userId") Long userId, @Param("dateGe") Date dateGe, @Param("dateLt") Date dateLt);
}
