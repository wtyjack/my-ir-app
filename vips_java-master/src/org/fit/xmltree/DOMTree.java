package org.fit.xmltree;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.*;

public class DOMTree {
	private Document doc;
	private int DOC;

	public DOMTree(String filename,int DOC) {
		this.buildDOM(filename);
		this.DOC = DOC;
	}

	private void getDOCLevelNodes(Node node, int currlevel,
			ArrayList<Node> results) {
		// do something with the current node instead of System.out
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				if (currlevel < DOC-1) {
					// calls this method for all the children which is Element
					getDOCLevelNodes(currentNode, currlevel + 1, results);
				} else {
					Element e = (Element) currentNode;
					String id = e.getAttribute("ID");
					//System.out.println(id);
					results.add(currentNode);
				}
			}
		}
	}

	public ArrayList<String> getDocuments() {
		ArrayList<String> results = new ArrayList<String>();
		ArrayList<Node> docLevelNodes = new ArrayList<Node>();
		this.getDOCLevelNodes(this.doc.getDocumentElement(), 0, docLevelNodes);
		for(Node node: docLevelNodes){
			Element e = (Element) node;
			String id = e.getAttribute("ID");
			System.out.println(id+"\n"); 
			results.add(""); 
			cocatenateChildrenContent(node,results);
			System.out.println(results.get(results.size()-1));
		}
		return results;
	}

	public ArrayList<String> iterate() {
		ArrayList<String> results = new ArrayList<String>();
		this.getChildren(this.doc.getDocumentElement(),results);
		return results;
	}
	
	public void cocatenateChildrenContent(Node node,ArrayList<String> results){
		NodeList nodeList = node.getChildNodes();
		
		if(nodeList.getLength()==0){
			
			Element e = (Element) node;
			String content = e.getAttribute("Content");
			results.set(results.size()-1, results.get(results.size()-1)+content+"\n");
		}else{
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node currentNode = nodeList.item(i);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					cocatenateChildrenContent(currentNode,results);
				}
			}
		}
	}
	
	public void getChildren(Node node,ArrayList<String> results){
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element e = (Element) currentNode;
				if(e.hasAttribute("Content")){
					results.add(e.getAttribute("Content"));
				}
				getChildren(currentNode,results);
			}
		}
	}

	private void buildDOM(String fileName) {
		try {
			File XmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			this.doc = dBuilder.parse(XmlFile);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		DOMTree d = new DOMTree("/Users/zhuangenze/Desktop/vips_java-master/VIPSResult.xml",2);
		ArrayList<String> r = d.iterate();
		boolean f = true;
	}

}
