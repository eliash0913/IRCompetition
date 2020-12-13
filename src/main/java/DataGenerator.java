import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DataGenerator {
    public void generateData() throws IOException, ParseException {
        String rootPath = "D:/Data/train/train/";
        String csvFile = rootPath + "documents/metadata.csv";
        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
        CSVParser parser = new CSVParser(new FileReader(csvFile), format);
        String newLine = System.getProperty("line.separator");
        int i = 0;
        FileWriter qw = new FileWriter("cord19.dat");
        qw.write("uid,title,abstract,authors ."+newLine);
        for (CSVRecord csvRecord : parser) {
            String doc_uid = csvRecord.get("uid").replace('.',' ');
            String doc_title = csvRecord.get("title").replace('.',' ');
            String doc_abstract = csvRecord.get("abstract").replace('.',' ');
            String doc_authors = csvRecord.get("authors").replace('.',' ');
            qw.write(doc_uid+","+doc_title+","+doc_abstract+","+doc_authors+" . "+newLine);

        }
        qw.close();
    }
}
