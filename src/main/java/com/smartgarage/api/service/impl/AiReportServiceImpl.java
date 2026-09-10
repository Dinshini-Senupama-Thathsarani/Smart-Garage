package com.smartgarage.api.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgarage.api.entity.JobCard;
import com.smartgarage.api.entity.JobCardMechanic;
import com.smartgarage.api.entity.JobCardPart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class AiReportServiceImpl implements com.smartgarage.api.service.AiReportService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.ai.enabled:false}")
    private boolean aiEnabled;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:claude-sonnet-5}")
    private String model;

    @Value("${app.ai.api-url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;

    public AiReportServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String generateSummary(JobCard jobCard) {
        if (!aiEnabled || apiKey == null || apiKey.isBlank() || apiKey.startsWith("YOUR_")) {
            return "AI summary is not configured. Set app.ai.enabled=true and a real app.ai.api-key " +
                    "in application.properties (see https://console.anthropic.com) to enable this feature.";
        }

        String prompt = buildPrompt(jobCard);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("max_tokens", 300);
            body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            String rawResponse = restTemplate.postForObject(apiUrl, entity, String.class);

            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode contentArray = root.path("content");
            if (contentArray.isArray() && !contentArray.isEmpty()) {
                return contentArray.get(0).path("text").asText("AI returned an empty summary.");
            }
            return "AI returned an unexpected response format.";

        } catch (Exception ex) {
            log.error("AI summary generation failed for job card {}: {}", jobCard.getId(), ex.getMessage());
            return "AI summary could not be generated right now (" + ex.getMessage() + "). The job card data is still saved normally.";
        }
    }

    private String buildPrompt(JobCard jobCard) {
        String mechanics = jobCard.getJobCardMechanics() == null ? "none recorded" :
                jobCard.getJobCardMechanics().stream()
                        .map(JobCardMechanic::getMechanic)
                        .filter(java.util.Objects::nonNull)
                        .map(m -> m.getFullName())
                        .collect(Collectors.joining(", "));

        String parts = jobCard.getJobCardParts() == null || jobCard.getJobCardParts().isEmpty() ? "none used" :
                jobCard.getJobCardParts().stream()
                        .map(p -> p.getPart().getPartName() + " x" + p.getQuantityUsed())
                        .collect(Collectors.joining(", "));

        return String.format(
                "Write a short, friendly 3-4 sentence service summary for a car garage customer, " +
                        "based on this completed job card. Do not invent details not given below.\n\n" +
                        "Mechanics involved: %s\n" +
                        "Spare parts used: %s\n" +
                        "Labor cost: Rs. %s\n" +
                        "Total cost: Rs. %s\n",
                mechanics, parts, jobCard.getLaborCost(), jobCard.getTotalCost());
    }
}
