package com.example.largest;

public class LargestOfThreeTest {
    public static void main(String[] args) {
        check(45.0, LargestOfThree.largest(12, 45, 23), "middle value is largest");
        check(99.0, LargestOfThree.largest(99, 4, 8), "first value is largest");
        check(77.0, LargestOfThree.largest(2, 3, 77), "third value is largest");
        check(5.5, LargestOfThree.largest(5.5, 5.5, 1), "equal values");
        check(-1.0, LargestOfThree.largest(-8, -1, -4), "negative values");
        System.out.println("ALL TESTS PASSED");
    }

    private static void check(double expected, double actual, String scenario) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(scenario + ": expected " + expected + " but got " + actual);
        }
        System.out.println("PASS: " + scenario);
    }
}
