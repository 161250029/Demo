package com.example.demo.Tool;

import com.example.demo.Entity.BugInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DomTool {
 
	public DomTool() {
	}

	public static Document getDocument(String FilePath) {
		Document document = null;
		try {
			File file = new File(FilePath);
			InputStream inputStream = new FileInputStream(file);
			//创建一个document解析工厂
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//实例化一个DocumentBuilder对象
			DocumentBuilder builder = factory.newDocumentBuilder();
			//使用DocumentBuilder对象获取一个Document的对象
			document = builder.parse(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	public static List<BugInfo> getBugs(String FilePath) throws Exception {
		List<BugInfo> list = new ArrayList<BugInfo>();
		//使用DocumentBuilder对象获取一个Document的对象
		Document document = getDocument(FilePath);
		Element element = document.getDocumentElement();  //获得元素节点
		NodeList nodeList = element.getElementsByTagName("BugInstance"); //获取元素节点
		for(int i=0; i<nodeList.getLength(); i++){
			Element bugElement = (Element) nodeList.item(i);
			if (bugElement.getElementsByTagName("Method").getLength() == 0)
				continue;
			BugInfo bug = new BugInfo();  //实例化 Bug对象
			bug.setLevel(Integer.parseInt(bugElement.getAttribute("priority")));
			NodeList childNodes = bugElement.getChildNodes();
			boolean isFirstMethod = true;
			for(int j = 0; j < childNodes.getLength(); j++){
				if(childNodes.item(j).getNodeType() == Node.ELEMENT_NODE){
					if (childNodes.item(j).getNodeName().equals("Class")) {
						if (!HasSpecialSymbol(((Element)childNodes.item(j)).getAttribute("classname").split("\\.")[((Element)childNodes.item(j)).getAttribute("classname").split("\\.").length - 2])) {
							bug.setJarLocation(((Element)childNodes.item(j)).getAttribute("classname").split("\\.")[((Element)childNodes.item(j)).getAttribute("classname").split("\\.").length - 2]);
						}
						else
							bug.setJarLocation(((Element)childNodes.item(j)).getAttribute("classname").split("\\.")[((Element)childNodes.item(j)).getAttribute("classname").split("\\.").length - 3] + "_" + ((Element)childNodes.item(j)).getAttribute("classname").split("\\.")[((Element)childNodes.item(j)).getAttribute("classname").split("\\.").length - 2]);
						bug.setSourceFile(((Element)childNodes.item(j)).getAttribute("classname").split("\\.")[((Element)childNodes.item(j)).getAttribute("classname").split("\\.").length - 1]);
					} else if("SourceLine".equals(childNodes.item(j).getNodeName())){
						bug.setStart(Integer.parseInt(((Element)childNodes.item(j)).getAttribute("start")));
						bug.setEnd(Integer.parseInt(((Element)childNodes.item(j)).getAttribute("end")));
					}  else if ("Method".equals(childNodes.item(j).getNodeName()) && isFirstMethod){
						isFirstMethod = false;
						bug.setMethodName(((Element)childNodes.item(j)).getAttribute("name"));
					}
				}
			}
			list.add(bug);
		}
		return list;
	}

	public static boolean HasSpecialSymbol(String s) {
		if (s.equals("s01") || s.equals("s02") || s.equals("s03") || s.equals("s04") || s.equals("s05"))
			return  true;
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			List<BugInfo> list = getBugs("D:\\FirstSix.xml");
			for(BugInfo bug:list){
				System.out.println(bug.getSourceFile() + " " + bug.getLevel() + " start:" + bug.getJarLocation());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
}
