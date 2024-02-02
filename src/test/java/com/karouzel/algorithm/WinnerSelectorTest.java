package com.karouzel.algorithm;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the WinnerSelector functionality to prove the fairness and robustness of the
 * winner picking algorithm.
 *
 * <p>
 * This class uses the UUID.randomUUID().toString() method to generate random entropy,
 * and new Random().nextInt() to generate a random number of participants.
 *
 * @author karouzel.com
 */
class WinnerSelectorTest {

    /**
     * The cases as documented on the website.
     */
    @Test
    void pickNumber_documentation() {
        repeatedVerify(22, "35751.07/8.19.32.41.42.9.12/15455.36/17.33.38.43.49.80/21", 6);
        repeatedVerify(3489, "35751.07/8.19.32.41.42.9.12/15455.36/17.33.38.43.49.80/21", 284);
        repeatedVerify(150333, "35751.07/8.19.32.41.42.9.12/15455.36/17.33.38.43.49.80/21", 34463);
    }


    /**
     * Makes sure the algorithm always creates the same result for the same input.
     */
    @Test
    void pickNumber_random() {
        for (int i=0; i<1000; i++) {
            repeatedVerify(new Random().nextInt(999_999)+1, UUID.randomUUID().toString());
        }
    }

    /**
     * Using numAddresses=10, each address gets about the same wins when run 1 million times.
     * When testing, the deviation was less than 1%.
     * Allowing 5% up and down in this test so that it always passes for everyone.
     */
    @Test
    void evenDistribution_1() {
        Map<Integer, Integer> resultsCounter = new HashMap<>();
        for (int i=0; i<1_000_000; i++) {
            int result = WinnerSelector.selectWinner(10, UUID.randomUUID().toString());
            resultsCounter.merge(result, 1, Integer::sum);
        }
        assertEquals(resultsCounter.size(), 10);
        for (Integer value : resultsCounter.values()) {
            assertTrue(value >= 95000 && value <= 105000);
        }
    }

    /**
     * Testing with varying number of participants, not hardcoded to 10.
     * This test takes 4-5 minutes.
     */
    @Test
    void evenDistribution_2() {
        List<Integer> runList = Arrays.asList(
                //feel free to play with these numbers. the larger the number, the longer the
                //test needs to finish.
                1, 2, 3, 5, 9, 20, 77, 150, 5555
                , 9999, 10_000, 10_001, 44_447
        );

        for (Integer numParticipants : runList) {
            int numRuns = numParticipants * 1000;
            int expected = numRuns / numParticipants;
            int lowerBound = (int)(expected * 0.8d); //80%    the allowed deviation here is higher
            int upperBound = (int)(expected * 1.2d); //120%   because we only run each numRuns=1000.

            Map<Integer, Integer> resultsCounter = new HashMap<>();
            for (int i = 0; i< numRuns; i++) {
                int result = WinnerSelector.selectWinner(numParticipants, UUID.randomUUID().toString());
                resultsCounter.merge(result, 1, Integer::sum);
            }
            assertEquals(resultsCounter.size(), numParticipants);
            for (Integer value : resultsCounter.values()) {
                assertTrue(value >= lowerBound && value <= upperBound);
            }
        }
    }



    /**
     * Makes sure the algorithm always creates the same result for the same input.
     */
    private static void repeatedVerify(int numAddresses, String entropy) {
        int expected = WinnerSelector.selectWinner(numAddresses, entropy);
        for (int i=0; i<100; i++) {
            int result = WinnerSelector.selectWinner(numAddresses, entropy);
            assertEquals(expected, result);
        }
    }

    /**
     * Makes sure the algorithm always creates the same expected result for the same input.
     */
    private static void repeatedVerify(int numAddresses, String entropy, int expected) {
        for (int i=0; i<100_000; i++) {
            int result = WinnerSelector.selectWinner(numAddresses, entropy);
            assertEquals(expected, result);
        }
    }

}