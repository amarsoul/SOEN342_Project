package ats.controller;
import ats.domain.DomainException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this is the class that reads and writes the csv files, first line is the header
public class CsvFile {

    private final Map<String, Integer> columns = new HashMap<>();
    private final List<List<String>> rows = new ArrayList<>();
    private final List<Integer> lineNumbers = new ArrayList<>();


    // read the whole file, nothing is imported if it cant be read
    public static CsvFile read(String filePath){
        List<String> lines;

        try {
            lines = Files.readAllLines(Path.of(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DomainException("cannot read " + filePath + " (nothing imported)");
        }

        if (lines.isEmpty()) {
            throw new DomainException("the file is empty");
        }

        CsvFile csv = new CsvFile();

        // remove the invisible char that excel put at the start
        String header = lines.get(0);
        if (header.startsWith("\uFEFF")) {
            header = header.substring(1);
        }

        List<String> names = parseLine(header);
        for (int i = 0; i < names.size(); i++) {
            csv.columns.put(names.get(i).trim().toLowerCase(), i);
        }

        for (int i = 1; i < lines.size(); i++){
            if (lines.get(i).isBlank()) {
                continue;
            }
            csv.rows.add(parseLine(lines.get(i)));
            csv.lineNumbers.add(i + 1);
        }

        return csv;
    }



    // write the lines in the file, create the folder if needed
    public static void write(String filePath, List<String> lines) {
        try {
            Path path = Path.of(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DomainException("cannot write " + filePath + ": " + e.getMessage());
        }
    }

    public boolean hasColumn(String name) {
        return columns.containsKey(name.toLowerCase());
    }

    public int rowCount() {
        return rows.size();
    }


   // the line number in the file, for error messages
    public int lineNumber(int row) {
        return lineNumbers.get(row);
    }


    // get value of a column in a row, error if missing or empty
    public String get(int row, String column){
        List<String> fields = rows.get(row);
        Integer index = columns.get(column.toLowerCase());

        if (index == null || index >= fields.size() || fields.get(index).isBlank()) {
            throw new DomainException("malformed (missing " + column + ")");
        }

        return fields.get(index).trim();
    }



    // put the value in quotes if it has a comma or quote
    public static String field(String s) {
        if (s == null) {
            return "";
        }

        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }

        return s;
    }

    // split one line into fields, commas inside quotes are kept
    private static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (quoted) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else if (c == '"') {
                    quoted = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                quoted = true;
            } else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString());
        return fields;
    }

}