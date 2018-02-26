package org.suxin.jwb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.suxin.jwb.util.DateUtil;
import org.suxin.jwb.util.HttpUtil;
import org.suxin.jwb.util.WordUtil;

/**
 * Hello world!
 *
 */
public class AppMain {
	
	//待查询的文件
	public static String makeDocFilePre = "/jwbSource";
	public static String makeListFilePre = "/jwbList";
	public static String makeDocFileSuf = ".docx";
	public static String makeListFileSuf = ".txt";
	
	public static final long sleepTime = 1500;
	
	//开始、结束时间
	public static Date startDate;
	public static Date endDate;
	
	//查询内容
	public static Map<String , PaperContent> map;
	public static  List<String> contentUrlList = new ArrayList<String>();
	
	public static boolean queryTileAuthorOnly = false;
	
	public static boolean useHttp = false;
	
    public static void main( String[] args ) {
    	try {
    		if(args == null || args.length <1){
    			System.out.println("no find file");
    		}
    		String filePath = args[0];
    		System.out.println("filePath: " + filePath);
    		
    		System.out.println("begin...");
    		//1.初始化 - 初始化 搜索内容
        	map = initSearchList(filePath);
        	System.out.println("initSearchList end...");
        	//2.默认查询url列表
        	List<String> urlList = getUrlList();
        	System.out.println("getUrlList end... ursList.size = " + urlList.size());
        	
        	//3.网络抓取
        	// 3.1 先抓标题列表
        	// 3.2 在查询文章 
        	
        	List<PaperContent> retPaperList = new ArrayList<PaperContent>();
        	
        	if(map != null && map.size() > 0 && urlList != null && urlList.size() > 0 ){
        		String retFile = makeDocFileName(filePath  ,false );
        		WordUtil wordPrint = new WordUtil();
        		wordPrint.init(retFile);
        		
        		for(String url : urlList){
        			getContentUrl(url);
        			Thread.sleep(sleepTime);
        			if(contentUrlList != null && contentUrlList.size() >0){
            			for(String contentUrl : contentUrlList){
            				PaperContent content = getPaperContent(contentUrl);
            				if(content != null){
            					retPaperList.add(content);
            				}
            				Thread.sleep(sleepTime);
            			}
            			
            			contentUrlList.clear();
            		}
        			
        	 		//4.输出到word
                	if(retPaperList != null && retPaperList.size() > 0){
                		for(PaperContent p: retPaperList){
                			wordPrint.addContent(p);
                		}
                		retPaperList.clear();
                	}
        		}
        		
        		wordPrint.Write2WordEnd();
            	System.out.println("netCatch end...");
            	System.out.println("write2Word end. retFile: " + retFile);
            	
        	}else if(urlList != null && urlList.size() > 0){
        		queryTileAuthorOnly = true;
        		String retFile = makeDocFileName(filePath  ,true );
        		WordUtil wordUtil = new WordUtil();
        		wordUtil.write2TextIni(retFile);
        		//only print 
        		for(String url : urlList){
        			 getContentUrl(url);
        			 Thread.sleep(sleepTime);
        			 
        			 if(contentUrlList != null && contentUrlList.size() >0){
             			for(String contentUrl : contentUrlList){
             				PaperContent content = getPaperContent(contentUrl);
             				if(content != null){
             					retPaperList.add(content);
             					System.out.println(content.getTitle() + "|" + content.getAuthor() +"|" +content.getDateStr() +"|"+ content.getUrl());
             				}
             				Thread.sleep(sleepTime);
             			}
             			System.out.println("");
             			contentUrlList.clear();
             		}
        			 
        			//4.输出到word
                 	if(retPaperList != null && retPaperList.size() > 0){
                 		
                 		wordUtil.write2Txt(retFile, retPaperList);
                 		retPaperList.clear();
                 	}
        		}
        		
        		wordUtil.write2TxtEnd();
        		
            	System.out.println("netCatch end...");
            	System.out.println("write2Word end. retFile: " + retFile);
        		
        	}
        	
        	
        	
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	System.out.println("end...");
    	
    }
    
    
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    private static Map<String , PaperContent> initSearchList(String fileName) {
    	Map<String ,PaperContent> map = new LinkedHashMap<String, PaperContent>();
    	
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	if(line == 1){
            		phaseQueryDate(tempString);
            	}else{
            		// 显示行
                    PaperContent paperContent = phaseQueryContent(tempString);
                    if(paperContent != null){
                    	map.put(paperContent.getTitle(), paperContent);
                    	
                    }else{
                    	System.out.println("phase error! line: " + line +" " + tempString) ;
                    }
            	}
                
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
        return map;
    }
    
    /**
     * 解析－格式：
     * 标题｜作者｜日期
     * @param lineStr
     * @return
     */
    private  static PaperContent phaseQueryContent(String lineStr){
    	try{
    		String[] strArray = lineStr.split("\\|");
    		if(strArray != null && strArray.length >= 2){
    			PaperContent c = new PaperContent();
    			c.setTitle(strArray[0]);
    			c.setAuthor(strArray[1]);
    			
    			return c;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }
    
    /**
     * 
     * @param lineStr
     */
    private static void phaseQueryDate(String lineStr){
    	String[] str = lineStr.split("~");
    	if(str != null && str.length >=2){
    		startDate = DateUtil.formatToDate(str[0], DateUtil.DEFAULT_DATE_FORMAT);
        	endDate = DateUtil.formatToDate(str[1], DateUtil.DEFAULT_DATE_FORMAT);
    	}
    }
    
    //http://epaper.jwb.com.cn/jwb/html/2018-02/13/node_5.htm
    private static String createDefaultUrl(Date date){
    	String yearMoth = DateUtil.formatDate(date, DateUtil.JWB_DATE_FORMAT);
    	return String.format(Constant.jwbURLFormat, yearMoth , "2");
    }
    
    private static String  createTitleUrl(String oriUrl, String newUrlStr){
    	int index = oriUrl.lastIndexOf("/");
    	return oriUrl.substring(0, index) +"/"+ newUrlStr;
    }
    
    private static String  createContentUrl(String oriUrl, String contUrl){
    	int index = oriUrl.lastIndexOf("/");
    	return oriUrl.substring(0, index) +"/"+ contUrl;
    }
    
    private static List<String> getUrlList(){
    	
    	List<String> dateList = new ArrayList<String>();
    	while(startDate.compareTo(endDate)<=0){
    		dateList.add(createDefaultUrl(startDate));
    		startDate = DateUtil.getIntervalDate(startDate, 1);
    	}
    	return dateList;
    }
    
    private static void getContentUrl(String url){
    	try {
    		Document doc = null;
    		try {
    			if(useHttp){
        			String html = HttpUtil.sendRequest(url, 4000, 8000, "UTF-8", false);
        			System.out.println(html);
        			doc = Jsoup.parse(html);
        		}else{
        			doc = Jsoup.connect(url).header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
        		    		.get();
        		}
			} catch (Exception e) {
				if(useHttp){
	    			String html = HttpUtil.sendRequest(url, 4000, 8000, "UTF-8", false);
	    			System.out.println(html);
	    			doc = Jsoup.parse(html);
	    		}else{
	    			doc = Jsoup.connect(url).header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
	    		    		.get();
	    		}
			}
    		
    		
    		
    		
    		String title = doc.title();
    		if(title.startsWith(Constant.titleFK)){
    			Element element = doc.getElementById("main-ed-articlenav-list");
    			Elements titles = element.getElementsByTag("a");
    			if(titles != null && titles.size() > 0){
    				
    				
    				System.out.println("url: " + url);
    				for(Element tmp : titles){
    					if(queryTileAuthorOnly){
    						String contentUrl = createContentUrl(url, tmp.attr("href"));
    						contentUrlList.add(contentUrl);
        				}else{
        					if(map.containsKey(tmp.text())){
        						String contentUrl = createContentUrl(url, tmp.attr("href"));
        						contentUrlList.add(contentUrl);
        					}
        				}
    				}
    				
    			}
    		}else{
    			Element element = doc.getElementById("bmdhTable");
    			Elements titles = element.select(".rigth_bmdh_href");
    			if(titles != null && titles.size() > 0){
    				for(Element tmp : titles){
    					if(tmp.text().contains(Constant.FK)){
    						url = createTitleUrl(url, tmp.attr("href"));
    						getContentUrl(url);
    						continue;
    					}
    				}
    			}
    		}
    		
    		System.out.println("");
    		Thread.sleep(sleepTime);
		} catch (Exception e) {
			System.out.println("[getContentUrl] error ! " + url + " " + e.getMessage());
			e.printStackTrace();
			
		}
    	
    }
    
    private static PaperContent getPaperContent(String contentUrl) {
    	
    	try {
    		Document doc = Jsoup.connect(contentUrl).header("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
    		.get();
    		
    		if(doc != null){
    			PaperContent tmpPaper = new PaperContent();
    			tmpPaper.setUrl(contentUrl);
    			
    			Elements title = doc.getElementsByTag("founder-title");
        		if(title != null && title.size() > 0){
        			tmpPaper.setTitle(title.get(0).text());
        			
        			tmpPaper.setAuthor(title.get(0).parent().parent().nextElementSibling().text());
        		}
        		
        		
        		Element content = doc.getElementById("ozoom");
        		if(content != null){
        			String text1 = content.text();
        			if(text1 != null){
        				String[] texts = text1.split(" ");
        				if(texts != null && texts.length >0){
        					tmpPaper.setContent(texts);
        				}
        			}
        			
        		}
        		
        		Element dateE = doc.getElementById("banner_top_date");
        		if(dateE != null ){
        			String dateStr = dateE.text();
        			tmpPaper.setDateStr(dateStr.substring(0 , dateStr.indexOf(" ")));
        		}
        		
        		if(map.containsKey(tmpPaper.getTitle())){
        			PaperContent contentTmp = map.get(tmpPaper.getTitle());
        			
        			if(contentTmp.createKey().equals(tmpPaper.createKey()) ){
        				tmpPaper.setMatch(true);
        			}else{
        				tmpPaper.setMatch(false);
        			}
        		}
        		
        		return tmpPaper;
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    private static String makeDocFileName(String filePath , boolean isList ){
		File file = new File(filePath);
    	StringBuffer sbuffer = new StringBuffer(file.getParentFile().getPath());
    	if(isList){
    		sbuffer.append(makeListFilePre);
    	}else{
    		sbuffer.append(makeDocFilePre);
    	}
    	
    	sbuffer.append(DateUtil.formatDate(new Date(), DateUtil.FMT_YYYYMMDDHHMMSS));
    	if(isList){
    		sbuffer.append(makeListFileSuf);
    	}else{
    		sbuffer.append(makeDocFileSuf);
    	}
    	
    	return sbuffer.toString();
    }
    
    
    
}
