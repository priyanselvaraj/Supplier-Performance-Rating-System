package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.KpiDefinitionRequest;
import com.supplier.sprsystem.dto.response.KpiCalculationResultResponse;
import com.supplier.sprsystem.dto.response.KpiDefinitionResponse;

import java.util.List;

public interface KpiService {

    List<KpiDefinitionResponse> getAllKpiDefinitions();

    List<KpiDefinitionResponse> getActiveKpiDefinitions();

    KpiDefinitionResponse getKpiDefinitionById(Long id);

    KpiDefinitionResponse createKpiDefinition(KpiDefinitionRequest request);

    KpiDefinitionResponse updateKpiDefinition(Long id, KpiDefinitionRequest request);

    KpiDefinitionResponse toggleKpiDefinition(Long id);

    void deleteKpiDefinition(Long id);

    List<KpiCalculationResultResponse> calculateAllActiveKpis();

    KpiCalculationResultResponse calculateKpi(String kpiCode);

    KpiCalculationResultResponse calculateKpiById(Long id);
}
