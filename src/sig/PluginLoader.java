package sig;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PluginLoader {
    public static void LoadPlugins() {
        Path p = Path.of("..","plugins");
        try {
            Files.walk(p).filter((f)->Files.isRegularFile(f)&&f.getFileName().toString().contains(".java")).forEach((f)->{
                System.out.println(LoadPlugin(f));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List getClasseNames(String jarName) {
        boolean debug=true;
        ArrayList classes = new ArrayList();
    
        if (debug)
            System.out.println("Jar " + jarName );
        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(
                    jarName));
            JarEntry jarEntry;
    
            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if (jarEntry.getName().endsWith(".class")) {
                    if (debug)
                        System.out.println("Found "
                                + jarEntry.getName().replaceAll("/", "\\."));
                    classes.add(jarEntry.getName().replaceAll("/", "\\."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static String LoadPlugin(Path f) {
        try {
            System.out.println("Loading "+f);
            System.out.println("  Compiling "+f.getParent().toAbsolutePath()+"...");
            Process process = Runtime.getRuntime().exec(new String[]{"javac","../plugins/TestPlugin.java"});
            //Process process = Runtime.getRuntime().exec("jar cfm TestPlugin.jar ../manifest .");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder line=new StringBuilder();
            String lin;
            while ((lin = reader.readLine()) != null) {
                line.append(lin).append("\n");
            }
            process = Runtime.getRuntime().exec(new String[]{"jar","cfm","TestPlugin.jar","../manifest","../plugins/TestPlugin.class"});
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((lin = reader.readLine()) != null) {
                line.append(lin).append("\n");
            }
            URL l = Paths.get(f.getFileName().toString().replace(".java","")+".jar").toUri().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{l});
            Object newPlugin = Class.forName(f.getFileName().toString().replace(".java",""),true,loader).getDeclaredConstructor().newInstance();
            return line.toString();
        } catch (IllegalArgumentException | SecurityException | IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
