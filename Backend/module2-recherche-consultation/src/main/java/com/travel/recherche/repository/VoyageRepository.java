package com.travel.recherche.repository;

import com.travel.recherche.entity.Voyage;
import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.TypeTransport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoyageRepository extends JpaRepository<Voyage, Long>, JpaSpecificationExecutor<Voyage> {

    Optional<Voyage> findByCodeVoyage(String codeVoyage);

    List<Voyage> findByVilleDestinationIgnoreCase(String villeDestination);

    List<Voyage> findByTypeTransport(TypeTransport typeTransport);

    List<Voyage> findByCategorie(CategorieOffre categorie);

    List<Voyage> findByEnPromotionTrueOrderByPrixPromoAsc();

    List<Voyage> findTop6ByOrderByNombreConsultationsDescNoteMoyenneDesc();

    @Query("SELECT DISTINCT v.villeDepart FROM Voyage v ORDER BY v.villeDepart ASC")
    List<String> findDistinctVillesDepart();

    @Query("SELECT DISTINCT v.villeDestination FROM Voyage v ORDER BY v.villeDestination ASC")
    List<String> findDistinctVillesDestination();

    @Query("SELECT DISTINCT v.paysDestination FROM Voyage v ORDER BY v.paysDestination ASC")
    List<String> findDistinctPaysDestination();

    @Query("SELECT DISTINCT v.compagnie FROM Voyage v ORDER BY v.compagnie ASC")
    List<String> findDistinctCompagnies();
}
