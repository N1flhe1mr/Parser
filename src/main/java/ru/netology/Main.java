package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        list = parseXML("data.xml");
        json = listToJson(list);
        writeString(json, "data2.json");
        json = readString("new_data.json");
        list = jsonToList(json);
        System.out.println(list);
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader cvsReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> cvs = new CsvToBeanBuilder<Employee>(cvsReader).withMappingStrategy(strategy).build();
            return cvs.parse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employeeList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;
                    String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = employee.getElementsByTagName("country").item(0).getTextContent();
                    long id = Long.parseLong(employee.getElementsByTagName("id").item(0).getTextContent());
                    int age = Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent());
                    employeeList.add(new Employee(id, firstName, lastName, country, age));
                }
            }
            return employeeList;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String string, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(string);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> jsonToList(String json) {
        try {
            List<Employee> employeeList = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(json);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            for (Object employee : jsonArray) {
                employeeList.add(
                        gson.fromJson(employee.toString(), Employee.class)
                );
            }
            return employeeList;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static String readString(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String string;
            while ((string = br.readLine()) != null) {
                return string;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}