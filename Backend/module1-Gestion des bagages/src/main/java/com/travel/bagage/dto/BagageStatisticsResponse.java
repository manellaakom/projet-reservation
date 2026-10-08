package com.travel.bagage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BagageStatisticsResponse {
    private long totalBagages;
    private double totalWeight;
    private double totalRevenue;
    private double averageWeight;
    private long pendingBagages;
    private long registeredBagages;
}
