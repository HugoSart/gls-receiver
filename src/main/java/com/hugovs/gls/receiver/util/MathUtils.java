package com.hugovs.gls.receiver.util;

import org.apache.commons.math3.complex.Complex;

public class MathUtils {

    private MathUtils() {
        //no instance
    }

    public static int nearestPowerOf2(int number) {
        return 32 - Integer.numberOfLeadingZeros(number - 1);
    }

    public static int compare(Complex c1, Complex c2) {
        if (c1.getReal() > c2.getReal()) return -1;
        else if (c1.getReal() < c2.getReal()) return 1;
        if (c1.getImaginary() > c2.getImaginary()) return -1;
        else if (c1.getImaginary() < c2.getImaginary()) return 1;
        return 0;
    }

    public static double[] normalize(double[] numbers) {
        double higher = higher(numbers);
        double[] normalized = new double[numbers.length];
        for (int i = 0; i < numbers.length; i++)
            normalized[i] = numbers[i] / higher;
        return normalized;
    }

    public static double higher(double[] numbers) {
        double higher = Double.NEGATIVE_INFINITY;
        for (double number : numbers)
            if (number > higher) higher = number;
        return higher;
    }

    public static Complex higher(Complex[] numbers) {
        Complex higher = new Complex(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        for (Complex number : numbers)
            if (compare(number, higher) == -1) higher = number;
        return higher;
    }

    public static double mean(double[] numbers) {
        double sum = 0.0;
        for (double number : numbers)
            sum += number;
        return sum / numbers.length;
    }

    public static Complex mean(Complex[] numbers) {
        Complex sum =  new Complex(0d, 0d);
        for (Complex number : numbers)
            sum.add(number);
        return sum.divide(numbers.length);
    }

    public static double variance(double[] numbers) {
        double mean = mean(numbers);
        double temp = 0;
        for (double number : numbers)
            temp += Math.pow(number, 2);
        return temp / (numbers.length - 1);
    }

    public static Complex variance(Complex[] numbers) {
        return variance(numbers, 0, numbers.length);
    }

    public static Complex variance(Complex[] numbers, int start, int end) {
        if (start < 0 || end < 0 || end < start || end > numbers.length) throw new IllegalArgumentException();
        Complex mean = mean(numbers);
        Complex temp = new Complex(0d, 0d);
        for (int i = start; i < end; i++) {
            Complex number = numbers[i];
            temp.add((number.subtract(mean)).pow(2));
        }
        return temp.divide(numbers.length - 1);
    }

    public static Complex expectation(Complex[] numbers, int start, int end) {
        if (start < 0 || end < 0 || end < start || end > numbers.length) throw new IllegalArgumentException();
        Complex prob = new Complex(1.0 / (double)numbers.length);
        Complex sum = new Complex(0d, 0d);
        for (int i = start; i < end; i++)
            sum.add(numbers[i]).multiply(prob);
        return sum;
    }

    public static Complex expectation(Complex[] numbers) {
        return expectation(numbers, 0, numbers.length);
    }

}
