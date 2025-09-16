package com.juanda.backend.service;

import com.juanda.backend.testinfra.PostgresTC;
import com.juanda.backend.web.dto.SearchResponseDTO;
import com.juanda.backend.web.error.ApiValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class SearchServiceIT extends PostgresTC {

    @Autowired
    SearchService service;

    @Test
    void search_should_return_sorted_results_with_positive_price() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = checkIn.plusDays(2);
        int guests = 2;

        SearchResponseDTO resp = service.search(checkIn, checkOut, guests);

        Assertions.assertThat(resp.results()).isNotEmpty();
        // orden ascendente por precio (lo hace la query)
        Assertions.assertThat(resp.results())
            .isSortedAccordingTo((a, b) -> a.price().compareTo(b.price()));
        Assertions.assertThat(resp.results())
            .allSatisfy(r -> Assertions.assertThat(r.price()).isPositive());
        Assertions.assertThat(resp.results())
            .allSatisfy(r -> {
                Assertions.assertThat(r.code()).isNotBlank();
                Assertions.assertThat(r.amenities()).isNotNull();
                // Por el seed, la mayoría traen "wifi": true
                Assertions.assertThat(r.amenities()).containsKey("wifi");
            });
    }

    @Test
    void search_should_fail_on_invalid_params() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = checkIn.minusDays(1);

        Assertions.assertThatThrownBy(() -> service.search(checkIn, checkOut, 1))
            .isInstanceOf(ApiValidationException.class);
    }

}
