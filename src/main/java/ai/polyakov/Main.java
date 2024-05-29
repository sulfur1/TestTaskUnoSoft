package ai.polyakov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
        if (args.length != 1) {
            System.out.println("Usage: java -jar {название проекта}.jar тестовый-файл.txt");
            return;
        }

        File file = new File(args[0]);
        Map<String, Set<String>> groups = new HashMap<>();
        Pattern pattern = Pattern.compile("(?=(\"[0-9]+\";?))\\1+");
        Matcher m;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                m = pattern.matcher(line);
                System.out.println(m.find(0));
                if (!(m.matches())) {
                    continue;
                }
                String[] values = line.split(";");
                String key = Arrays.toString(values);
                groups.computeIfAbsent(key, k -> new HashSet<>()).add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        List<Set<String>> groupList = new ArrayList<>(groups.values());
        groupList.sort(Comparator.<Set<String>>comparingInt(Set::size).reversed());

        try (FileWriter writer = new FileWriter("output.txt")) {
            int groupCount = 0;
            for (Set<String> group : groupList) {
                if (group.size() > 1) {
                    groupCount++;
                    writer.write("группа " + groupCount + "\n");
                    for (String line : group) {
                        writer.write(line + "\n");
                    }
                    writer.write("\n");
                }
            }
            System.out.println("Количество групп с более чем одним элементом: " + groupCount);
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }
}