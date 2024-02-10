import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args != null && args.length == 2 && args[0] != null && args[1] != null) {
            Path watchPath = Paths.get(args[0]);
            Path logFile = Paths.get(args[1]);
            if (Files.isDirectory(watchPath)) {
                System.out.println("File-Watch-Directory successfully started!");
                System.out.println("Watch directory: " + watchPath);
                System.out.println("Log File: " + logFile);
                try (WatchService watchService = FileSystems.getDefault().newWatchService();
                     FileWriter fileWriter = new FileWriter(logFile.toFile(), true)) {

                    watchPath.register(watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE
                            //StandardWatchEventKinds.ENTRY_MODIFY
                    );

                    for (;;) {
                        WatchKey watchKey;
                        try {
                            watchKey = watchService.take();
                        } catch (InterruptedException e) {
                            break;
                        }

                        for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                            WatchEvent.Kind<?> kind = watchEvent.kind();

                            if (kind == StandardWatchEventKinds.OVERFLOW) {
                                continue;
                            }

                            WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
                            Path pathWatch = pathWatchEvent.context();
                            String output = LocalDateTime.now() + "; " + kind + "; "
                                    + pathWatch.getFileName() + "; " + Files.size(watchPath) / 1024 + "\n";
                            fileWriter.append(output);
                            fileWriter.flush();

                            if (!watchKey.reset()) {
                                break;
                            }
                        }

                    }
                }
            } else {
                System.out.println("No correct watch directory!");
            }
        } else {
            System.out.println("Arguments not correct! Use following syntax: \"C:\\Temp\\test\\\" \"C:\\test.csv\"");
        }
    }
}