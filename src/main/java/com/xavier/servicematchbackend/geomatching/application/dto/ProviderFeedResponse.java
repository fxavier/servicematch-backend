package com.xavier.servicematchbackend.geomatching.application.dto;

import java.util.List;

public record ProviderFeedResponse(List<ProviderFeedItemResponse> items,
                                   int page,
                                   int size,
                                   long total,
                                   String sort) {
}
