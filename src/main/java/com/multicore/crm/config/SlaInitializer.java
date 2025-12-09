package com.multicore.crm.config;

import com.multicore.crm.entity.SLA;
import com.multicore.crm.repository.SLARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlaInitializer implements CommandLineRunner {

    private final SLARepository slaRepository;

    @Override
    public void run(String... args) {
        seedIfMissing(SLA.PriorityLevel.LOW, 72);
        seedIfMissing(SLA.PriorityLevel.MEDIUM, 48);
        seedIfMissing(SLA.PriorityLevel.HIGH, 24);
        seedIfMissing(SLA.PriorityLevel.URGENT, 8);
    }

    private void seedIfMissing(SLA.PriorityLevel level, int hours) {
        slaRepository.findByPriorityLevel(level).orElseGet(() -> {
            SLA sla = new SLA();
            sla.setPriorityLevel(level);
            sla.setAllowedHours(hours);
            return slaRepository.save(sla);
        });
    }
}

