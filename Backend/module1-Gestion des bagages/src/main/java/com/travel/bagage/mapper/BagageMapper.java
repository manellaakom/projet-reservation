package com.travel.bagage.mapper;

import com.travel.bagage.dto.BagageRequest;
import com.travel.bagage.dto.BagageResponse;
import com.travel.bagage.entity.Bagage;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BagageMapper {

    public Bagage toEntity(BagageRequest request) {
        Bagage bagage = new Bagage();
        bagage.setId(UUID.randomUUID().toString());
        bagage.setReservationId(request.getReservationId());
        bagage.setType(request.getType());
        bagage.setPoids(request.getPoids());
        bagage.setStatut(request.getStatut());
        bagage.setPrix(request.getPrix());
        return bagage;
    }

    public BagageResponse toResponse(Bagage bagage) {
        BagageResponse response = new BagageResponse();
        response.setId(bagage.getId());
        response.setReservationId(bagage.getReservationId());
        response.setType(bagage.getType());
        response.setPoids(bagage.getPoids());
        response.setStatut(bagage.getStatut());
        response.setPrix(bagage.getPrix());
        return response;
    }
}
