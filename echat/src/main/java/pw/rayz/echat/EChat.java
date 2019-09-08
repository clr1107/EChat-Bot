package pw.rayz.echat;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class EChat {
    private static EChat instance = new EChat();
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private ExecutorService executorService;
    private Configuration config;
    private JDABot jda;
    private boolean running = false;

    private EChat() {
    }

    public static void main(String[] args) {
        instance.load();

        if (instance.jda.awaitReady())
            instance.logger.info("Connected to E-Chat server.");
        else instance.stop();

        // Yes, it's hacky, but temporary ;)
        Scanner scanner = new Scanner(System.in);
        for (String next = ""; instance.running; next = scanner.next()) {
            if (next.equals("stop"))
                instance.stop();
            else instance.logger.info("Unknown command supplied, only command is \"stop\"");
        }
    }

    private void load() {
        this.running = true;

        this.logger.setUseParentHandlers(false); // remove timestamp line.
        this.executorService = Executors.newFixedThreadPool(4);
        this.config = new Configuration("config.json");
        this.jda = new JDABot();
    }

    public void stop() {
        logger.info("Stopping EChat bot...");

        jda.getJDA().shutdown();
        executorService.shutdown();

        logger.info("Stopped.");
        System.exit(0);
    }

    public Configuration getConfig() {
        return config;
    }

    public Logger getLogger() {
        return logger;
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

    public boolean isRunning() {
        return running;
    }

    public static EChat eChat() {
        return instance;
    }
}
