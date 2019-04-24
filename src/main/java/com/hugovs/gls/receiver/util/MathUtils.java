package com.hugovs.gls.receiver.util;

import org.apache.commons.math3.complex.Complex;
import org.apache.log4j.Logger;

public class MathUtils {

    private static final Logger log = Logger.getLogger(MathUtils.class);

    private MathUtils() {
        //no instance
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

    public static Complex mean(Complex[] numbers, int start, int end) {
        checkSubarrayArguments(numbers, start, end);
        int length = end - start + 1;
        Complex sum =  new Complex(0d, 0d);
        for (int i = start; i <= end; i++)
            sum = sum.add(numbers[i]);
        return sum.divide(length);
    }

    public static Complex variance(Complex[] numbers, int start, int end) {
        checkSubarrayArguments(numbers, start, end);
        int length = end - start + 1;
        Complex mean = mean(numbers, start, end);
        Complex temp = new Complex(0d, 0d);
        for (int i = start; i <= end; i++)
            temp = temp.add((numbers[i].subtract(mean)).pow(2));
        return temp.divide(length - 1);
    }

    public static Complex expectation(Complex[] numbers, int start, int end) {
        checkSubarrayArguments(numbers, start, end);
        int length = end - start + 1;
        Complex prob = new Complex(1.0 / (double)length);
        Complex sum = new Complex(0d, 0d);
        for (int i = start; i <= end; i++)
            sum = sum.add(numbers[i]).multiply(prob);
        return sum;
    }

    public static Complex[] abs(final Complex[] complexes) {
        final Complex[] absComplexes = new Complex[complexes.length];
        for (int i = 0; i < complexes.length; i++)
            absComplexes[i] = new Complex(complexes[i].abs());
        return absComplexes;
    }

    private static void checkSubarrayArguments(Object[] numbers, int start, int end) {
        if (start < 0 || end < 0 || end < start || end > numbers.length) throw new IllegalArgumentException();
    }

}
