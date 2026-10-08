package com.travel.bagage.controller;

import com.travel.bagage.dto.BagageRequest;
import com.travel.bagage.dto.BagageResponse;
import com.travel.bagage.dto.BagageStatisticsResponse;
import com.travel.bagage.service.BagageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bagages")
public class BagageController {

    private final BagageService bagageService;

    public BagageController(BagageService bagageService) {
        this.bagageService = bagageService;
    }

    @PostMapping
    public ResponseEntity<BagageResponse> createBagage(@Valid @RequestBody BagageRequest request) {
        return new ResponseEntity<>(bagageService.createBagage(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BagageResponse>> getAllBagages() {
        return ResponseEntity.ok(bagageService.getAllBagages());
    }

    @GetMapping("/statistics")
    public ResponseEntity<BagageStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(bagageService.getStatistics());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BagageResponse> getBagageById(@PathVariable String id) {
        return ResponseEntity.ok(bagageService.getBagageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BagageResponse> updateBagage(@PathVariable String id, @Valid @RequestBody BagageRequest request) {
        return ResponseEntity.ok(bagageService.updateBagage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBagage(@PathVariable String id) {
        bagageService.deleteBagage(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reservation/{reservationId}")
    public ResponseEntity<BagageResponse> associateToReservation(@PathVariable String id, @PathVariable String reservationId) {
        return ResponseEntity.ok(bagageService.associateToReservation(id, reservationId));
    }
}
