package com.testscripts;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.common.framework.lib.CommonFunctions;
import com.common.framework.lib.Global;
import com.common.framework.lib.Messages;
import com.common.framework.lib.MethodModel;
import com.qmetry.qaf.automation.step.QAFTestStep;

import io.restassured.response.Response;

public class TC_1_Postmethod {

	ExtentTest node;
	CommonFunctions cf = new CommonFunctions();

	@QAFTestStep(description="User made {0} request to {1} with {2} and compare {3} and file {4}")
	public void userMadeRequestToWithAndCompareAndFile(String methodType,String url,String  requestfile,Integer expectedResponseCount,String file){

		String requestPath = Global.executeDirectory+"\\RequestFiles\\"+requestfile;
		String request = cf.fetchRequestDataFromJsonObject(requestPath);

		String prettyJsonString = cf.returnPrettyString(request);

		node = Global.test.createNode("Request Body & Response Time details");
		cf.showResponseBody("Test Case Response Body",prettyJsonString,node);

		Response res = new MethodModel(url,request).setMethod(methodType).getResponse();
		
		long actualTimeWithUnits = res.getTimeIn(TimeUnit.MILLISECONDS);
		node.log(Status.INFO, MarkupHelper.createLabel("API Response Time",ExtentColor.GREEN)).info("<pre>" + "API took "+actualTimeWithUnits+" milliseconds to return response." +"</pre>");
		
		node = Global.test.createNode("Verify Status Code");
		if(res.getStatusCode() != 200) {
			Messages.errorMsg = "Status Code is not 200. Status code returned is "+res+". ";
			cf.extentReportFailLogger("Status Code validation.", node);
			Assert.fail();
		}
		cf.extentReportPassLogger("Status Code validation.", "Status code from API returned is 200.",node);
		
		node = Global.test.createNode("Compare Response Body Count");
		
		int bodyCountActual = res.then().extract().jsonPath().getList("Data").size();
		String jsonDataBody = cf.fetchJsonStringByKey(res.getBody().asString(), "Data");
		//String jsonDataBody = res.getBody().jsonPath().getString("Data");
		
		boolean match = expectedResponseCount == bodyCountActual ? true : false;
			
		if(!match) {
			String expectedfile = Global.executeDirectory+"\\ExpectedFiles\\"+file;
			String expectedResponse = cf.fetchRequestDataFromJsonObject(expectedfile);
			String datajson = cf.fetchJsonStringByKey(expectedResponse, "Data");
			String jsonDiff = cf.findJsonDiff(datajson, jsonDataBody);
			System.out.println(jsonDiff);
			cf.doVerifyMissedData(jsonDiff);
			Messages.errorMsg = "Total "+(expectedResponseCount-bodyCountActual)+" variance in 'Data' count.\n\nTotal number of expired security out of total variance is "+Global.listOfExpiredSecurities.size()+".\n\nInstrumentIDs of expired Securities "+Global.listOfExpiredSecurities+".\n\nInstrumentIDs of unExpired Securities "+Global.listOfUnExpiredSecurities+"";
			cf.extentReportFailLogger("Details of expired securities", node);
			String prettyJsonDiff = cf.returnPrettyString(jsonDiff);
			Messages.errorMsg = "Expected and Actual 'Data' count didn't matched. Response returned "+bodyCountActual+" counts. \n\n"+ prettyJsonDiff;
			cf.extentReportFailLogger("Verify count of 'Data' in API", node);
			
		}else {
			cf.extentReportPassLogger("Verify count of 'Data' in API", "Counts Matched. Response returned "+bodyCountActual+" counts.",node);
		}
		
		
		String requestID = res.then().extract().jsonPath().getString("RequestID");
		
		String expectedfile1 = Global.executeDirectory+"\\ExpectedFiles\\"+file;
		
		String expectedResponse1 = cf.fetchRequestDataFromJsonObject(expectedfile1);
		node = Global.test.createNode("Response body validation. Request Id: "+requestID+"");
		
		boolean comparison = cf.compareJsons(expectedResponse1, res.getBody().asString());
		if(!comparison) {
			Messages.errorMsg = "Actual Response is not matched with expected response file. \n\n" + Messages.errorMsg;
			cf.extentReportFailLogger("API response comparison.", node);
			Assert.fail();
		}
		cf.extentReportPassLogger("API response comparison.", "Expected and Actual response matched.",node);

	}



}
