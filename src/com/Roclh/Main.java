package com.Roclh;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Main {

    static int profIter=0;
    // args: filename, tableName, amount,args(paramName:paramType[optional])
    public static void main(String[] args) {
        String[] values = new String[args.length - 3];
        String filename = args[0];
        String tableName = args[1];
        int amount = Integer.parseInt(args[2]);
        System.arraycopy(args, 3, values, 0, args.length - 3);
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        ArrayList<HashMap<String, String>> parents = new ArrayList<>();
        ArrayList<HashMap<String, String>> children = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            result.add(fillValues(values));
        }
        parents = (ArrayList<HashMap<String, String>>)result.stream().filter(map->{
            if(map.get("birth_date")!=null&&!map.get("birth_date").equals("NULL")){
                return LocalDate.parse(map.get("birth_date").replace("'","")).isBefore(LocalDate.of(2029, 1, 1));
            }else return false;
        }).collect(Collectors.toList());
        children = (ArrayList<HashMap<String, String>>)result.stream().filter(map->{
            if(map.get("birth_date")!=null&&!map.get("birth_date").equals("NULL")){
                return LocalDate.parse(map.get("birth_date").replace("'","")).isAfter(LocalDate.of(2029, 1, 1));
            }else return false;
        }).collect(Collectors.toList());
        System.out.println("Parents");
        System.out.println(parents);
        System.out.println("Children");
        System.out.println(children);
        ArrayList<HashMap<String, String>> finalParents = parents;
        ArrayList<HashMap<String, String>> resultChild = new ArrayList<>();
        ArrayList<HashMap<String, String>> finalChildren = children;
        children.forEach(map->{
            Integer parent1 = (int)Math.floor(Math.random()* finalParents.size());
            Integer parent2 = (int)Math.floor(Math.random()* finalParents.size());
            HashMap<String, String> child = new HashMap<>();
            if((LocalDate.parse(map.get("birth_date").replace("'","")).toEpochDay()-LocalDate.parse(finalParents.get(parent1).get("birth_date").replace("'","")).toEpochDay() )
                    <= (LocalDate.of(2029, 1, 1).toEpochDay()-LocalDate.of(2019,1,1).toEpochDay())){
                parent1 = null;
            }
            if((LocalDate.parse(map.get("birth_date").replace("'","")).toEpochDay()-LocalDate.parse(finalParents.get(parent2).get("birth_date").replace("'","")).toEpochDay() )
                    <= (LocalDate.of(2029, 1, 1).toEpochDay()-LocalDate.of(2019,1,1).toEpochDay())){
                parent2 = null;
            }
            child.put("parent1_id", parent1==null?"NULL":parent1.toString());
            child.put("parent2_id", parent2==null?"NULL":parent2.toString());
            child.put("child_id", String.valueOf(finalChildren.indexOf(map)));
            resultChild.add(child);
        });
        System.out.println(resultChild);
        File childs = new File("Descendants.txt");
        File file = new File(filename);
        try {
            FileWriter childWriter = new FileWriter(childs, false);
            FileWriter fileWriter = new FileWriter(file, false);
            resultChild.stream().forEach(child->{
                try {
                    childWriter.write(mapToInsertion(tableName, child) + "\r\n");
                    childWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            result.stream().forEach(res -> {
                try {
                    fileWriter.write(mapToInsertion(tableName, res) + "\r\n");
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    *

*
* ArrayList<String> list = new ArrayList<>();
        try {
            File file = new File("FemaleNames.txt");
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                list.add(sc.nextLine());
            }
            sc.close();
            for(int i=0; i<list.size(); i++){
                if(list.get(i).contains("\t")){
                    list.set(i,list.get(i).split("\t")[2]);
                }
            }
            FileWriter fileWriter = new FileWriter(file, false);
            list.forEach(s -> {
                try {
                    fileWriter.write(s+"\r\n");
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    * */

    public static HashMap<String, String> fillValues(String[] values) {
        HashMap<String, String> map = new HashMap<>();
        ArrayList<Integer> unique = new ArrayList<>();
        ArrayList<String> professions = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File("ProfessionsNames.txt"));
            while (sc.hasNextLine()){
                professions.add(sc.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LocalDate birthDate = null;
        LocalDate deathDate = null;
        Boolean sex = null;
        for (String s : values) {
            String[] value = s.split(":");
            map.put(value[0], value[1]);
            if (value[1].contains("nullable")) {
                if (Math.random() >= 0.8d) {
                    map.put(value[0], "NULL");
                    continue;
                }
            }
            if (value[1].equals("name")) {
                try {
                    map.put(value[0], "'" + getRandomValueFromFile("MaleNames.txt") + "'");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (value[1].equals("planet")) {
                try {
                    map.put(value[0], "'" + getRandomValueFromFile("Planet.txt") + "'");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else if (value[1].equals("profession")) {
                map.put(value[0], "'" + professions.get(profIter) + "'");
                profIter++;
            }else if (value[1].equals("surname")) {
                try {
                    map.put(value[0], "'" + getRandomValueFromFile("Surnames.txt") + "'");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (value[1].equals("fullname")) {
                try {
                    if (sex != null) {
                        if (sex) {
                            map.put(value[0], "'" + getRandomValueFromFile("MaleNames.txt") + " " + getRandomValueFromFile("Surnames.txt") + "'");
                        } else {
                            map.put(value[0], "'" + getRandomValueFromFile("FemaleNames.txt") + " " + getRandomValueFromFile("Surnames.txt") + "'");
                        }
                    } else {
                        sex = Math.random() > 0.5f;
                        if (sex) {
                            map.put(value[0], "'" + getRandomValueFromFile("MaleNames.txt") + " " + getRandomValueFromFile("Surnames.txt") + "'");
                        } else {
                            map.put(value[0], "'" + getRandomValueFromFile("FemaleNames.txt") + " " + getRandomValueFromFile("Surnames.txt") + "'");
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (value[1].contains("int")) {
                if(value[1].contains("unique")){
                    String limits = value[1].substring(value[1].indexOf("[") + 1, value[1].indexOf("]"));
                    int min = Integer.parseInt(limits.split(";")[0]);
                    int max = Integer.parseInt(limits.split(";")[1]);
                    map.put(value[0], String.valueOf((int) Math.floor(Math.random() * (max - min) + min)));
                    while(unique.contains(Integer.parseInt(map.get(value[0])))){
                        map.put(value[0], String.valueOf((int) Math.floor(Math.random() * (max - min) + min)));
                    }
                    unique.add(Integer.parseInt(map.get(value[0])));
                    continue;
                }
                String limits = value[1].substring(value[1].indexOf("[") + 1, value[1].indexOf("]"));
                int min = Integer.parseInt(limits.split(";")[0]);
                int max = Integer.parseInt(limits.split(";")[1]);
                map.put(value[0], String.valueOf((int) Math.floor(Math.random() * (max - min) + min)));
            } else if (value[1].contains("double")) {
                String limits = value[1].substring(value[1].indexOf("[") + 1, value[1].indexOf("]"));
                double min = Double.parseDouble(limits.split(";")[0]);
                double max = Double.parseDouble(limits.split(";")[1]);
                map.put(value[0], String.valueOf(Math.random() * (max - min) + min));
            } else if (value[1].contains("boolean")) {
                if(value[0].contains("sex")){
                    if(sex!=null){
                        map.put(value[0], String.valueOf(sex).toUpperCase());
                        continue;
                    }
                }
                map.put(value[0], String.valueOf(Math.random() > 0.5f).toUpperCase());
            } else if (value[1].contains("birthdate")) {
                if (deathDate != null) {
                    String limits = value[1].substring(value[1].indexOf("[") + 1, value[1].indexOf("]"));
                    LocalDate startDate = LocalDate.of(Integer.parseInt(limits.split(";")[0].split("-")[0]),
                            Integer.parseInt(limits.split(";")[0].split("-")[1]),
                            Integer.parseInt(limits.split(";")[0].split("-")[2])); //start date
                    long start = startDate.toEpochDay();
                    LocalDate endDate = LocalDate.of(Integer.parseInt(deathDate.toString().split("-")[0]),
                            Integer.parseInt(deathDate.toString().split("-")[1]),
                            Integer.parseInt(deathDate.toString().split("-")[2])); //end date
                    long end = endDate.toEpochDay();
                    long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
                    birthDate = LocalDate.ofEpochDay(randomEpochDay);
                    map.put(value[0], "'" + LocalDate.ofEpochDay(randomEpochDay).toString() + "'");
                } else {
                    String limits = value[1].substring(value[1].indexOf("[") + 1, value[1].indexOf("]"));
                    LocalDate startDate = LocalDate.of(Integer.parseInt(limits.split(";")[0].split("-")[0]),
                            Integer.parseInt(limits.split(";")[0].split("-")[1]),
                            Integer.parseInt(limits.split(";")[0].split("-")[2])); //start date
                    long start = startDate.toEpochDay();

                    LocalDate endDate = LocalDate.of(Integer.parseInt(limits.split(";")[1].split("-")[0]),
                            Integer.parseInt(limits.split(";")[1].split("-")[1]),
                            Integer.parseInt(limits.split(";")[1].split("-")[2])); //end date
                    long end = endDate.toEpochDay();

                    long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
                    birthDate = LocalDate.ofEpochDay(randomEpochDay);
                    map.put(value[0], "'" + LocalDate.ofEpochDay(randomEpochDay).toString() + "'");
                }
            } else if (value[1].contains("deathdate")) {
                if (Math.random() >= 0.7d) {
                    if (birthDate != null) {
                        String limits = value[1].substring(value[1].indexOf("[") + 1, value[1].indexOf("]"));
                        LocalDate startDate = LocalDate.of(Integer.parseInt(birthDate.toString().split("-")[0]),
                                Integer.parseInt(birthDate.toString().split("-")[1]),
                                Integer.parseInt(birthDate.toString().split("-")[2])); //start date
                        long start = startDate.toEpochDay();
                        LocalDate endDate = LocalDate.of(Integer.parseInt(limits.split(";")[1].split("-")[0]),
                                Integer.parseInt(limits.split(";")[1].split("-")[1]),
                                Integer.parseInt(limits.split(";")[1].split("-")[2])); //end date
                        long end = endDate.toEpochDay();
                        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
                        deathDate = LocalDate.ofEpochDay(randomEpochDay);
                        map.put(value[0], "'" + LocalDate.ofEpochDay(randomEpochDay).toString() + "'");
                    } else {
                        String limits = value[1].substring(value[1].indexOf("[") + 1, value[1].indexOf("]"));
                        LocalDate startDate = LocalDate.of(Integer.parseInt(limits.split(";")[0].split("-")[0]),
                                Integer.parseInt(limits.split(";")[0].split("-")[1]),
                                Integer.parseInt(limits.split(";")[0].split("-")[2])); //start date
                        long start = startDate.toEpochDay();

                        LocalDate endDate = LocalDate.of(Integer.parseInt(limits.split(";")[1].split("-")[0]),
                                Integer.parseInt(limits.split(";")[1].split("-")[1]),
                                Integer.parseInt(limits.split(";")[1].split("-")[2])); //end date
                        long end = endDate.toEpochDay();

                        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
                        deathDate = LocalDate.ofEpochDay(randomEpochDay);
                        map.put(value[0], "'" + LocalDate.ofEpochDay(randomEpochDay).toString() + "'");
                    }
                } else {
                    map.put(value[0], "NULL");
                }
            } else if (value[1].contains("date")) {
                String limits = value[1].substring(value[1].indexOf("[") + 1, value[1].indexOf("]"));
                LocalDate startDate = LocalDate.of(Integer.parseInt(limits.split(";")[0].split("-")[0]),
                        Integer.parseInt(limits.split(";")[0].split("-")[1]),
                        Integer.parseInt(limits.split(";")[0].split("-")[2])); //start date
                long start = startDate.toEpochDay();

                LocalDate endDate = LocalDate.of(Integer.parseInt(limits.split(";")[1].split("-")[0]),
                        Integer.parseInt(limits.split(";")[1].split("-")[1]),
                        Integer.parseInt(limits.split(";")[1].split("-")[2])); //end date
                long end = endDate.toEpochDay();

                long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
                map.put(value[0], "'" + LocalDate.ofEpochDay(randomEpochDay).toString() + "'");
            } else{
                map.put(value[0], value[1]);
            }
        }
        return map;
    }

    public static String getRandomValueFromFile(String filepath) throws FileNotFoundException {
        File file = new File(filepath);
        Scanner sc = new Scanner(file);
        ArrayList<String> values = new ArrayList<>();
        while (sc.hasNextLine()) {
            values.add(sc.nextLine());
        }
        int randomIndex = (int) Math.floor(Math.random() * values.size());
        return values.get(randomIndex);
    }

    public static String mapToInsertion(String tableName, HashMap<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("INSERT INTO ").append(tableName).append(" (");
        String[] keys = map.keySet().toArray(new String[0]);
        int i;
        for (i = 0; i < map.size() - 1; i++) {
            stringBuilder.append(keys[i]).append(",");
        }
        stringBuilder.append(keys[i]).append(") VALUES (");
        for (i = 0; i < map.size() - 1; i++) {
            stringBuilder.append(map.get(keys[i])).append(",");
        }
        stringBuilder.append(map.get(keys[i])).append(");");
        return stringBuilder.toString();
    }

}
