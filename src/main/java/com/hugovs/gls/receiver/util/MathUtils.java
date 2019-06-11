package com.hugovs.gls.receiver.util;

import org.apache.commons.math3.complex.Complex;
import org.apache.log4j.Logger;

public class MathUtils {

    private static final Logger log = Logger.getLogger(MathUtils.class);

    private MathUtils() {
        //no instance
    }

    public static double[] normalize(double[] numbers) {
        final double higher = higher(numbers);
        final double lower = lower(numbers);
        double[] normalized = new double[numbers.length];
        for (int i = 0; i < numbers.length; i++)
            normalized[i] = numbers[i] / higher;
        return normalized;
    }

    public static double[] normalize(double[] numbers, double a, double b) {
        final double[] normalized = new double[numbers.length];
        final double[] lh = lowerAndHigher(numbers);
        final double min = lh[0];
        final double max = lh[1];
        for (int i = 0; i < numbers.length; i++)
            normalized[i] = (((b - a) * (numbers[i] - min)) / (max - min)) + a;
        return normalized;
    }

    public static double higher(double[] numbers) {
        double higher = Double.NEGATIVE_INFINITY;
        for (double number : numbers)
            if (number > higher) higher = number;
        return higher;
    }

    public static double lower(double[] numbers) {
        double min = Double.POSITIVE_INFINITY;
        for (double number : numbers)
            if (number < min) min = number;
        return min;
    }

    public static double[] lowerAndHigher(double[] numbers) {
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (double number : numbers)
            if (number < min) min = number;
            else max = number;
        return new double[]{min, max};
    }

    public static Complex mean(Complex[] numbers, int start, int end) {
        checkSubarrayArguments(numbers, start, end);
        int length = end - start + 1;
        Complex sum =  new Complex(0d, 0d);
        for (int i = start; i <= end; i++)
            sum = sum.add(numbers[i]);
        return sum.divide(length);
    }

    public static Complex mean(Complex[] numbers) {
        return mean(numbers, 0, numbers.length - 1);
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

    public static Complex variance(Complex[] numbers) {
        return variance(numbers, 0, numbers.length - 1);
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

    public static Complex expectation(Complex[] numbers) {
        return expectation(numbers, 0, numbers.length - 1);
    }

    public static double[] convertToDouble(final Complex[] c) {
        final double[] ret = new double[c.length];
        for (int i = 0; i < ret.length; i++)
            ret[i] = c[i].getReal();
        return ret;
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
