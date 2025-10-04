package org.example;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

/**
 * Simple CSV writer that uses semicolon as separator. Appends.
 */
public final class CsvWriter {

    private final Path file;
    private final boolean headerWritten;

    public CsvWriter(String path) throws IOException {
        this.file = Paths.get(path);
        if (Files.notExists(file)) {
            Files.createDirectories(file.getParent() == null ? Paths.get(".") : file.getParent());
            Files.createFile(file);
            headerWritten = false;
        } else {
            headerWritten = Files.size(file) > 0;
        }
    }

    public synchronized void writeHeader(String headerLine) throws IOException {
        if (!headerWritten) {
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
                w.write(headerLine);
                w.newLine();
            }
        }
    }

    public synchronized void appendLine(String line) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            w.write(line);
            w.newLine();
        }
    }

}