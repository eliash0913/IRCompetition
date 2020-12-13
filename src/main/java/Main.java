
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static final String INDEX_DIR = "luceneIdx/";

    public static void main(String[] args) throws Exception
    {
        XMLQuery xQuery = new XMLQuery("D:/Data/train/train/queries.xml");
        xQuery.writeFile("query");

        DataGenerator dg = new DataGenerator();
        dg.generateData();


        IndexWriter writer = createWriter();
        List<Document> documents = new ArrayList<>();
        String rootPath = "D:/Data/train/train/";
        String csvFile = rootPath + "documents/metadata.csv";
        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
        CSVParser parser = new CSVParser(new FileReader(csvFile), format);
        ArrayList<String> utt = new ArrayList<String>();
        int doc_id = 0;
        for (CSVRecord csvRecord : parser) {
            String doc_uid = csvRecord.get("uid");
            String doc_title = csvRecord.get("title");
            String doc_abstract = csvRecord.get("abstract");
            String doc_authors = csvRecord.get("authors");
            documents.add(createDocument(doc_uid,doc_title,doc_abstract,doc_authors));
            doc_id++;
        }
        writer.deleteAll();
        writer.addDocuments(documents);
        writer.commit();
        writer.close();


        HashMap<String,ArrayList<String>> xq = xQuery.getQueries();
        String queryTerms = "";
        for(String query : xq.get("query")){
            queryTerms += query + " ";
        }


//        Similarity bl25 = new BM25Similarity(1.25f, 0.75f);
        IndexSearcher searcher = createSearcher();
        FileWriter qw = new FileWriter("predictions.txt");
        String newLine = System.getProperty("line.separator");
        int qn = 0;
        for(String query : xq.get("query")) {
            TopDocs foundDocs = searchByTitle(query, searcher);
            ScoreDoc[] sd = foundDocs.scoreDocs;
            for(ScoreDoc s : sd){
                String result = qn + "\t" +documents.get(s.doc).get("uid") + "\t" + s.score;
                System.out.println(result);
                qw.write(result + newLine);
            }
            qn++;
        }
        qw.close();
    }

    private static Document createDocument(String doc_uid, String doc_title, String doc_abstract, String doc_authors)
    {
        Document document = new Document();
        document.add(new TextField("uid", doc_uid , Field.Store.YES));
        document.add(new TextField("title", doc_title , Field.Store.YES));
        document.add(new TextField("abstract", doc_abstract , Field.Store.YES));
        document.add(new TextField("authors", doc_authors , Field.Store.YES));
        return document;
    }

    private static IndexWriter createWriter() throws IOException
    {
        FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        IndexWriter writer = new IndexWriter(dir, config);
        return writer;
    }

    private static TopDocs searchByUid(String doc_uid, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("uid", new StandardAnalyzer());
        Query uidQuery = qp.parse(doc_uid);
        TopDocs hits = searcher.search(uidQuery, 1000);
        return hits;
    }

    private static TopDocs searchByAbstract(String doc_abstract, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("abstract", new StandardAnalyzer());
        Query abstractQuery = qp.parse(doc_abstract);
        TopDocs hits = searcher.search(abstractQuery, 1000);
        return hits;
    }

    private static TopDocs searchByAuthor(String doc_author, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("authors", new StandardAnalyzer());
        Query authorQuery = qp.parse(doc_author);
        TopDocs hits = searcher.search(authorQuery, 1000);
        return hits;
    }

    private static TopDocs searchByTitle(String doc_title, IndexSearcher searcher) throws Exception
    {
        QueryParser qp = new QueryParser("title", new StandardAnalyzer());
        Query titleQuery = qp.parse(doc_title);
        TopDocs hits = searcher.search(titleQuery, 1000);
        return hits;
    }

    private static IndexSearcher createSearcher() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }




    public void test(){
        uid_to_text utt = new uid_to_text();
        try {
            utt.readMetadata();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
