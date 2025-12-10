package com.multicore.crm.controller;

import com.multicore.crm.dto.DealDTO;
import com.multicore.crm.entity.Deal;
import com.multicore.crm.service.DealPipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;


@RestController
@RequestMapping("/api/business/{businessId}/pipeline")
@RequiredArgsConstructor
public class DealPipelineController {

    private final DealPipelineService pipelineService;

    @GetMapping
    public Map<Deal.Stage, List<DealDTO>> getPipeline(@PathVariable Long businessId) {
        return pipelineService.getPipeline(businessId);
    }

    @GetMapping("/values")
    public Map<Deal.Stage, Double> getStageValues(@PathVariable Long businessId) {
        return pipelineService.getStageValues(businessId);
    }
}
