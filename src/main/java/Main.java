import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        List<Employee> listFromCSV = parseCSV(columnMapping, fileNameCSV);
        String jsonFromCSV = listToJson(listFromCSV);
        writeString(jsonFromCSV, "data1.json");

        String fileNameXML = "data.xml";
        List<Employee> listFromXML = parseXML(fileNameXML);
        String jsonFromXML = listToJson(listFromXML);
        writeString(jsonFromXML, "data2.json");

        String fileNameJSON = "data1.json";
        String json = readString(fileNameJSON);
        List<Employee> listFromJSON = jsonToList(json);
        for (Employee e : listFromJSON) {
            System.out.println(e);
        }

    }

    static List<Employee> parseCSV(String[] columnMapping, String filename) {
        List<Employee> staff = null;
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return staff;
    }

    static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    static void writeString(String str, String jsonFileName) {
        try (FileWriter fileWriter = new FileWriter(jsonFileName)) {
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<Employee> parseXML(String fileName) throws Exception {
        List<Employee> staffList = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        NodeList nodeEmpList = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeEmpList.getLength(); i++) {
            Node nodeEmp = nodeEmpList.item(i);
            if (nodeEmp.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nodeEmp;
                staffList.add(new Employee(
                        Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("lastName").item(0).getTextContent(),
                        element.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                ));
            }
        }
        return staffList;
    }

    static String readString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

    static List<Employee> jsonToList(String json) {
        JSONParser parser = new JSONParser();
        List<Employee> list = new ArrayList<>();
        try {
            Object obj = parser.parse(json);
            JSONArray jsonArray = (JSONArray) obj;
            Gson gson = new GsonBuilder().create();
            for (Object o : jsonArray) {
                Employee employee = gson.fromJson(o.toString(), Employee.class);
                list.add(employee);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
