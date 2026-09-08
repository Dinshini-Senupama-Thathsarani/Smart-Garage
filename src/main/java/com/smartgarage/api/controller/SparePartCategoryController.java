package com.smartgarage.api.controller;

import com.smartgarage.api.constant.CommonResponse;
import com.smartgarage.api.constant.ResponseCode;
import com.smartgarage.api.constant.ResponseMessage;
import com.smartgarage.api.entity.SparePartCategory;
import com.smartgarage.api.repository.SparePartCategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/spare-part-categories")
@RequiredArgsConstructor
@Tag(name = "Spare Part Categories")
public class SparePartCategoryController {

    private final SparePartCategoryRepository sparePartCategoryRepository;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> create(@RequestBody SparePartCategory category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommonResponse(ResponseCode.OPERATION_SUCCESS, sparePartCategoryRepository.save(category), ResponseMessage.SUCCESS_MESSAGE));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAll() {
        return ResponseEntity.ok(new CommonResponse(ResponseCode.OPERATION_SUCCESS, sparePartCategoryRepository.findAll(), ResponseMessage.SUCCESS_MESSAGE));
    }
}
