package com.miw.service;

import com.miw.database.RootRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


@Service
class StatisticsServiceTest {

    private RootRepository rootRepository;

    @Autowired
    public void StatisticsService(RootRepository rootRepository) {
        this.rootRepository = rootRepository;
    }

    private Map<LocalDate, Map<String, Double>> totalCumulativeValues = new HashMap<>();
    private Map<LocalDate, Map<String, Double>> boughtUnits = new HashMap<>();
    private Map<LocalDate, Map<String, Double>> soldUnits = new HashMap<>();


    void setupTestValues() {

        Map<String, Double> testValues = new HashMap<>();
        testValues.put("BTC", 1.3);
        testValues.put("ADA", 13.0);
        boughtUnits.put(LocalDate.now().minusDays(5), testValues);


//        testValues.put("BTC", 2.0);
//        testValues.put("ADA", 10.0);
//        boughtUnits.put(LocalDate.now().minusDays(4), testValues);

        Map<String, Double> testValues1 = new HashMap<>();
        testValues1.put("BTC", 1.5);
        testValues1.put("ADA", 15.0);
        boughtUnits.put(LocalDate.now().minusDays(3), testValues1);


        Map<String, Double> testValues2 = new HashMap<>();
        testValues2.put("BTC", 1.6);
        testValues2.put("ADA", 16.0);
        boughtUnits.put(LocalDate.now().minusDays(2), testValues2);

        Map<String, Double> testValues3 = new HashMap<>();
        testValues3.put("BTC", 1.7);
        testValues3.put("ADA", 17.0);
        boughtUnits.put(LocalDate.now().minusDays(1), testValues3);


        Map<String, Double> testValues4 = new HashMap<>();
        testValues4.put("BTC", 1.8);
        testValues4.put("ADA", 18.0);
        boughtUnits.put(LocalDate.now(), testValues4);



    }



    @Test
    void getPortfolioStats() {

    }

    @Test
    // TODO: cumulativeDayValues map gets overwritten, temp Map does'nt work, what to do??!
    void getCumillitivePortfolioValues() {
        setupTestValues();
        Map<String, Double> cumulativeValues = new HashMap<>();

        // find first day of purchase
        var startdate = boughtUnits.keySet().stream().min(Comparator.comparing(LocalDate::toEpochDay)).orElse(LocalDate.now());
        int daysBack = (int) ChronoUnit.DAYS.between(startdate, LocalDate.now());

        // Iterate over each date since portfolio has units, starting with first day of crypto purchase
        for (int days = daysBack; days >= 0; days--) {
            Map<String, Double> dayValues = new HashMap<>();
            LocalDate date = LocalDate.now().minusDays(days);
            System.out.println("\n" + date);

            // Set values of yesterday as today to start with
            totalCumulativeValues.put(date,totalCumulativeValues.get(date.minusDays(1)));

            // If units where bought on this day --> add units
            if (boughtUnits.containsKey(date)) {
                for (String symbol : boughtUnits.get(date).keySet()) {

                    //System.out.println( "\n" + symbol + ": ");
                    double units = boughtUnits.get(date).get(symbol);
                    double pastUnits = cumulativeValues.getOrDefault(symbol, 0.0);
                    System.out.println("units: " + units);
                    System.out.println("past units of this crypto: " + pastUnits);
                    // adds crypto symbol, today value and past values to map
                    cumulativeValues.put(symbol, units + pastUnits); // gets overwrited....
                    dayValues.put(symbol, units);
                    //Map<String, Double> tempCumValues = new HashMap<>();
                }
                // Put everything in super map with current date
            }
            // Put everything in super map with current date
            //totalCumulativeValues.values().stream().collect().
//            if (totalCumulativeValues.containsKey(date)){
//                dayValues.put(totalCumulativeValues.get(date));
//            }
            totalCumulativeValues.put(date, dayValues); // find a way to add past values as well.)
            System.out.println("symbol: " + dayValues.keySet());
            System.out.println("value: " + dayValues.values());
            System.out.println(totalCumulativeValues.values());


        }

        System.out.println("Datums: " + totalCumulativeValues.keySet());
        System.out.println("Values:  " + totalCumulativeValues.values());
    }
}