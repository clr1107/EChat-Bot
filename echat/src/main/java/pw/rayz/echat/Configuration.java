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
            logger.info("Loaded the configuration from disk");
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

    public Object get(String key, Object def, boolean create) {
        String[] parts = key.split("\\.");

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

    public Integer getInteger(String key, int def, boolean create) {
        return getField(key, def, Integer.class, create);
    }

    public Integer getInteger(String key, boolean create) {
        return getField(key, Integer.class, create);
    }

    public Boolean getBoolean(String key, boolean def, boolean create) {
        return getField(key, def, Boolean.class, create);
    }

    public Boolean getBoolean(String key, boolean create) {
        return getField(key, Boolean.class, create);
    }
}
