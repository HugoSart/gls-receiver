package com.hugovs.gls.receiver.listeners;

import com.hugovs.gls.receiver.AudioReceiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Listener that dumps all the received samples in a file frequently.
 */
public class ScheduledWavListener implements AudioReceiver.Listener {

    private static final int SAMPLES_PER_FILE = 64;
    private static final int FRAME_LIMIT = 16;

    // Important
    private final List<byte[]> buffer = new ArrayList<>(SAMPLES_PER_FILE);

    // Utils
    private int frameCounter = 0;
    private int pos = 0;

    /**
     * Method called when a new data is received by the {@link AudioReceiver}.
     * Dump the buffer to a file when the condition is true.
     *
     * @param data the new data received.
     */
    @Override
    public void onDataReceived(byte[] data) {

        if (buffer.size() <= pos && buffer.size() < SAMPLES_PER_FILE) buffer.add(data);
        else buffer.set(pos, data);
        pos++;

        if (pos == SAMPLES_PER_FILE) {
            pos = 0;
            new Thread(() -> dumpBufferToWav(List.copyOf(buffer))).start();
        }

    }

    /**
     * Dumps the data on the {@code buffer} to a wav file.
     * @param buffer the list to be dumped.
     */
    private void dumpBufferToWav(List<byte[]> buffer) {
        try {

            // Dump to file
            FileOutputStream file = new FileOutputStream("frame-" + frameCounter + ".wav");
            for (byte[] bytes : buffer)
                file.write(bytes);
            file.close();

            // Increment frameCounter
            frameCounter++;
            if (frameCounter >= FRAME_LIMIT)
                frameCounter = 0;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
