package com.common.listers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.common.framework.lib.CommonFunctions;
import com.common.framework.lib.Global;
import com.common.framework.lib.Messages;

public class MyListner implements ITestListener  {
	
	CommonFunctions cf = new CommonFunctions();
	
	public void onTestStart(ITestResult result) {
		
		Object[] params = result.getParameters();
		if(params.length != 0) {
			Object paramss = params[0];
			ObjectMapper m = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String,Object> mappedObject = m.convertValue(paramss,Map.class);
			Global.testCaseId = mappedObject.get("TestCaseID").toString();
			if(mappedObject.get("TestFileName") != null) {
				Global.fileName = mappedObject.get("TestFileName").toString();
			}
			Global.test = Global.extent.createTest("TestCase No: "+Global.testCaseId+" - "+Global.fileName, "<pre>"+""+result.getName()+""+ "</pre>");
			
		}else {
			String[] testId = result.getName().split("#");
			Global.testCaseId = testId[0].trim();
			Global.test = Global.extent.createTest("TestCase No: "+Global.testCaseId+" - "+Global.fileName, "<pre>"+""+testId[1]+""+ "</pre>");
		}
		
	}

	public void onTestSuccess(ITestResult result) {
		Global.extent.flush();
	}

	public void onTestFailure(ITestResult result) {
		Global.extent.flush();
	}

	public void onTestSkipped(ITestResult result) {
		Global.extent.flush();
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {


	}

	public void onStart(ITestContext context) {
		
		cf.extentReportLogoCreator(Global.confgXmlFile, Global.logo);
		Global.extent = cf.loadExtentSettings(Global.htmlReporter, Global.extent, Global.test, Global.propertyFile);
		boolean access = cf.fetchAccessToken();
		if(!access) {
			Assert.fail();
		}
		
		Global.allheaders = new LinkedHashMap<String,String>();
		Global.allheaders.put("Authorization", "bearer "+Messages.accessCode);
		Global.allheaders.put("Content-Type", "application/json");
	}

	public void onFinish(ITestContext context) {

	}


}
