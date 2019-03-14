package com.hugovs.gls.util;

import com.hugovs.wav.WavFile;
import com.hugovs.wav.WavFileException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import java.util.List;
import java.io.*;

public class WavFileCreator {

    public static final int TYPE_PCM = 1;
    public static final int TYPE_INTEGER = 2;

    // Constants
    private static final String RIFF = "RIFF";
    private static final String WAVE = "WAVE";
    private static final String FMT = "fmt\0";
    private static final String DATA = "data";

    private WavFileCreator() {
        //no instance
    }

    public static void createFile(String path, AudioFormat format, List<byte[]> samples) {
        try {
            File file = new File(path);
            WavFile wavFile = WavFile.newWavFile(
                    file,
                    format.getChannels(),
                    1280,
                    16,
                    (int)format.getSampleRate()
            );
            int remaining = samples.size();
            for (byte[] sample : samples)
                wavFile.writeFrames(sample, remaining--);
            wavFile.close();
        } catch (IOException | WavFileException e) {
            e.printStackTrace();
        }
    }

    public static byte[] buildByteArray(AudioFormat format, List<byte[]> samples) {

        // Values
        int frameSize = format.getFrameSize();
        int sampleSizeInBits = format.getSampleSizeInBits();
        int sampleRate = (int)format.getSampleRate();
        short channels = (short)format.getChannels();

        // Build sample array
        byte[] temp = new byte[samples.size() * samples.get(0).length];
        int pos = 0;
        for (byte[] sample : samples) {
            write(temp, pos, sample);
            pos += sample.length;
        }

        byte[] buff = new byte[44 + temp.length];

        // Wav metadata
        write(buff, 0, RIFF.getBytes());
        write(buff, 4, itba(buff.length));
        write(buff, 8, WAVE.getBytes());
        write(buff, 12, FMT.getBytes());

        // Format
        write(buff, 16, itba(frameSize));
        write(buff, 20, itba(ets(format.getEncoding())));
        write(buff, 22, stba(channels));
        write(buff, 24, itba(sampleRate));
        write(buff, 28, itba(((sampleRate * sampleSizeInBits * channels) / 8)));
        write(buff, 32, itba(((sampleSizeInBits * channels))));
        write(buff, 34, itba(sampleSizeInBits));

        // Data
        write(buff, 36, DATA.getBytes());
        write(buff, 40, itba(temp.length));
        write(buff, 44, temp);

        return buff;

    }

    private static byte[] itba(int value) {
        return new byte[] {
                (byte)(value),
                (byte)(value >>> 8),
                (byte)(value >>> 16),
                (byte)(value >>> 24)};
    }

    private static byte[] stba(short value) {
        byte[] ret = new byte[2];
        ret[1] = (byte)(value & 0xff);
        ret[0] = (byte)((value >> 8) & 0xff);
        return ret;
    }

    private static short ets(AudioFormat.Encoding encoding) {
        if (encoding == AudioFormat.Encoding.PCM_SIGNED)    return 1;
        if (encoding == AudioFormat.Encoding.PCM_UNSIGNED)  return 2;
        if (encoding == AudioFormat.Encoding.PCM_FLOAT)     return 3;
        if (encoding == AudioFormat.Encoding.ALAW)          return 4;
        if (encoding == AudioFormat.Encoding.ULAW)          return 5;
        return 0;
    }

    private static void write(byte[] array, int pos, byte[] value) {
        for (byte b : value) {
            array[pos] = b;
            pos++;
        }
    }

}
