package pw.rayz.echat;

import org.apache.commons.lang3.time.DurationFormatUtils;
import pw.rayz.echat.utils.logging.ConsoleLoggerFormatter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

public final class EChat {
    public static final String COMMAND_PREFIX = "::";
    private static final EChat instance = new EChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private ExecutorService executorService;
    private Configuration config;
    private JDABot bot;
    private boolean running = false;
    private long startup;

    private EChat() {
    }

    public static void main(String[] args) {
        instance.setupLogger();

        instance.logger.info("Starting up...");
        instance.load();
        instance.startup = System.currentTimeMillis();

        // Yes, it's hacky, but temporary ;)
        Scanner scanner = new Scanner(System.in);
        String next;

        while (instance.running && (next = scanner.next()) != null) {
            if (next.equals("stop"))
                instance.stop();
            else if (next.equals("reload"))
                instance.config.load();
            else if (next.equalsIgnoreCase("ut")) {
                long seconds = instance.millisSinceStartup();
                instance.logger.info("Uptime: " + DurationFormatUtils.formatDuration(seconds, "HH:mm:ss"));
            } else instance.logger.info("Unknown command supplied, stop, reload, ut");
        }
    }

    private void setupLogger() {
        logger.setUseParentHandlers(false);
        String loggingFile = new File(getFolder().getParentFile(), "log.txt").toPath().toString();

        try {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            FileHandler fileHandler = new FileHandler(loggingFile, true);
            Formatter formatter = new ConsoleLoggerFormatter();

            consoleHandler.setFormatter(formatter);
            fileHandler.setFormatter(formatter);

            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
        } catch (IOException exception) {
            System.out.println("Error tuning logger.");
            System.exit(1);
        }
    }

    private void load() {
        this.running = true;
        this.executorService = Executors.newFixedThreadPool(4);
        this.config = new Configuration("config.json");

        this.bot = new JDABot(this);
        bot.load();
    }

    public void stop() {
        logger.info("Stopping EChat bot...");

        bot.unload();
        executorService.shutdown();

        this.running = false;
        logger.info("Stopped.");

        System.exit(0);
    }

    public File getFolder() {
        URL url = EChat.class.getProtectionDomain().getCodeSource().getLocation();
        File file = null;

        try {
            file = new File(url.toURI());
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
            logger.severe("Could not load folder for EChat bot.");
        }

        return file;
    }

    public long millisSinceStartup() {
        return (System.currentTimeMillis() - startup);
    }

    public Logger getLogger() {
        return logger;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Configuration getConfig() {
        return config;
    }

    public JDABot getBot() {
        return bot;
    }

    public boolean isRunning() {
        return running;
    }

    public static EChat eChat() {
        return instance;
    }
}
