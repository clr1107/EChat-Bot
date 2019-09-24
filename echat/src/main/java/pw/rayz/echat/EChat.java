package pw.rayz.echat;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class EChat {
    public static final String COMMAND_PREFIX = "::";
    private static EChat instance = new EChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private ExecutorService executorService;
    private Configuration config;
    private JDABot bot;
    private boolean running = false;

    private EChat() {
    }

    public static void main(String[] args) {
        instance.load();
        instance.logger.info("Connected to E-Chat server.");

        // Yes, it's hacky, but temporary ;)
        Scanner scanner = new Scanner(System.in);
        for (String next = ""; instance.running; next = scanner.next()) {
            if (next.equals("stop"))
                instance.stop();
            else if (next.equals("reload"))
                instance.config.load();
            else instance.logger.info("Unknown command supplied, only command is \"stop\"");
        }
    }

    private void load() {
        this.running = true;

        this.executorService = Executors.newFixedThreadPool(4);
        this.config = new Configuration("config.json");
        this.bot = new JDABot();
    }

    public void stop() {
        logger.info("Stopping EChat bot...");

        bot.getJDA().shutdown();
        executorService.shutdown();

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
