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

        // Пример входных данных
        String[] inputStrings = {
                "111;123;222",
                "200;123;100",
                "300;;100",
                "100;200;300",
                "200;300;100"
        };

        // Создание уникального множества строк
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

        // Сортировка групп по количеству элементов
        List<Map.Entry<Integer, List<String>>> sortedGroups = new ArrayList<>(groups.entrySet());
        sortedGroups.sort(Comparator.comparingInt(entry -> entry.getValue().size()));
        Collections.reverse(sortedGroups);

        // Вывод в файл
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

    /*public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar {название проекта}.jar тестовый-файл.txt");
            return;
        }

        File file = new File(args[0]);
        Map<String, Set<String>> groups = new HashMap<>();
        Pattern pattern = Pattern.compile("(?=(\\\"[0-9]+\\\";?))\\1+");
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
                int count = (int) m.results().count();
                if (count == values.length) {
                    String key = Arrays.toString(values);
                    groups.computeIfAbsent(key, k -> new HashSet<>()).add(Arrays.toString(values));
                }
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
    }*/
}