package pw.rayz.echat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class Configuration {
    private final Logger logger = Logger.getLogger("EChat-Bot");
    private final File file;
    private final Gson gson = new Gson();
    private Map<String, Object> fields;
    private Set<Runnable> loadTasks = Collections.synchronizedSet(new HashSet<>());

    Configuration(String fileName) {
        this.file = createAndGetFile(fileName);
        load();
    }

    private File createAndGetFile(String fileName) {
        File folder = EChat.eChat().getFolder().getParentFile();
        File file = new File(folder, fileName);

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
            logger.severe("IOException whilst creating configuration file: " + fileName);
        }

        return file;
    }

    public synchronized void load() {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Type type = new TypeToken<ConcurrentHashMap<String, Object>>() {
            }.getType();
            fields = gson.fromJson(bufferedReader, type);

            loadTasks.forEach(Runnable::run); // run all the tasks.
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            logger.severe("Could not find configuration file.");
        }
    }

    public void addLoadTask(Runnable runnable) {
        loadTasks.add(runnable);
    }

    public void removeLoadTask(Runnable runnable) {
        loadTasks.remove(runnable);
    }

    public Object get(String key, Object def) {
        System.out.println(fields.toString());
        return fields != null ? fields.getOrDefault(key, def) : def;
    }

    public Object get(String key) {
        return get(key, null);
    }

    public <T> T getField(String key, T def, Class<T> type) {
        Object o = get(key);

        if (type.isInstance(o))
            return type.cast(o);
        else return def;
    }

    public <T> T getField(String key, Class<T> type) {
        return getField(key, null, type);
    }

    public String getString(String key) {
        return getField(key, String.class);
    }

    public String getString(String key, String def) {
        return getField(key, def, String.class);
    }

    public Integer getInteger(String key, int def) {
        return getField(key, def, Integer.class);
    }

    public Integer getInteger(String key) {
        return getField(key, Integer.class);
    }

    public Boolean getBoolean(String key, boolean def) {
        return getField(key, def, Boolean.class);
    }

    public Boolean getBoolean(String key) {
        return getField(key, Boolean.class);
    }
}
