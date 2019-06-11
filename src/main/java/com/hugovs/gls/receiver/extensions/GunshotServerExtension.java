package com.hugovs.gls.receiver.extensions;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioListener;
import com.hugovs.gls.core.AudioServerExtension;
import com.hugovs.gls.receiver.api.GunshotAPI;
import com.hugovs.gls.receiver.util.MathUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class GunshotServerExtension extends AudioServerExtension implements AudioListener {

    private static final Logger log = Logger.getLogger(GunshotServerExtension.class);

    private final GunshotAPI server = new GunshotAPI(new InetSocketAddress("localhost", 55556));
    private final Thread thread = new Thread(server);

    @Override
    public void onServerStart() {
        thread.start();
    }

    @Override
    public void onServerClose() {
        try {
            server.stop();
        } catch (IOException | InterruptedException e) {
            log.error("Failed to stop GunshotAPI", e);
        }
    }

    @Override
    public void onDataReceived(AudioData data) {
        if (data.hasProperty("FFT")) {
            final List<Complex[]> fftWindows = (List<Complex[]>) data.getProperty("FFT");
            final double[] first = MathUtils.convertToDouble(fftWindows.get(0));
            server.sendFrequencies(data.getSourceId(), first);
        }
    }

}
