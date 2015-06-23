package edu.warbot.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Created by beugnon on 22/06/15.
 */
public class WarbotOptions extends Options {

    private Option helpOption = new Option("help","print this message");

    private

    public WarbotOptions() {
        addOption(helpOption);
    }
}
