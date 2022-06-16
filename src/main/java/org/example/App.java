package org.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

    private record Argument(int listenPort, String documentRoot) {
    }

    public static void main(String[] args) {
        Argument argument = parseCommandLineArguments(args);
        try {
            new Http11Server(argument.listenPort, argument.documentRoot).start();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    private static Argument parseCommandLineArguments(String[] args) {
        Options options = new Options();

        Option port = new Option("p", "port", true, "listen port");
        port.setRequired(false);
        options.addOption(port);

        Option htdocs = new Option("l", "docroot", true, "directory of html documents");
        htdocs.setRequired(true);
        options.addOption(htdocs);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printUsage(options);
        }

        int listenPort = 10080;
        try {
            listenPort = Integer.parseInt(cmd.getOptionValue("port", "12345"));
        } catch (NumberFormatException e) {
            printUsage(options);
        }

        return new Argument(listenPort, cmd.getOptionValue("docroot"));
    }

    private static void printUsage(Options options) {
        new HelpFormatter().printHelp("utility-name", options);
        System.exit(1);
    }
}
