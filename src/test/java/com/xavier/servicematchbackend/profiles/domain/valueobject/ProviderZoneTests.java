package com.xavier.servicematchbackend.profiles.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProviderZoneTests {

    @Test
    void createRejectsInvalidLatitude() {
        assertThatThrownBy(() -> ProviderZone.create(100.0, 10.0, 5.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("centerLat must be between -90 and 90");
    }

    @Test
    void createRejectsInvalidLongitude() {
        assertThatThrownBy(() -> ProviderZone.create(10.0, 200.0, 5.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("centerLng must be between -180 and 180");
    }

    @Test
    void createRejectsRadiusOutOfBounds() {
        assertThatThrownBy(() -> ProviderZone.create(10.0, 10.0, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("radiusKm must be positive");

        assertThatThrownBy(() -> ProviderZone.create(10.0, 10.0, ProviderZone.MAX_RADIUS_KM + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("radiusKm must be <=");
    }
}
