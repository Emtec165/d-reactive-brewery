package guru.springframework.sfgrestbrewery.web.controller;

import guru.springframework.sfgrestbrewery.bootstrap.BeerLoader;
import guru.springframework.sfgrestbrewery.services.BeerService;
import guru.springframework.sfgrestbrewery.web.model.BeerDto;
import guru.springframework.sfgrestbrewery.web.model.BeerPagedList;
import guru.springframework.sfgrestbrewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@WebFluxTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    BeerService beerService;

    BeerDto validBear;

    @BeforeEach
    void setUp() {
        validBear = BeerDto.builder()
                .beerName("Test beer")
                .beerStyle("PALE_ALE")
                .upc(BeerLoader.BEER_1_UPC)
                .build();
    }

    @Test
    void getBeerById() {
        UUID beerId = UUID.randomUUID();
        given(beerService.getById(any(), any())).willReturn(Mono.just(validBear));

        webTestClient.get()
                .uri("/api/v1/beer/" + beerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(beerDto -> beerDto.getBeerName(), equalTo(validBear.getBeerName()));
    }

    @Test
    void getBeerByUpc() {
        given(beerService.getByUpc(anyString())).willReturn(Mono.just(validBear));

        webTestClient.get()
                .uri("/api/v1/beerUpc/" + validBear.getUpc())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerDto.class)
                .value(BeerDto::getUpc, equalTo(validBear.getUpc()));
    }

    @Test
    void listBeers() {
        var beerPagedList = new BeerPagedList(List.of(validBear, validBear));

        given(beerService.listBeers(anyString(), any(BeerStyleEnum.class), any(PageRequest.class), anyBoolean()))
                .willReturn(Mono.just(beerPagedList));

        webTestClient.get()
                .uri("/api/v1/beer")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BeerPagedList.class);
    }
}