package com.hugovs.gls.extensions;

import com.hugovs.gls.receiver.AudioData;
import com.hugovs.gls.receiver.AudioServerExtension;
import com.hugovs.gls.receiver.DataListener;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DataExtractor extends AudioServerExtension implements DataListener {

    private final Logger log = Logger.getLogger(DataExtractor.class);

    private final Map<Long, Long> zeroCrossingRate = new HashMap<>();
    private LogTask task;

    @Override
    public void onServerStart() {
        super.onServerStart();
        task = new LogTask();
        new Timer().schedule(new LogTask(), 0, 1000);
    }

    @Override
    public void onDataReceived(AudioData data) {
        for (int i = 0; i < data.getSamples().length - 1; i++) {
            byte current = data.getSamples()[i], next = data.getSamples()[i + 1];
            if (current > 0 && next < 0)
                if (!zeroCrossingRate.containsKey(data.getSourceId()))
                    zeroCrossingRate.put(data.getSourceId(), 1L);
                else
                    zeroCrossingRate.put(data.getSourceId(), zeroCrossingRate.get(data.getSourceId()) + 1);
        }
    }

    @Override
    public void onServerClose() {
        super.onServerClose();
    }

    private class LogTask extends TimerTask {
        @Override
        public void run() {
            log.info("Zero-Crossing rate: " + zeroCrossingRate.get(0L));
            zeroCrossingRate.put(0L, 0L);
        }
    }

}
