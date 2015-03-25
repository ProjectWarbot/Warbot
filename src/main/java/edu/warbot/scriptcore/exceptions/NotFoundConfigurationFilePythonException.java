package edu.warbot.scriptcore.exceptions;

/**
 * Created by jimmy on 12/03/15.
 */
public class NotFoundConfigurationFilePythonException extends Exception {
    public NotFoundConfigurationFilePythonException (String pythonFile) {
        super("We can't not found the configuration file python : " + pythonFile);
    }
}
