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
    public static final String DELIMITER = "\\.";
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
            logger.info("Loaded the configuration from disk");
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            logger.severe("Could not find configuration file.");
        }
    }

    public void addLoadTask(Runnable runnable, boolean runNow) {
        loadTasks.add(runnable);

        if (runNow)
            runnable.run();
    }

    public void removeLoadTask(Runnable runnable) {
        loadTasks.remove(runnable);
    }

    private Object get(String key, Object def, boolean create) {
        String[] parts = key.split(DELIMITER);

        Map currentMap = fields;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            Object nextO = currentMap.get(part);

            if (nextO instanceof Map) {
                currentMap = (Map) nextO;
            } else if (nextO == null) {
                if (create)
                    currentMap.put(part, new ConcurrentHashMap<String, Object>());
                else return def;
            } else return def;
        }

        String finalPart = parts[parts.length - 1];
        Object finalO = currentMap.get(finalPart);

        if (finalO == null) {
            if (create) {
                finalO = new ConcurrentHashMap<String, Object>();
                currentMap.put(finalPart, finalO);
            } else return def;
        }

        return finalO;
    }

    public Object get(String key, boolean create) {
        return get(key, null, create);
    }

    public <T> T getField(String key, T def, Class<T> type, boolean create) {
        Object o = get(key, def, create);

        if (type.isInstance(o)) {
            return type.cast(o);
        } else return def;
    }

    public <T> T getField(String key, Class<T> type, boolean create) {
        return getField(key, null, type, create);
    }

    public String getString(String key, boolean create) {
        return getField(key, String.class, create);
    }

    public String getString(String key, String def, boolean create) {
        return getField(key, def, String.class, create);
    }

    public Double getDouble(String key, boolean create) {
        return getField(key, -1D, Double.class, create);
    }

    public Double getDouble(String key, double def, boolean create) {
        return getField(key, def, Double.class, create);
    }

    public Integer getInt(String key, boolean create) {
        return getInt(key, -1, create);
    }

    public Integer getInt(String key, int def, boolean create) {
        return getDouble(key, def, create).intValue();
    }

    public Long getLong(String key, boolean create) {
        return getLong(key, -1L, create);
    }

    public Long getLong(String key, long def, boolean create) {
        return getDouble(key, def, create).longValue();
    }

    public Boolean getBoolean(String key, boolean def, boolean create) {
        return getField(key, def, Boolean.class, create);
    }

    public Boolean getBoolean(String key, boolean create) {
        return getField(key, Boolean.class, create);
    }
}
