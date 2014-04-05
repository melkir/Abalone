package com.github.abalone.config;

import com.github.abalone.view.Window;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author melkir
 */
public class Theme extends ConstraintValue<String> {
    private static HashSet<String> list = null;

    public Theme(String description, String value) {
        super(description, value);
    }

    @Override
    protected void initConstraint() {
        if (Theme.list == null) {
            Theme.list = new HashSet<String>();

            String jarPath = null;
            try {
                jarPath = Window.class.getResource("game").getPath();
                jarPath = jarPath.substring(5, jarPath.indexOf("!"));
            } catch (StringIndexOutOfBoundsException e) {
                // TODO Replace this ugly fix !
                jarPath = getClass().getClassLoader().getResource("./").getPath();
                jarPath = jarPath.substring(0, jarPath.length()-8) + "Abalone-1.0-SNAPSHOT-jar-with-dependencies.jar";
            }

            JarFile jar = null;
            try {
                jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            } catch (IOException ex) {
                Logger.getLogger(Theme.class.getName()).log(Level.SEVERE, null,
                        ex);
                System.exit(1);
            }

            Enumeration<JarEntry> jarContent = jar.entries();
            while (jarContent.hasMoreElements()) {
                JarEntry entry = jarContent.nextElement();
                String fileName = entry.getName();
                if (!entry.isDirectory() || !fileName.contains("game")
                        || fileName.endsWith("game/"))
                    continue;
                String[] theme = fileName.split("/");
                Theme.list.add(theme[theme.length - 1]);
            }
        }
    }

    @Override
    protected Boolean check(String value) {
        return Theme.list.contains(value);
    }

    @Override
    public Set<String> getList() {
        return Collections.unmodifiableSet(Theme.list);
    }
}
