package com.hugovs.gls.receiver.extensions;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioListener;
import com.hugovs.gls.core.AudioServerExtension;
import edu.cmu.sphinx.frontend.*;
import edu.cmu.sphinx.frontend.frequencywarp.MelFrequencyFilterBank;
import edu.cmu.sphinx.frontend.transform.DiscreteCosineTransform2;
import edu.cmu.sphinx.frontend.transform.DiscreteFourierTransform;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class GunshotDetector extends AudioServerExtension implements AudioListener {

    private static final Logger log = Logger.getLogger(GunshotDetector.class);

    @Override
    public void onDataReceived(AudioData data) {

        // Extract impulsive windows
        List<double[]> impWindows;
        try {
            impWindows = (List<double[]>) data.getProperty("IMP");
        } catch (Exception e) {
            impWindows = new ArrayList<>();
        }

        // Apply gunshot detection algorithm to all impulsive windows
        for (double[] impWindow : impWindows)
            if (isGunshot(impWindow))
                log.info("GUNSHOT DETECTED!!!");

    }

    private boolean isGunshot(double[] window) {
        calculateMFCC(window);
        return false;
    }

    private int calculateMFCC(double[] window) {

        final ArrayList<DataProcessor> pipeline = new ArrayList<DataProcessor>();

        pipeline.add(new SingleValueAudioSource(window));
        pipeline.add(new MelFrequencyFilterBank(50, 16000, 16));
        pipeline.add(new DiscreteCosineTransform2(16, 12));
        FrontEnd f = new FrontEnd(pipeline);

        log.info("Data: " + f.getData());

        return 0;
    }

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
