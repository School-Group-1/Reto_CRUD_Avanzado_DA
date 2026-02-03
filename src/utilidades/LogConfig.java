package utilidades;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

public class LogConfig {

    private static final Logger LOGGER = Logger.getLogger("");

    public static void setup() throws IOException {
        String logsDir = "logs";
        Files.createDirectories(Paths.get(logsDir));

        String path = logsDir + File.separator + "logs.log";
        FileHandler fileHandler = new FileHandler(path, true);
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.INFO);

        LOGGER.setLevel(Level.INFO);
        LOGGER.addHandler(fileHandler);
    }
}
