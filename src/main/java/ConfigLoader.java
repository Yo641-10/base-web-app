import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

import static java.lang.System.out;

public class ConfigLoader {
    private static HashMap<String, String> configMap = new HashMap<>();
    public static String HOST = "ip";
    public static String PORT = "port";
    static {
    }

    public static void loadConfig(String filename) {
        try (Stream<String> fileStream = Files.lines(Paths.get(filename))) {
            HashMap<String, String> newConfigMap = new HashMap<>();
            fileStream.forEach(line -> {
                line = line.replaceAll("#.*", "");
                if (line.length() > 0 && line.contains("=")) {
                    String[] kv = line.split("=");
                    newConfigMap.put(kv[0], kv[1]);
                }
            });
            configMap.putAll(newConfigMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetHashMap() {
        configMap = new HashMap<>();
    }

    public static String getValue(String key) {
        return configMap.get(key);
    }

    public static HashMap<?, ?> getHashMap() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.putAll(configMap);
        return hashMap;
    }

    public static void main(String[] args) throws Exception {
        loadConfig("config/config.dat");
        getHashMap().entrySet().stream().forEach(out::println);
    }
}
