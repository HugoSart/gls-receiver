package com.hugovs.gls;

import com.hugovs.gls.extensions.DataExtractor;
import com.hugovs.gls.extensions.SoundPlayer;
import com.hugovs.gls.extensions.WaveDrawer;
import com.hugovs.gls.receiver.AudioServer;
import com.hugovs.gls.receiver.AudioServerExtension;
import com.hugovs.gls.util.StringUtils;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.choice.RangeArgumentChoice;
import net.sourceforge.argparse4j.inf.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private static Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) {
        Namespace ns = parseArguments(args);
        if (ns == null) System.exit(-1);

        int sampleRate = ns.getInt("sample_rate");
        int sampleSize = ns.getInt("sample_size");
        int bufferSize = ns.getInt("buffer_size");
        int port = ns.getInt("port");

        log.info("Connection properties:");
        log.info("  - Port: " + port);
        log.info("Sound properties:");
        log.info("  - Sample rate: " + sampleRate);
        log.info("  - Sample size: " + sampleSize);
        log.info("  - Buffer size: " + bufferSize);

        List<AudioServerExtension> extensions = new ArrayList<>();
        if (ns.getAttrs().containsKey("wave_drawer"))
            extensions.add(new WaveDrawer());
        log.info("Extensions: " + StringUtils.join(extensions));

        AudioServer audioServer = new AudioServer(sampleRate, sampleSize, bufferSize);
        audioServer.addExtension(extensions);
        audioServer.addExtension(new DataExtractor());
        audioServer.startReceiving(port);
    }

    private static Namespace parseArguments(String[] args) {
        // Register arguments
        ArgumentParser parser = ArgumentParsers.newFor("GLS Receiver").build()
                .defaultHelp(true)
                .description("Starts the GLS Receiver application server.");

        // Connection arguments
        ArgumentGroup connectionGroup = parser.addArgumentGroup("Connection").description("Specify some connection properties");
        connectionGroup.addArgument("-p", "--port")
                .metavar("port")
                .type(Integer.class)
                .choices(new RangeArgumentChoice<>(0, 65535))
                .setDefault(55555)
                .help("Specify the port to listen to packets");

        // Sound arguments
        ArgumentGroup soundGroup = parser.addArgumentGroup("Sound").description("Specify some sound properties");
        soundGroup.addArgument("-sr", "--sample-rate")
                .metavar("sampleRate")
                .type(Integer.class)
                .choices(new RangeArgumentChoice<>(16000, 128000))
                .setDefault(16000)
                .help("Specify the audio sample rate to be used");
        soundGroup.addArgument("-ss", "--sample-size")
                .metavar("sampleSize")
                .type(Integer.class)
                .choices(new RangeArgumentChoice<>(16, 128))
                .setDefault(16)
                .help("Specify the audio sample size");
        soundGroup.addArgument("-b", "--buffer-size")
                .metavar("bufferSize")
                .type(Integer.class)
                .choices(new RangeArgumentChoice<>(1280, 12800))
                .setDefault(1280)
                .help("Specify the buffer size");

        // Extensions arguments
        ArgumentGroup extensionsGroup = parser.addArgumentGroup("Extensions").description("Enable extra built-in extensions");
        extensionsGroup.addArgument("--wave-drawer")
                .metavar("waveDrawer")
                .action(Arguments.storeTrue())
                .help("Converts the received data in a graphically wave visualization");
        extensionsGroup.addArgument("--sound-player")
                .metavar("soundPlayer")
                .action(Arguments.storeTrue())
                .help("Reproduces the received data on the main device's sound output");

        // Parse
        try {
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            log.error("Failed to parse arguments", e);
            return null;
        }

    }

}
