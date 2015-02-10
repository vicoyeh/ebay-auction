package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.Item;
import edu.ucla.cs.cs144.DbManager;

public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
 
    private IndexWriter indexWriter = null;

    public IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/"));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
    }    

    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    public void indexItem(Item item) throws IOException {
        System.out.println("Indexing item: " + item);
        IndexWriter writer = getIndexWriter(false);
        Document doc = new Document();
        doc.add(new StringField("iid", item.iid, Field.Store.YES));
        doc.add(new StringField("name", item.name, Field.Store.YES));
        String fullText = item.iid + " " + item.name + " " + item.category + " " + item.description; 
        doc.add(new TextField("content", fullText, Field.Store.NO));
        writer.addDocument(doc);
    }   


    public void rebuildIndexes() {

        Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
	try {
	    conn = DbManager.getConnection(true);
	} catch (SQLException ex) {
	    System.out.println(ex);
	}

    getIndexWriter(true);

	//retrieve data
    //todo
    PreparedStatement prepareRetrieveItems = con.PrepareStatement(
        "SELECT Items.id, Categories.id, Items.name, Categories.name, Items.description
         FROM Items, Categories, Items_Categories WHERE Items_Categories.item_id = Items.id 
        AND Categories.id = Items_Categories.category_id;"
    );

    String iid, cid name, category, description;
    ArrayList<Item> itemList = new ArrayList<Item>();
    ResultSet rs = prepareRetrieveItems.executeQuery();
    while (rs.next()) {
        iid = rs.getString("iid");
        cid = rs.getString("cid");
        name = rs.getString("name");
        category = rs.getString("category");
        description = rs.getString("description");
        Item item = new Item(iid,cid,name,category,description);
        itemList.add(item);
    }

    //build index
    for (Item item: itemList) {
        indexItem(item);
    }


    closeIndexWriter();

        // close the database connection
	try {
	    conn.close();
	} catch (SQLException ex) {
	    System.out.println(ex);
	}
    }    

    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
