package edu.warbot.scriptcore.exceptions;

public class NotFoundScriptLanguageException extends Exception {

    public NotFoundScriptLanguageException(String teamName) {
        super("We can't found the script language for " + teamName);
    }
}
