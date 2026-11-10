package com.travel.paiement.repository;

import com.travel.paiement.entity.Paiement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends CrudRepository<Paiement, String> {
}