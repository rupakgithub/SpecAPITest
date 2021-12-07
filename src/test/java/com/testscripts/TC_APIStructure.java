package com.testscripts;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.common.framework.lib.APIStructure;
import com.common.framework.lib.CommonFunctions;
import com.common.framework.lib.Global;
import com.common.framework.lib.MethodModel;
import com.qmetry.qaf.automation.step.QAFTestStep;

import io.restassured.response.Response;

public class TC_APIStructure {
	
	ExtentTest node;
	CommonFunctions cf = new CommonFunctions();

	@QAFTestStep(description="User made {0} request to {1} with {2}")
	public void userMadeRequestToWith(String methodType,String url,String requestfile){
		
		String requestPath = Global.executeDirectory+"\\RequestFiles\\"+requestfile;
		String request = cf.fetchRequestDataFromJsonObject(requestPath);

		String prettyJsonString = cf.returnPrettyString(request);

		node = Global.test.createNode("Request Body & Response Time details");
		cf.showResponseBody("Test Case Response Body",prettyJsonString,node);

		Response res = new MethodModel(url,request).setMethod(methodType).getResponse();
		
		long actualTimeWithUnits = res.getTimeIn(TimeUnit.MILLISECONDS);
		node.log(Status.INFO, MarkupHelper.createLabel("API Response Time",ExtentColor.GREEN)).info("<pre>" + "API took "+actualTimeWithUnits+" milliseconds to return response." +"</pre>");
		
		node = Global.test.createNode("Validate Response API Structure");
		int messageCode = Integer.parseInt(res.then().extract().jsonPath().getString("MessageCode"));
		
		APIStructure str = new APIStructure();
		boolean test = str.setMessageCode(messageCode).validate(res);
		if(!test) {
			cf.extentReportFailLogger("Validate API Structure.MessageCode: "+messageCode+"", node);
			Assert.fail();
		}
		cf.extentReportPassLogger("Validate API Structure.MessageCode: "+messageCode+"", "API Structure matched expectation",node);
	}
}
