package lang.command;

import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class CommandData {
    private final List<File> inputs;
    private final File output;
    private final List<File> libraries;
}
