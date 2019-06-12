package com.hugovs.gls.receiver.extensions;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioListener;
import com.hugovs.gls.core.AudioServerExtension;
import com.hugovs.gls.receiver.util.MathUtils;
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

    @Override
    public void onDataReceived(AudioData data) {
        // Extract impulsive windows
        List<Complex[]> subFftImpWindows;
        try {
            subFftImpWindows = (List<Complex[]>) data.getProperty("SUB_FFT|IMP");
        } catch (Exception e) {
            subFftImpWindows = new ArrayList<>();
        }

        // Apply gunshot detection algorithm to all impulsive windows
        boolean isGunshot = false;
        for (Complex[] subFftImpWindow : subFftImpWindows) {
            boolean isWindowGunshot = isGunshot(MathUtils.convertToDouble(subFftImpWindow));
            if (isWindowGunshot) log.info("Impulsive sound: GUNSHOT!");
            else log.info("Impulsive sound: other.");
            if (isWindowGunshot) isGunshot = true;
        }

        data.putProperty("GUNSHOT", isGunshot);

    }

    private boolean isGunshot(double[] window) {
        double[] mfccFeatures = calculateMFCC(window);
        return decide(mfccFeatures);
    }

    private double[] calculateMFCC(double[] window) {
        final ArrayList<DataProcessor> pipeline = new ArrayList<>();
        pipeline.add(new SingleValueAudioSource(window));
        pipeline.add(new MelFrequencyFilterBank(130, 6800, 4));
        pipeline.add(new DiscreteCosineTransform2(4, 22));
        FrontEnd f = new FrontEnd(pipeline);


        return ((DoubleData)f.getData()).getValues();
    }

    private boolean decide(double[] x) {
        return (x[1] < 0.115742 && x[7] < 0.0202726 && x[1] >= 0.01597984 && x[13] < -0.010137) ||
                (x[1] > 0.115742 && x[21] > 0.0678466) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] < -0.00664235) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] > 0.438763) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] > 0.438763) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] < 0.438763 && x[0] < 0.0654665) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] < 0.438763 && x[0] > 0.0654665 && x[6] < 0.242854) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] < 0.438763 && x[0] > 0.0654665 && x[6] > 0.242854) ||
                (x[1] > 0.115742 && x[21] < 0.0678466 && x[7] > -0.00664235 && x[1] < 0.438763 && x[0] > 0.0654665 && x[6] > 0.242854 && x[2] < -0.0360553);
    }

    private String dataValuesToString(final Data data) {
        if (data.getClass().isAssignableFrom(DoubleData.class)) {
            final StringBuilder b = new StringBuilder();
            final double[] values = ((DoubleData) data).getValues();
            b.append(data.toString()).append(" -> [ ");
            for (double value : values) b.append(value).append(" ");
            b.append("]");
            return b.toString();
        }

        return data.toString();
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

    private class LogDataProcessor extends BaseDataProcessor {

        @Override
        public Data getData() throws DataProcessingException {
            final Data data = getPredecessor().getData();
            //log.info(getPredecessor().getClass().getSimpleName() + " data: " + dataValuesToString(data));
            return data;
        }
    }

}
