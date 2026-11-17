package com.example.largest;

import java.util.InputMismatchException;
import java.util.Scanner;

/** Finds the largest of three numbers entered by the user. */
public class LargestOfThree {
    public static double largest(double a, double b, double c) {
        return Math.max(a, Math.max(b, c));
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter first number: ");
            double a = scanner.nextDouble();
            System.out.print("Enter second number: ");
            double b = scanner.nextDouble();
            System.out.print("Enter third number: ");
            double c = scanner.nextDouble();
            System.out.println("Largest number = " + largest(a, b, c));
        } catch (InputMismatchException ex) {
            System.out.println("Please enter valid numbers.");
        }
    }
}
