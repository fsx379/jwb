package org.suxin.jwb.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.suxin.jwb.PaperContent;

public class WordUtil {

	   // 生成Word2007版本  
    private XWPFDocument doc2007;  
    
    // 创建Word输出流  
    private FileOutputStream fos;  
    
	
	public void init(String objectFile) throws Exception {
        
        // 初始化2007版本  
        doc2007 = new XWPFDocument();  
  
        // 初始化输出流  
        fos = new FileOutputStream(new File(objectFile));  
	}
    
	public void addContent( PaperContent content ) throws IOException{
		  try {
			
		
        // 创建段落  
        XWPFParagraph p1 = doc2007.createParagraph();  
        // 设置样式,此时样式为一个正方形包围文字  
        p1.setAlignment(ParagraphAlignment.CENTER);  
  
        boolean match = content.isMatch();
        String desc = match ? "" : " [作者不匹配]";
        
        // 创建1段文字,通过段落创建  
        XWPFRun r1 = p1.createRun();  
        // 设置是否粗体  
        r1.setBold(true);  
        r1.setText(content.getTitle() + desc);  
        r1.setFontFamily("黑体");
        r1.setFontSize(14);
  
        XWPFParagraph p2 = doc2007.createParagraph();  
        p2.setAlignment(ParagraphAlignment.CENTER);
  
        XWPFRun r2 = p2.createRun();  
        r2.setText(content.getAuthor());  
        r2.setFontFamily("宋体");
        r2.setFontSize(10);
        r2.addBreak();
  
        XWPFParagraph p3 = null;
        XWPFRun r3 = null;
        for(String textTmp : content.getContent()){
            p3 = doc2007.createParagraph();  
            p3.setAlignment(ParagraphAlignment.LEFT);
            p3.setIndentationFirstLine(2);
            
        	r3 = p3.createRun();  
            r3.setText(textTmp);  
            r3.setFontFamily("宋体");
            r3.setFontSize(10);
           
        }
        r3.addBreak();
        
        XWPFParagraph p4 = doc2007.createParagraph();  
        p4.setAlignment(ParagraphAlignment.LEFT);
        p4.setIndentationFirstLine(2);
        
        XWPFRun r4= p4.createRun();  
        r4.setText(content.getDateStr());  
        r4.setFontFamily("黑体");
        r4.setFontSize(10);
        r4.setBold(true); 
        
        XWPFParagraph p5 = null;
        XWPFRun r5 = null;
        for(int i =0 ; i < 3 ; i++){
        	p5 =  doc2007.createParagraph();  
            p5.setAlignment(ParagraphAlignment.LEFT);
            
            r5= p5.createRun();  
            r5.setText("");  
            r5.setFontSize(20);
        }
		  } catch (Exception e) {
				// TODO: handle exception
			  System.out.println("[addContent] error! " + e.getMessage());
			}
	}
	
	public void addPaperList ( PaperContent content ) throws IOException{
		  
        // 创建段落  
        XWPFParagraph p3 = null;
        XWPFRun r3 = null;
        p3 = doc2007.createParagraph();  
        p3.setAlignment(ParagraphAlignment.LEFT);
        p3.setIndentationFirstLine(2);
            
        r3 = p3.createRun();  
        r3.setText(content.getContent() + "|" + content.getAuthor()+"|" + content.getDateStr() +"|"+ content.getUrl());  
        r3.setFontFamily("宋体");
        r3.setFontSize(9);
           
    
	}

	public void Write2WordEnd() throws IOException{
		doc2007.write(fos); 
	
        if (fos != null) {  
            fos.close();  
        }  
	}
	
	
	public static File writename;
	public static BufferedWriter out;
	
	public void write2TextIni(String outFile){
		try {
			/* 写入Txt文件 */  
	        File writename = new File(outFile); // 相对路径，如果没有则要建立一个新的output。txt文件  
	        if(!writename.exists()){
	        	 writename.createNewFile(); // 创建新文件 
	        }
	       out = new BufferedWriter(new FileWriter(writename));  
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void write2Txt(String outFile , List<PaperContent> list){
		try {
	        
	        for(PaperContent p : list){
	        	 out.write(p.getTitle() +"|"+p.getAuthor() +"|"+ p.getDateStr() +"|"+p.getUrl() +"\r\n"); // \r\n即为换行  
	        }
	        out.write("\r\n");
	        out.write("\r\n");
	        out.flush(); // 把缓存区内容压入文件  
	       
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void write2TxtEnd(){
		try {
		 out.close(); // 最后记得关闭文件 
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
