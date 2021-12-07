package com.common.framework.lib;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import static org.hamcrest.Matchers.*;

public class APIStructure {
	
	private int messageCode;
	private String errorMsg = "";
	boolean failFlag = false;
	
	public APIStructure setMessageCode(int code) {
		this.messageCode = code;
		return this;
	}
	
	private String getMessage() {
		if(this.messageCode == 107) {
			return "Partial data available locally  - Validation Failed - Licence Code not provided";
		}
		else if(this.messageCode == 111) {
			return "Completed , All data available locally1";
		}
		else if(this.messageCode == 333) {
			return "No Data available locally - Data requested from source";
		}
		else if(this.messageCode == 999) {
			return "Request Failed";
		}
		else if(this.messageCode == 222) {
			return "Partial data available locally - For Pending ones , Data requested from source";
		}
		else if(this.messageCode == 401) {
			return "Request Failed - Error Connecting to Source";
		}
		else if(this.messageCode == 402) {
			return "Request Failed - Error processing Request/Response from source";
		}
		else if(this.messageCode == 555) {
			return "Complete Pending Data Available from Source";
		}
		
		return "Unknown Message Code";
	}
	
	public boolean validate(Response res) {
		
		ValidatableResponse validatableResponse = res.then();
		try {
			validatableResponse.body("Message", equalTo(getMessage()));
		}catch(Error e) {
			failFlag = true;
			String assertionerror = e.getMessage();
			errorMsg += assertionerror.substring(e.getMessage().indexOf("JSON")) + "\n";
		}
		/*try {
			validatableResponse.body("$", hasKey("Data1"));
		}catch(Error e) {
			failFlag = true;
			String assertionerror = e.getMessage();
			errorMsg += assertionerror.substring(e.getMessage().indexOf("Expected")) + "\n";
		}
		try {
			validatableResponse.body("$", hasKey("Info"));
		}catch(Error e) {
			failFlag = true;
			String assertionerror = e.getMessage();
			errorMsg += assertionerror.substring(e.getMessage().indexOf("Expected")) + "\n";
		}
		if(this.messageCode == 107) {
			try {
				validatableResponse.body("$", hasKey("PendingDataFor"));
			}catch(Error e) {
				failFlag = true;
				String assertionerror = e.getMessage();
				errorMsg += assertionerror.substring(e.getMessage().indexOf("Expected")) + "\n";
			}
		}*/
		
		if(failFlag == true) {
			Messages.errorMsg = errorMsg;
			return false;
		}
		return true;
	}

}
