package org.nico.quoted.config;

public class Logger {
    public static final System.Logger LOGGER = System.getLogger(Logger.class.getName());
    public static final System.Logger.Level INFO = System.Logger.Level.INFO;
    public static final System.Logger.Level DEBUG = System.Logger.Level.DEBUG;
    public static final System.Logger.Level WARNING = System.Logger.Level.WARNING;
    public static final System.Logger.Level ERROR = System.Logger.Level.ERROR;
}
