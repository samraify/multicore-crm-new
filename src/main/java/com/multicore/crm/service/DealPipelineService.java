package com.multicore.crm.service;

import com.multicore.crm.repository.DealRepository;
import com.multicore.crm.dto.DealDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.multicore.crm.entity.Deal;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DealPipelineService {

    private final DealRepository dealRepository;

    // Get deals grouped by stage
    public Map<Deal.Stage, List<DealDTO>> getPipeline(Long businessId) {
        Map<Deal.Stage, List<DealDTO>> pipeline = new LinkedHashMap<>();
        for (Deal.Stage stage : Deal.Stage.values()) {
            List<DealDTO> deals = dealRepository.findByBusinessIdAndStage(businessId, stage)
                    .stream().map(this::toDTO).toList();
            pipeline.put(stage, deals);
        }
        return pipeline;
    }

    // Optional: Get total value per stage
    public Map<Deal.Stage, Double> getStageValues(Long businessId) {
        Map<Deal.Stage, Double> totals = new LinkedHashMap<>();
        for (Deal.Stage stage : Deal.Stage.values()) {
            double sum = dealRepository.findByBusinessIdAndStage(businessId, stage)
                    .stream()
                    .mapToDouble(d -> d.getAmount() != null ? d.getAmount() : 0)
                    .sum();
            totals.put(stage, sum);
        }
        return totals;
    }

    private DealDTO toDTO(Deal deal) {
        DealDTO dto = new DealDTO();
        dto.setId(deal.getId());
        dto.setBusinessId(deal.getBusiness().getId());
        dto.setCustomerId(deal.getCustomerId());
        dto.setLeadId(deal.getLeadId());
        dto.setTitle(deal.getTitle());
        dto.setAmount(deal.getAmount());
        dto.setStage(deal.getStage());
        dto.setProbability(deal.getProbability());
        dto.setExpectedCloseDate(deal.getExpectedCloseDate());
        dto.setCreatedAt(deal.getCreatedAt());
        dto.setUpdatedAt(deal.getUpdatedAt());
        return dto;
    }
}
