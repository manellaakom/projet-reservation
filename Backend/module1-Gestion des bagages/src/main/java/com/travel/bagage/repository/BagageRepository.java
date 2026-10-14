package com.travel.bagage.repository;

import com.travel.bagage.entity.Bagage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BagageRepository extends CrudRepository<Bagage, String> {
    List<Bagage> findByReservationId(String reservationId);
}
