package com.tutego.date4u.core.controller;

import com.tutego.date4u.Registrations;
import com.tutego.date4u.Trending;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class StatisticRestController {

    @GetMapping( "/api/stat/trending-xml" )
    // @GetMapping( path     = "/api/stat/trending-xml",
    //              produces = MediaType.APPLICATION_XML_VALUE )
    public Trending trendingXml() {
        return new Trending();
    }

    @GetMapping( path     = "/api/stat/trending-json",
            produces = MediaType.APPLICATION_JSON_VALUE )
    public Trending trendingJson() {
        return new Trending();
    }

    @GetMapping( path = "/api/stat/registrations" )
    public Registrations registrations() {
        YearMonth start = YearMonth.now().minusYears( 2 );
        YearMonth end   = YearMonth.now();

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return new Registrations(
                Stream.iterate( start, o -> o.plusMonths( 1 ) )
                        .limit( start.until( end, ChronoUnit.MONTHS ) + 1 )
                        .map( yearMonth -> new Registrations.Data(
                                yearMonth, rnd.nextInt( 1000, 10000 ) ) )
                        .collect( Collectors.toList() ) );
    }

}