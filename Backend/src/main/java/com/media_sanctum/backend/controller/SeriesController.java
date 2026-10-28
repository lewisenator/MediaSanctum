package com.media_sanctum.backend.controller;

import com.media_sanctum.backend.resource.DataResponse;
import com.media_sanctum.backend.resource.SeriesResponse;
import com.media_sanctum.backend.service.SeriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/series")
public class SeriesController {

    private final SeriesService seriesService;

    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<SeriesResponse>>> getSeries() {
        return ResponseEntity.ok(DataResponse.data(seriesService.getSeries()));
    }
}
