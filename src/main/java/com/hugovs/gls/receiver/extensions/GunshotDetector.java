package com.hugovs.gls.receiver.extensions;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioListener;
import com.hugovs.gls.core.AudioServerExtension;
import com.hugovs.gls.receiver.util.MathUtils;
import com.hugovs.gls.receiver.util.Property;
import edu.cmu.sphinx.frontend.*;
import edu.cmu.sphinx.frontend.frequencywarp.MelFrequencyFilterBank;
import edu.cmu.sphinx.frontend.transform.DiscreteCosineTransform2;
import org.apache.commons.math3.complex.Complex;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * AudioServerExtension to detect a gunshot given it is an impulsive sound.
 *
 * @author Hugo Sartori
 */
public class GunshotDetector extends AudioServerExtension implements AudioListener {

    private static final Logger log = Logger.getLogger(GunshotDetector.class);

    /**
     * Process the data looking for gunshot sounds.
     *
     * @param data: the data to be processed.
     */
    @Override
    public void onDataReceived(AudioData data) {
        // Extract impulsive windows
        List<Complex[]> subFftImpWindows;
        try {
            subFftImpWindows = (List<Complex[]>) data.getProperty(Property.ALAN.name());
        } catch (Exception e) {
            subFftImpWindows = new ArrayList<>();
        }

        // Apply gunshot detection algorithm to all impulsive windows
        boolean isGunshot = false;
        for (Complex[] subFftImpWindow : subFftImpWindows) {
            boolean isWindowGunshot = isGunshot(MathUtils.abs(subFftImpWindow, true));
            if (isWindowGunshot) log.info("Impulsive sound: GUNSHOT!");
            else log.info("Impulsive sound: other.");
            if (isWindowGunshot) isGunshot = true;
        }

        data.putProperty(Property.GUNSHOT.name(), isGunshot);

    }

    /**
     * Checks if a given window has a gunshot sound.
     *
     * @param window: the window to be checked.
     * @return: {@code true} : if the window contains gunshot sound;
     *          {@code false}: if it does not.
     */
    private boolean isGunshot(double[] window) {
        double[] mfccFeatures = calculateMFCC(window);
        return decide(mfccFeatures);
    }

    /**
     * Extract MFCC features from a windows.
     *
     * @param window: the window to extract the features.
     * @return 22 MFCC features.
     */
    private double[] calculateMFCC(double[] window) {
        final ArrayList<DataProcessor> pipeline = new ArrayList<>();
        pipeline.add(new SingleValueAudioSource(window));
        pipeline.add(new MelFrequencyFilterBank(130, 6800, 4));
        pipeline.add(new DiscreteCosineTransform2(4, 22));
        FrontEnd f = new FrontEnd(pipeline);


        return ((DoubleData)f.getData()).getValues();
    }

    /**
     * Apply a decision tree to the given MFCC features.
     *
     * @param x: a array with MFCC features (at least 22).
     * @return {@code true}: if it is a gunshot;
     *         {@code false}: if it is not.
     */
    private boolean decide(double[] x) {
        return  (x[1] < 0.115742 && x[7] < 0.0202726 && x[1] >= 0.01597984 && x[13] < -0.010137) ||
                (x[1] > 0.115742 && x[21] > 0.0678466) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] < -0.00664235) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] > 0.438763) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] > 0.438763) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] < 0.438763 && x[0] < 0.0654665) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] < 0.438763 && x[0] > 0.0654665 && x[6] < 0.242854) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] < 0.438763 && x[0] > 0.0654665 && x[6] > 0.242854 && x[2] < -0.0360553) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] < 0.438763 && x[0] > 0.0654665 && x[6] > 0.242854 && x[2] < -0.0360553 && x[4] > 0.0147022);
    }

    /**
     * A DataProcessor that is used to inject a single value into the {@link FrontEnd}.
     */
    private class SingleValueAudioSource extends BaseDataProcessor {

        private Data data;

        SingleValueAudioSource(Data data) {
            this.data = data;
        }

        SingleValueAudioSource(double[] data) {
            this.data = new DoubleData(data, (int) getAudioServer().getAudioFormat().getSampleRate(), 0);
        }

        @Override
        public Data getData() throws DataProcessingException {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

    }

}
