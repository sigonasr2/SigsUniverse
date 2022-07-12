package sig;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginLoader {
    public static void LoadPlugins() {
        Path p = Path.of("..","plugins");
        try {
            Files.walk(p).filter((f)->Files.isRegularFile(f)&&f.getFileName().toString().contains(".jar")).forEach((f)->{
                LoadPlugin(f);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void LoadPlugin(Path f) {
        try {
            System.out.println("Loading "+f);
            URL l = f.toUri().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{l});
            Object newPlugin = Class.forName(f.getFileName().toString().replace(".jar",""),true,loader).getDeclaredConstructor().newInstance();
        } catch (MalformedURLException | InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }
}
