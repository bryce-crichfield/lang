package lang.command;

import org.apache.commons.cli.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandLineInterface {
    private CommandLineParser parser;
    private Options options;

    public CommandLineInterface() {
        parser = new DefaultParser();
        options = new Options();
    }

    public CommandData parse(String[] args) throws Exception {
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("slang", options);
        }

        File outputFile = new File("a.out");
        if (cmd.hasOption("o")) {
            String output = cmd.getOptionValue("o");
            outputFile = new File(output);
        }
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        List<File> libraryFiles = new ArrayList<>();
        if (cmd.hasOption("l")) {
            String[] libraries = cmd.getOptionValues("l");
            for (String library : libraries) {
                File libraryFile = new File(library);
                if (!libraryFile.exists()) {
                    throw new Exception("Library file does not exist: " + library);
                }
                libraryFiles.add(libraryFile);
            }
        }

        String[] remaining = cmd.getArgs();
        List<File> inputFiles = new ArrayList<>();
        for (String arg : remaining) {
            File inputFile = new File(arg);
            if (!inputFile.exists()) {
                throw new Exception("Input file does not exist: " + arg);
            }
            inputFiles.add(inputFile);
        }

        return new CommandData(inputFiles, outputFile, libraryFiles);
    }

}
