package ai.polyakov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        Map<Integer, List<String>> groups = new HashMap<>();

        if (args.length != 1) {
            System.out.println("Usage: java -jar {название проекта}.jar тестовый-файл.txt");
            return;
        }

        File file = new File(args[0]);
        Pattern pattern = Pattern.compile("(?=(\\\"[0-9]+\\\";?))\\1+");
        Matcher m;
        List<String> stringList = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                m = pattern.matcher(line);
                String[] values = line.split(";");
                int count = (int) m.results().count();
                if (count == values.length) {
                    stringList.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        // Create unuque set strings
        Set<String> uniqueStrings = new HashSet<>(stringList);

        // Группировка строк
        for (String str : uniqueStrings) {
            boolean added = false;
            for (Map.Entry<Integer, List<String>> entry : groups.entrySet()) {
                List<String> group = entry.getValue();
                for (String existingStr : group) {
                    if (haveCommonValues(str, existingStr)) {
                        group.add(str);
                        added = true;
                        break;
                    }
                }
            }
            if (!added) {
                List<String> newGroup = new ArrayList<>();
                newGroup.add(str);
                groups.put(groups.size() + 1, newGroup);
            }
        }

        // Sort by count elements
        List<Map.Entry<Integer, List<String>>> sortedGroups = new ArrayList<>(groups.entrySet());
        sortedGroups.sort(Comparator.comparingInt(entry -> entry.getValue().size()));
        Collections.reverse(sortedGroups);

        // Output to file
        try (PrintWriter writer = new PrintWriter("output.txt")) {
            for (Map.Entry<Integer, List<String>> entry : sortedGroups) {
                List<String> group = entry.getValue();
                if (group.size() > 1) {
                    writer.println("Группа " + entry.getKey());
                    for (String str : group) {
                        writer.println(str);
                    }
                    writer.println();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Ошибка при записи в файл");
        }
    }

    private static boolean haveCommonValues(String str1, String str2) {
        String[] values1 = str1.split(";");
        String[] values2 = str2.split(";");
        for (int i = 0; i < values1.length; i++) {
            if (values2.length == i) {
                return false;
            }
            if (!values2[i].isEmpty() && !values1[i].isEmpty() && values1[i].equals(values2[i])) {
                return true;
            }
        }
        return false;
    }
}
