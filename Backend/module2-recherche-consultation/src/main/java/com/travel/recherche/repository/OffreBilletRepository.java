package com.travel.recherche.repository;

import com.travel.recherche.entity.OffreBillet;
import com.travel.recherche.enums.ClasseBillet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OffreBilletRepository extends JpaRepository<OffreBillet, Long> {

    Optional<OffreBillet> findByCodeOffreBillet(String codeOffreBillet);

    List<OffreBillet> findByVoyageIdOrderByPrixAsc(Long voyageId);

    List<OffreBillet> findByClasseOrderByPrixAsc(ClasseBillet classe);

    List<OffreBillet> findByPrixLessThanEqualOrderByPrixAsc(BigDecimal prixMax);
}
