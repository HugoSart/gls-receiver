package com.hugovs.gls.receiver;

import com.hugovs.gls.core.AudioServer;
import com.hugovs.gls.core.AudioServerExtension;
import com.hugovs.gls.core.util.StringUtils;
import com.hugovs.gls.receiver.api.GunshotAPIManager;
import com.hugovs.gls.receiver.extensions.GunshotDetector;
import com.hugovs.gls.receiver.extensions.GunshotSender;
import com.hugovs.gls.receiver.extensions.ImpulsiveSoundDetector;
import com.hugovs.gls.receiver.extensions.WaveDrawer;
import com.hugovs.gls.receiver.input.UdpAudioInput;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.choice.RangeArgumentChoice;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private static Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) {
        GunshotAPIManager.start("localhost", 55556);

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
        log.info("Extensions: " + StringUtils.join(extensions));

        AudioServer audioServer = new AudioServer(sampleRate, sampleSize);
        audioServer.setInput(new UdpAudioInput(55555, bufferSize));
        audioServer.addExtension(new ImpulsiveSoundDetector());
        audioServer.addExtension(new GunshotDetector());
        audioServer.addExtension(new WaveDrawer());
        audioServer.addExtension(new GunshotSender());
        audioServer.addExtension(extensions);
        audioServer.start();
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
