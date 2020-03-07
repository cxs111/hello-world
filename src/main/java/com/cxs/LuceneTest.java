package com.cxs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Chen Xiasheng
 * @email 1113407795@qq.com
 * @date 2019/12/7 17:52
 */
@Slf4j
public class LuceneTest {
    @Test
    //创建索引步骤
    public void createIndex() throws Exception {
        //1 创建一个Directory对象,指定索引库保存位置
        Directory directory = FSDirectory.open(new File("E:\\Study\\IDEA\\Workspace\\lucene-test\\src\\main\\resources").toPath());
        //2 基于Directory对象创建一个IndexWriter对象
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig());
        //3 读取文件，对应每个文件创建一个文档对象
        File dir = new File("E:\\Study\\IDEA\\Workspace\\lucene-test\\src\\main\\resources\\textSource");
        File[] flist = dir.listFiles();
        for (File file : flist) {
            String fileName = file.getName();
            String filePath = file.getPath();
            String fileContent = FileUtils.readFileToString(file, "utf-8");
            long fileSize = FileUtils.sizeOf(file);
            ////创建域
            //        //第一个参数：域的名称
            //        //第二个参数：域的内容
            //        //第三个参数：是否存储
            Field fileNameField = new TextField("name", fileName, Field.Store.YES);
            Field filePathField = new TextField("path", filePath, Field.Store.YES);
            Field fileContentField = new TextField("content", fileContent, Field.Store.YES);
            Field fileSizeField = new TextField("size", String.valueOf(fileSize), Field.Store.YES);
            Document document = new Document();
            //4 向文档对象中添加域
            document.add(fileNameField);
            document.add(filePathField);
            document.add(fileContentField);
            document.add(fileSizeField);
            //5 文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //6 关闭IndexWriter对象
        indexWriter.close();
    }
    //查询索引
    @Test
    public void queryIndex() throws IOException {
        //1 创建一个Director对象，指定索引库的位置
        Directory directory = FSDirectory.open(new File("E:\\Study\\IDEA\\Workspace\\lucene-test\\src\\main\\resources").toPath());
        //2 创建一个IndexReader对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //3 创建一个IndexSearch对象，构造方法中参数indexReader
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //4 创建Query对象
        Query query = new TermQuery(new Term("content", "java"));
        //5 执行查询 得到TopDocs对象
        TopDocs topDocs = indexSearcher.search(query, 10);
        //6 去查询结果总记录数
        log.debug("查询java结果总记录数==={}",topDocs.totalHits.value);
        //7 取文档列表
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        //8 打印文档中的内容
        for(ScoreDoc s : scoreDocs){
            Document doc = indexSearcher.doc(s.doc);
            log.debug("doc name==={}",doc.get("name"));
            log.debug("doc path==={}",doc.get("path"));
            log.debug("doc size==={}",doc.get("size"));
            log.debug("doc content==={}",doc.get("content"));
            log.debug("==========================");
        }
        //9 关闭IndexReader对象
        indexReader.close();
    }
}
