package com.travel.recherche.repository;

import com.travel.recherche.entity.HistoriqueRecherche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueRechercheRepository extends JpaRepository<HistoriqueRecherche, Long> {

    List<HistoriqueRecherche> findTop10ByOrderByDateRechercheDesc();
}
