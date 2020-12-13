import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class XMLQuery {
    Document document;
    Element root;
    public XMLQuery(String file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(new File(file));
        document.getDocumentElement().normalize();
        root = document.getDocumentElement();
    }

    HashMap<String,ArrayList<String>> getQueries(){
        HashMap<String,ArrayList<String>> queries = new HashMap<String, ArrayList<String>>();
        queries.put("query", new ArrayList<String>());
        queries.put("question", new ArrayList<String>());
        queries.put("narrative", new ArrayList<String>());
        NodeList nl = document.getElementsByTagName("topic");
        for(int i = 0; i<nl.getLength(); i++){
            Node node = nl.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) node;
                queries.get("query").add(element.getElementsByTagName("query").item(0).getTextContent());
                queries.get("question").add(element.getElementsByTagName("question").item(0).getTextContent());
                queries.get("narrative").add(element.getElementsByTagName("narrative").item(0).getTextContent());
            }
        }
        return queries;
    }

    void writeFile(String field) throws IOException {
        HashMap<String,ArrayList<String>> queries = getQueries();
        ArrayList<String> fieldName = queries.get(field);
        FileWriter qw = new FileWriter("queries.txt");
        String newLine = System.getProperty("line.separator");
        for(String f : fieldName) {
            qw.write(f + newLine);
        }
        qw.close();
    }
}
