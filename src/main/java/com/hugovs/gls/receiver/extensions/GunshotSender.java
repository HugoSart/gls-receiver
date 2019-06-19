package com.hugovs.gls.receiver.extensions;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioListener;
import com.hugovs.gls.core.AudioServerExtension;
import com.hugovs.gls.receiver.api.GunshotAPIManager;
import com.hugovs.gls.receiver.api.model.Frequency;
import com.hugovs.gls.receiver.api.model.Gunshot;
import com.hugovs.gls.receiver.util.MathUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.log4j.Logger;

import java.util.List;

public class GunshotSender extends AudioServerExtension implements AudioListener {

    private static final Logger log = Logger.getLogger(GunshotSender.class);

    @Override
    public void onDataReceived(AudioData data) {
        if (data.hasProperty("FFT")) {
            final List<Complex[]> fftWindows = (List<Complex[]>) data.getProperty("FFT");
            final double[] first = MathUtils.abs(fftWindows.get(0), true);
            GunshotAPIManager.sendFrequencies(new Frequency(data.getSourceId(), first));
        }

        if (data.hasProperty("GUNSHOT") && (boolean)data.getProperty("GUNSHOT")) {
            GunshotAPIManager.sendGunshot(new Gunshot(data.getSourceId(), data.getTimestamp()));
        }

    }

}
