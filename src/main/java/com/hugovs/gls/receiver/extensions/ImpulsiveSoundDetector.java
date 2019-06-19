package com.hugovs.gls.receiver.extensions;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioListener;
import com.hugovs.gls.core.AudioServerExtension;
import com.hugovs.gls.receiver.util.MathUtils;
import com.hugovs.gls.receiver.util.Property;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An {@link AudioServerExtension} to detects impulsive sound waves.
 *
 * @author Hugo Sartori
 */
public class ImpulsiveSoundDetector extends AudioServerExtension implements AudioListener {

    private static final Logger log = Logger.getLogger(ImpulsiveSoundDetector.class);
    private static final FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.UNITARY);

    private int windowSize;
    private List<Complex[]> fftWindows;
    private List<Complex[]> subFftImpWindows;

    /**
     * Do something when the {@link com.hugovs.gls.core.AudioServer} starts.
     */
    @Override
    public void onServerStart() {
        super.onServerStart();
        windowSize = 99;
        int windowsSizePowerOfTwo = Math.max(2, 2 * Integer.highestOneBit(windowSize - 1));
        log.info("Window Size   : " + windowSize);
        log.info("Power of two  : " + windowsSizePowerOfTwo);
    }

    /**
     * Do something when a new {@link AudioData} is received.
     *
     * @param data: the received {@link AudioData}.
     */
    @Override
    public void onDataReceived(AudioData data) {
        int pos = 0;
        double[] window = new double[windowSize];
        byte[] samples = data.getSamples();
        fftWindows = new ArrayList<>();
        subFftImpWindows = new ArrayList<>();

        // Extract windows
        for (int i = 1; i < samples.length; i += 2, pos++) {

            // Process window
            if (pos >= windowSize || i == samples.length - 1) {
                isImpulsive(data.getTimestamp(), window);
                pos = -1;
                continue;
            }

            window[pos] = samples[i];
        }

        data.putProperty(Property.FFT.name(), fftWindows);
        data.putProperty(Property.ALAN.name(), subFftImpWindows);
    }

    /**
     * Apply the impulsive sound algorithm.
     *
     * @param timestamp: the timestamp of the window.
     * @param window: the window itself.
     * @return {@code true} : if the window contains an impulsive sound;
     *         {@code false}: if it does not contains an impulsive sound.
     */
    private boolean isImpulsive(final long timestamp, final double[] window) {
        final int start = 30, end = 49;

        // Apply Fourier Transform to the window
        final double[] paddedWindow = Arrays.copyOf(window, 128);
        final Complex[] fftWindow = fft.transform(paddedWindow, TransformType.FORWARD);

        fftWindows.add(MathUtils.abs(fftWindow));

        // Calculate statistics
        final Complex[] subFFT = Arrays.copyOfRange(fftWindow, start, end + 1);
        final Complex[] absFFT = MathUtils.abs(subFFT);
        final Complex expectation = MathUtils.expectation(absFFT);
        final Complex variance = MathUtils.variance(absFFT);

        // Checks if it is impulsive sound
        if (expectation.getReal() > 0.5 && variance.getReal() > 0.2) {
            subFftImpWindows.add(subFFT);
            return true;
        }

        return false;

    }

}
