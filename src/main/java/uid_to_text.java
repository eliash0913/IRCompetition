import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.RAMDirectory;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


public class uid_to_text {
    public void readMetadata() throws IOException, ParseException {
        HashMap<String, ArrayList<HashMap<String, Object>>> uid_to_text = new HashMap<String, ArrayList<HashMap<String, Object>>>();
        String rootPath = "D:/Data/train/train/";
        String csvFile = rootPath + "documents/metadatamini.csv";
        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
        CSVParser parser = new CSVParser(new FileReader(csvFile), format);

        ArrayList<String> utt = new ArrayList<String>();
        for (CSVRecord csvRecord : parser) {
            ArrayList<String> introduction = new ArrayList<String>();
            String doc_uid = csvRecord.get("uid");
            String doc_title = csvRecord.get("title");
            String doc_abstract = csvRecord.get("abstract");
            String[] doc_authors = csvRecord.get("authors").split(";");
            String[] pdfJsonFiles = csvRecord.get("pdf_json_files").split(";");
            boolean found = false;
            if (pdfJsonFiles.length != 0) {
                for (String pdfJsonfile : pdfJsonFiles) {
                    String jsonPath = rootPath + "documents/" + pdfJsonfile;
                    JSONParser jParser = new JSONParser();
                    Object obj = jParser.parse(new FileReader(jsonPath));
                    JSONObject jObj = (JSONObject) obj;
                    JSONArray bodyText = (JSONArray) jObj.get("body_text");
                    Iterator itr = bodyText.iterator();
                    while (itr.hasNext() && !found) {
                        JSONObject jo = (JSONObject) itr.next();
                        if (jo.get("section").toString().toLowerCase().contains("intro")) {
                            introduction.add(jo.get("text").toString());
                            found = true;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
            if (uid_to_text.containsKey(doc_uid)) {
                HashMap<String, Object> tmp = new HashMap<>();
                tmp.put("title", doc_title);
                tmp.put("abstract", doc_abstract);
                tmp.put("introduction", introduction);
                uid_to_text.get(doc_uid).add(tmp);
            } else {
                HashMap<String, Object> tmpHM = new HashMap<>();
                tmpHM.put("title", doc_title);
                tmpHM.put("abstract", doc_abstract);
                tmpHM.put("introduction", introduction);
                ArrayList<HashMap<String, Object>> tmpList = new ArrayList<HashMap<String, Object>>();
                uid_to_text.put(doc_uid, tmpList);
            }
        }
    }
}
