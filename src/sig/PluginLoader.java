package sig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginLoader {
    public static void LoadPlugins() {
        Path p = Path.of("..","plugins");
        try {
            Files.walk(p).filter((f)->Files.isRegularFile(f)).forEach((f)->{System.out.println("Found "+f);});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
