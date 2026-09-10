package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.dto.request.AssignMechanicRequest;
import com.smartgarage.api.dto.request.JobCardCreateRequest;
import com.smartgarage.api.dto.request.JobCardStatusUpdateRequest;
import com.smartgarage.api.dto.request.UsePartRequest;
import com.smartgarage.api.enums.JobCardStatus;
import com.smartgarage.api.service.AiReportService;
import com.smartgarage.api.service.JobCardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/job-cards")
@RequiredArgsConstructor
@Tag(name = "Job Cards")
public class JobCardController {

    private final JobCardService jobCardService;
    private final AiReportService aiReportService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@Valid @RequestBody JobCardCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, jobCardService.createFromBooking(request.getBookingId()), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, jobCardService.getById(id), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll(@RequestParam(required = false) JobCardStatus status) {
        if (status != null) {
            return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, jobCardService.getByStatus(status), ResponseMessage.SUCCESS_MESSAGE));
        }
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, jobCardService.getAll(), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody JobCardStatusUpdateRequest request) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, jobCardService.updateStatus(id, request.getStatus()), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PostMapping(value = "/{id}/mechanics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> assignMechanic(@PathVariable Long id, @Valid @RequestBody AssignMechanicRequest request) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, jobCardService.assignMechanic(id, request), ResponseMessage.SUCCESS_MESSAGE));
    }

    @PostMapping(value = "/{id}/parts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> usePart(@PathVariable Long id, @Valid @RequestBody UsePartRequest request) {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, jobCardService.usePart(id, request), ResponseMessage.SUCCESS_MESSAGE));
    }


    @GetMapping(value = "/{id}/ai-summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAiSummary(@PathVariable Long id) {
        String summary = aiReportService.generateSummary(jobCardService.getById(id));
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, java.util.Map.of("summary", summary), ResponseMessage.SUCCESS_MESSAGE));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> delete(@PathVariable Long id) {
        jobCardService.delete(id);
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, ResponseMessage.SUCCESS_MESSAGE));
    }
}
