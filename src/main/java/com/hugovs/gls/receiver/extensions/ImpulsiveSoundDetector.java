package com.hugovs.gls.receiver.extensions;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioListener;
import com.hugovs.gls.core.AudioServerExtension;
import com.hugovs.gls.receiver.util.MathUtils;
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

    private int windowSize;
    private int windowsSizePowerOfTwo;
    private List<Complex[]> fftWindows;
    private List<double[]> impWindows;

    /**
     * Do something when the {@link com.hugovs.gls.core.AudioServer} starts.
     */
    @Override
    public void onServerStart() {
        super.onServerStart();
        windowSize = 99;
        windowsSizePowerOfTwo = Math.max(2, 2 * Integer.highestOneBit(windowSize - 1));
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
        impWindows = new ArrayList<>();

        // Extract windows
        for (int i = 1; i < samples.length; i += 2, pos++) {

            // Process window
            if (pos >= windowSize || i == samples.length - 1) {
                if (isImpulsive(data.getTimestamp(), window))
                    this.impWindows.add(window);
                pos = -1;
                continue;
            }

            window[pos] = samples[i];
        }

        data.putProperty("IMP", impWindows);
        data.putProperty("FFT", fftWindows);
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
        final FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.UNITARY);
        final Complex[] fftWindow = transformer.transform(paddedWindow, TransformType.FORWARD);

        fftWindows.add(fftWindow);

        // Calculate statistics
        final Complex[] absWindow = MathUtils.abs(fftWindow);
        final Complex expectation = MathUtils.expectation(absWindow, start, end);
        final Complex variance = MathUtils.variance(absWindow, start, end);

        // Checks if it is impulsive sound
        if (expectation.getReal() > 0.5 && variance.getReal() > 0.2) {
            log.info("Impulsive sound detected!");
            return true;
        }

        return false;

    }

}
