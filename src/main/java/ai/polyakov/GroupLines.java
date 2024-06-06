package ai.polyakov;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupLines {

    private static final Pattern pattern = Pattern.compile("(?=(\\\"[0-9]+\\\";?))\\1+");
    public static void main(String[] args) {
        String inputFilePath = args[0];
        try {
            List<Set<String>> groups = groupLines(inputFilePath);
            printGroups(groups);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Set<String>> groupLines(String filePath) throws IOException {
        Map<String, Set<String>> valueToGroupMap = new HashMap<>();
        List<Set<String>> groups = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isValidLine(line)) {
                    String[] values = line.split(";");
                    Set<String> currentGroup = new HashSet<>();
                    for (String value : values) {
                        if (!value.isEmpty()) {
                            if (valueToGroupMap.containsKey(value)) {
                                currentGroup = valueToGroupMap.get(value);
                                break;
                            } else {
                                valueToGroupMap.put(value, currentGroup);
                            }
                        }
                    }
                    if ((!currentGroup.isEmpty()) && (!groups.contains(currentGroup))) {
                        System.out.println("Группа: " + groups);
                        System.out.println();
                        groups.add(currentGroup);
                    }
                }
            }
        }
        return groups;
    }

    private static boolean isValidLine(String line) {
        Matcher m = pattern.matcher(line);
        int count = (int) m.results().count();
        String[] values = line.split(";");

        return count == values.length;
    }

    private static void printGroups(List<Set<String>> groups) throws IOException {
        groups.sort(Comparator.<Set<String>>comparingInt(Set::size).reversed());
        int groupCount = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            for (Set<String> group : groups) {
                if (group.size() > 1) {
                    groupCount++;
                    writer.write("Группа " + groupCount);
                    writer.newLine();
                    for (String line : group) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.newLine();
                }
            }
            writer.write("Число групп с более чем одним элементом: " + groupCount);
        }
    }
}
