package pw.rayz.echat.utils.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ConsoleLoggerFormatter extends Formatter {
    private static final String FORMAT = "[%s] [EChat] [%s]: %s  %s\n";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Override
    public String format(LogRecord logRecord) {
        String message = logRecord.getMessage();
        String throwable = "";

        if (logRecord.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            pw.println();
            logRecord.getThrown().printStackTrace(pw);

            pw.close();
            throwable = sw.toString();
        }

        String time = LocalDateTime.now().format(formatter);
        return String.format(FORMAT, time, logRecord.getLevel().getLocalizedName(), message, throwable);
    }

}
