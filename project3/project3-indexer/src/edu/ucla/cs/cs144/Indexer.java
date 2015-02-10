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
            Directory indexDir = FSDirectory.open(new File("/var/lib/lucene/index1"));
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


    try {
        getIndexWriter(true);

    	//retrieve data
        //todo
        PreparedStatement prepareRetrieveItems = conn.prepareStatement(
            "select Items.id as item_id, Items.name as item_name, description, Categories.categories as" +
            " categories from Items inner join (select item_id, group_concat(name SEPARATOR ' ') "+
                "as categories from Items_Categories inner join Categories on category_id = "+
                "Categories.id group by item_id) as Categories on Items.id = Categories.item_id"
        );

        int iid;
        String id, name, category, description;
        ArrayList<Item> itemList = new ArrayList<Item>();
        ResultSet rs = prepareRetrieveItems.executeQuery();
        while (rs.next()) {
            iid = rs.getInt(1);
            id = Integer.toString(iid);
            name = rs.getString(2);
            description = rs.getString(3);
            category = rs.getString(4);
            Item item = new Item(id,name,category,description);
            itemList.add(item);
        }

        //build index
        for (Item item: itemList) {
            indexItem(item);
        }


        closeIndexWriter();

    } catch (SQLException ex) {
        System.out.println(ex);
    } catch (IOException ex) {
        System.out.println(ex);
    }

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
