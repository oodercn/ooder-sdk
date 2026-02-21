/*
 * Created on 2004-2-6
 *
 * The spk Software License, Version 1.0
 * 
 * Copyright (c) 2004 The spk Software Foundation.  All rights
 * reserved.
 */

/**
 * $RCSfile: XMLUtility.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Title: 常用XML工具类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: spk</p>
 * @author Huchm 2004-2-6 20:07:04
 * @version 1.0
 */
public class XMLUtility {
	static DocumentBuilderFactory factory;
	static DocumentBuilder builder;
	//初始化DocumentBuilder
	static {
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用于把xml流转化为html输出流。
	 * @param xmlInput java.io.Reader          xml输入流
	 * @param htmlOutput java.io.Writer        html输出流
	 * @param xslInput java.io.Reader          xsl文件输入流
	 */
	public static void renderXmlStream(
		Reader xmlInput,
		Writer htmlOutput,
		Reader xslInput) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer =
				factory.newTransformer(new StreamSource(xslInput));
			xslInput.close();
			transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.transform(
				new StreamSource(xmlInput),
				new StreamResult(htmlOutput));
			xmlInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用于把xml流转化为html输出流。
	 * @param xmlInput java.io.Reader          xml输入流
	 * @param htmlOutput java.io.Writer        html输出流
	 * @param xsl java.lang.String             xsl文件
	 */
	public static void renderXmlStream(
		Reader xmlInput,
		Writer htmlOutput,
		String xsl) {
		try {
			FileReader xslInput = new FileReader(xsl);
			renderXmlStream(xmlInput, htmlOutput, xslInput);
			//xslInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 传入XML流和XSL文档的URL,返回用于显示的HTML字符串
	 * 一般把返回值放到request对象中，
	 * 在JSP中get出来并输出。
	 * @param xmlInput java.io.Reader  xml输入流，可以从Clob中得到
	 * @param xsl java.lang.String     xsl文件url
	 * @return the html stream to the page
	 */
	public static String renderXmlStream(Reader xmlInput, String xsl) {
		StringWriter sw = new StringWriter();
		renderXmlStream(xmlInput, sw, xsl);
		sw.flush();
		return sw.toString();
	}

	/**
	 * 从文件中读取xml文件内容，生成DOMDocument对象
	 * 参数：strFile -- xml文件，内部为xml格式
	 * 返回值： 生成的DomDocument对象。读取失败返回null;
	 */
	public static Document readXMLDoc(String strFile) {
		try {
			FileInputStream in = new FileInputStream(strFile);
			return readXMLDoc(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 从文件中读取xml文件内容，生成DOMDocument对象
	 * 参数：file -- xml文件，内部为xml格式
	 * 返回值： 生成的DomDocument对象。读取失败返回null;
	 */
	public static Document readXMLDoc(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			return readXMLDoc(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从InputStream中读取xml文件内容，生成DOMDocument对象
	 * 参数：in -- 一个InputStream对象，内部有xml文件的内容
	 * 返回值： 生成的DomDocument对象。读取失败返回null;
	 */
	public static Document readXMLDoc(InputStream in) {
		Document doc = null;
		try {
			//解析XML文件
			doc = builder.parse(in);
			doc.normalize();
			in.close();
			return doc;
		} catch (Exception e) {
			System.err.println("读取流程控制文件错误 in ProcessManager::readXMLDoc();");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取得一个xml节点上的值
	 * 参数：node -- 节点
	 * 返回值： 此节点上的值，如果节点不存在值，返回""
	 */
	public static String getNodeValue(Node node) {
		try {
			String str = node.getFirstChild().getNodeValue();
			return str.trim();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 取得一个xml节点下某个子节点的值
	 * 参数：node -- 父节点
	 *       strChild -- 子节点名称
	 * 返回：一个字符串
	 *         如果同名子节点存在多余一个，返回遇到的第一个子节点的值
	 */
	public static String getChildValue(Element node, String strChild) {
		try {
			NodeList list = node.getElementsByTagName(strChild);
			String str = getNodeValue(list.item(0));
			return str.trim();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 取得一个父节点下指定名称的子节点
	 * @param node 父节点
	 * @param childName 子节点的名称
	 * @return 如果存在满足条件的子节点，则返回对应的子节点，否则返回null
	 */
	public static Node getFirstChild(Node node, String childName) {
		if (node == null || childName == null || childName.trim().equals("")) {
			return null;
		}
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeName().equals(childName)) {
				return childNode;
			}
		}
		return null;
	}

	/**
	 * 取得一个父节点下指定类型的第一个子节点
	 * @param node 父节点
	 * @param type 子节点类型
	 * @return 返回满足条件的子节点
	 */
	public static Node getFirstChild(Node node, short type) {
		if (node == null) {
			return null;
		}
		NodeList nodeList = node.getChildNodes();
		for (int i=0;i<nodeList.getLength();i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == type) {
				return childNode;
			}
		}
		return null;
	}

	/**
	 * 取得一个父节点下指定类型的所有子节点
	 * @param node 父节点
	 * @param type 子节点类型
	 * @return 返回满足条件的子节点列表
	 */
	public static List getChildNodes(Node node, short type) {
		List retList = new ArrayList();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == type) {
				retList.add(childNode);
			}
		}
		return retList;
	}
}
