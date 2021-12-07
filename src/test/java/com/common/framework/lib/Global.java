package com.common.framework.lib;

import java.util.List;
import java.util.Map;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class Global {

	public static String executeDirectory = System.getProperty("user.dir");
	public static String confgXmlFile = executeDirectory+"\\ExtentReportConfigs\\extent-config.xml";
	public static String logo =  executeDirectory+"\\ExtentReportConfigs\\Tranhub_logo.PNG";
	public static String extentlog =  executeDirectory+"\\ExtentLogs\\";
	public static String propertyFile = executeDirectory+"\\ExtentReportConfigs\\config.properties";

	public static String excuteScenario;
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	public static ExtentTest test;
	public static String testCaseId = null;
	public static String fileName = null;
	public static Map<String,String> allheaders;
	public static List<String> listOfExpiredSecurities;
	public static List<String> listOfUnExpiredSecurities;
	
	
}
