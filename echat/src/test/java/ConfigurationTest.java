import org.junit.jupiter.api.Test;
import pw.rayz.echat.Configuration;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationTest {

    private static Configuration prepareConfiguration() {
        try {
            ReflectionFactory factory = ReflectionFactory.getReflectionFactory();
            Constructor<?> constructor = factory.newConstructorForSerialization(Configuration.class, Object.class.getDeclaredConstructor(new Class[0]));
            Configuration c = (Configuration) constructor.newInstance(new Object[0]);

            Map<String, Object> fieldsMap = new ConcurrentHashMap<>();
            Map<String, Object> secondMap = new ConcurrentHashMap<>();


            fieldsMap.put("alpha", 5);
            fieldsMap.put("beta", secondMap);
            secondMap.put("alpha", 6);

            Field fieldsField = c.getClass().getDeclaredField("fields");
            fieldsField.setAccessible(true);
            fieldsField.set(c, fieldsMap);
            return c;
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    @Test
    void testRegularConfiguration() {
        Configuration configuration = prepareConfiguration();

        if (configuration != null) {
            assertNotNull(configuration.get("alpha", false));
        }
    }

    @Test
    void testCorrectType() {
        Configuration configuration = prepareConfiguration();

        if (configuration != null) {
            assertEquals(configuration.getInt("alpha", false), 5);
        }
    }

    @Test
    void testEmbeddedConfiguration() {
        Configuration configuration = prepareConfiguration();

        if (configuration != null) {
            assertNotNull(configuration.get("beta.alpha", false));
        }
    }
}
