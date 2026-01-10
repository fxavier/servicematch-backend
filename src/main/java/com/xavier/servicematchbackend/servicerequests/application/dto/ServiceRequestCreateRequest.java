package com.xavier.servicematchbackend.servicerequests.application.dto;

public record ServiceRequestCreateRequest(String categoryId,
                                          String description,
                                          Double locationLat,
                                          Double locationLng,
                                          String status) {
}
