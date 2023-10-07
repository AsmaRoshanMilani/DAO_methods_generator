package sample;

import java.io.*;
import java.net.URL;
//import java.lang.reflect.ReflectAccess;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {

    @FXML
    private TextField erDiagramInput;

    @FXML
    private TextField projectName;

    @FXML
    private TextField packageName;

    @FXML
    private TextField outputDirectory;

    @FXML
    public void extractDataAndCreateDaoClasses(ActionEvent event) {
        String address = erDiagramInput.getText().replace('/','\\');
        File xmlFile = new File(address);
        String xmlContent = null;

        try {

            BufferedReader br = new BufferedReader(new FileReader(xmlFile));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            br.close();
            xmlContent = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String name = projectName.getText();
        String packagePath = packageName.getText().replace(".", "/");
        String outputPath = outputDirectory.getText().endsWith("/") ?
                outputDirectory.getText() + name :
                outputDirectory.getText() + "\\" + name;
        System.out.println(outputPath);

        int startIndex = 0;
        int endIndex = 0;
        while ((startIndex = xmlContent.indexOf("<mxCell", endIndex)) != -1) {
            endIndex = xmlContent.indexOf("</mxCell>", startIndex) + "</mxCell>".length();
            String entityXml = xmlContent.substring(startIndex, endIndex);
            String entityShape = extractShape(entityXml, "shape");
            String entityName = extractValue(entityXml, "value");
            if (entityShape.equals("table")){
                String daoCode = generateDaoCode(entityName, name, packagePath);
                File daoFile = new File(outputPath+ "/src/" + packagePath + "/" + entityName + "Dao.java");
                try {

                    FileWriter writer = new FileWriter(daoFile);
                    writer.write(daoCode);
                    writer.close();
                    System.out.println("Generated DAO code in " + daoFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            startIndex=endIndex+1;
        }

    }
@FXML
       private String generateDaoCode(String entityName, String name, String packageName) {
    String genericFileContent = null;
    String finalContent = null;
    try {

        BufferedReader br = new BufferedReader(new FileReader("src/sample/Generic.txt"));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }
        br.close();
        genericFileContent = sb.toString();

    } catch (IOException e) {
        e.printStackTrace();
    }

    finalContent=genericFileContent.replace("<entity name>",entityName.toLowerCase());
    finalContent=finalContent.replace("<Capital entity name>",capitalize(entityName));

    return finalContent;
    }

    public static final String capitalize(String str) {
        if (str == null || str.length() == 0) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String extractShape(String xml, String attributeName) {
        int startIndex = xml.indexOf(attributeName + "=");
        if (startIndex == -1) {
            return "";
        }
        startIndex += attributeName.length() + 1;
        int endIndex = xml.indexOf(";", startIndex);
        return xml.substring(startIndex, endIndex);
    }
    private static String extractValue(String xml, String attributeName) {
        int startIndex = xml.indexOf(attributeName +"=\"");
        if (startIndex == -1) {
            return "";
        }
        startIndex += attributeName.length() + 2;
        int endIndex = xml.indexOf("\"", startIndex);
        return xml.substring(startIndex, endIndex);
    }

}

