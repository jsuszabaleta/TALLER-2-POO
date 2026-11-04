package org.example;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.LogManager;

public class NivelLog extends Level {

    public static final Level INFO = Level.INFO;
    public static final Level WARNING = Level.WARNING;
    public static final Level ERROR = new NivelLog("ERROR", Level.SEVERE.intValue());

    static {
        try {
            Files.createDirectories(Path.of("target"));
            try (InputStream is = NivelLog.class.getClassLoader().getResourceAsStream("logging.properties")) {
                if (is != null) {
                    LogManager.getLogManager().readConfiguration(is);
                }
            }
        } catch (Exception ignored) {
        }
    }

    protected NivelLog(String name, int value) {
        super(name, value);
    }
}