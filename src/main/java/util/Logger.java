package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Logger {
    private static int MAX_LENGTH_BEFORE_CONTENT = 30;
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    public static void debug(String message) {
        composeAndPrint(Level.DEBUG, message);
    }

    public static void info(String message) {
        composeAndPrint(Level.INFO, message);
    }

    public static void warn(String message) {
        composeAndPrint(Level.WARN, message);
    }

    public static void error(String message) {
        composeAndPrint(Level.ERROR, message);
    }

    public static void fatal(String message) {
        composeAndPrint(Level.FATAL, message);
    }

    public static void debugf(String message, Object... args) {
        composeAndPrint(Level.DEBUG, String.format(message, args));
    }

    public static void infof(String message, Object... args) {
        composeAndPrint(Level.INFO, String.format(message, args));
    }

    public static void warnf(String message, Object... args) {
        composeAndPrint(Level.WARN, String.format(message, args));
    }

    public static void errorf(String message, Object... args) {
        composeAndPrint(Level.ERROR, String.format(message, args));
    }

    public static void fatalf(String message, Object... args) {
        composeAndPrint(Level.FATAL, String.format(message, args));
    }

    private static void composeAndPrint(Level level, String message) {
        String levelSegmentRaw = "[" + String.format("%5s", level.name()) + "]";
        String timeSegmentRaw = TIME_FORMAT.format(new Date());

        ArrayList<String> stackTraceSegments = new ArrayList<>();
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        for (int i = 3, j = 0; i < stacks.length && j < 3; i++,j++) {
            StackTraceElement stackTraceElement = stacks[i];
            String rawClassName = stackTraceElement.getClassName();
            String className = rawClassName.substring(rawClassName.lastIndexOf(".") + 1);

            stackTraceSegments.add(String.format("%s(%d)",
                    className,
                    stackTraceElement.getLineNumber()
            ));
        }
        // reverse
        Collections.reverse(stackTraceSegments);
        String stackTraceSegmentRaw = String.join(".", stackTraceSegments);

        String levelSegment = Ansi.wrap(getLogLevelColor(level), levelSegmentRaw);
        String timeSegment = Ansi.wrap(Ansi.CYAN, timeSegmentRaw);
        String stackTraceSegment = Ansi.wrap(Ansi.PURPLE, stackTraceSegmentRaw);

        int expectedPrefixLength = levelSegment.length() + timeSegment.length() + stackTraceSegment.length() + 2;
        int padLength = 0;
        if (expectedPrefixLength < MAX_LENGTH_BEFORE_CONTENT) {
            padLength = MAX_LENGTH_BEFORE_CONTENT - expectedPrefixLength;
        }

        MAX_LENGTH_BEFORE_CONTENT = Math.max(MAX_LENGTH_BEFORE_CONTENT, expectedPrefixLength);

        String composedMessage = String.format("%s %s %s %s%s", levelSegment, timeSegment, stackTraceSegment,
                " ".repeat(padLength), message);

        System.out.println(composedMessage);
    }

    private static String getLogLevelColor(Level level) {
        switch (level) {
            case DEBUG:
                return Ansi.BLUE;
            case INFO:
                return Ansi.GREEN;
            case WARN:
                return Ansi.YELLOW;
            case ERROR:
                return Ansi.RED;
            case FATAL:
                return Ansi.PURPLE;
            default:
                return Ansi.RESET;
        }
    }

    public enum Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }
}
