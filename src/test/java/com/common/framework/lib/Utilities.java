package com.common.framework.lib;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Utilities
{
	public  String sScreenShotFolderPath = "screenshots";
	public static boolean bscreenShot = true;
	private static Map<String, String> objMap = null;

	public Utilities() {}


	public Map<String, String> readTestData(String sFilePath, String sSheetName, String sTestCaseName) { 

		String sKey = null;
		String sValue = null;
		try {
			objMap = new HashMap<String, String>();
			File file =    new File(sFilePath);
			FileInputStream inputStream = new FileInputStream(file);
			Workbook objWorkbook = null;

			if(sFilePath.toLowerCase().endsWith("xlsx")){
				objWorkbook = new XSSFWorkbook(inputStream);
			}else if(sFilePath.toLowerCase().endsWith("xls")){
				objWorkbook = new HSSFWorkbook(inputStream);
			}
			Sheet objSheet = objWorkbook.getSheet(sSheetName);

			int numOfRows=objSheet.getLastRowNum();
			for(int rowNum=0;rowNum<numOfRows;rowNum++){

				Row row=objSheet.getRow(rowNum);
				int numOfCellPerRow=row.getLastCellNum();
				String sCurTestCaseName = row.getCell(0).toString();
				if (sCurTestCaseName.equalsIgnoreCase(sTestCaseName)) {
					for(int cellNum=0;cellNum<numOfCellPerRow;cellNum++){
						sKey = row.getCell(0).getStringCellValue();
						sKey = objSheet.getRow(0).getCell(cellNum).getStringCellValue();
						sValue =row.getCell(cellNum).getStringCellValue();
						if ((!sValue.equalsIgnoreCase("Null")) && (sValue.trim().length() != 0)) {
							objMap.put(sKey, sValue);
						}
					}
					break;
				}
			}

		} catch (Exception e) {
			Messages.errorMsg = "Exception occured.." + e.getMessage();
		}
		return objMap;
	}

	@SuppressWarnings("unused")
	public  Map<String, Map<String, String>> readMultipleTestData(String sFilePath, String sSheetName, String sTestCaseName) {
		String sPreviousTestCaseName = "";
		Map<String, Map<String, String>> objTestData = new HashMap<String, Map<String, String>>();
		try {

			int iRowNo = 1;
			File file =    new File(sFilePath);
			FileInputStream inputStream = new FileInputStream(file);
			Workbook objWorkbook = null;

			if(sFilePath.toLowerCase().endsWith("xlsx")){
				objWorkbook = new XSSFWorkbook(inputStream);
			}else if(sFilePath.toLowerCase().endsWith("xls")){
				objWorkbook = new HSSFWorkbook(inputStream);
			}
			Sheet objSheet = objWorkbook.getSheet(sSheetName);
			if(objSheet==null) {
				return objTestData;
			}

			int iRowCount=objSheet.getLastRowNum();

			for (int iRowCounter = 1; iRowCounter <= iRowCount; iRowCounter++) {
				Row row=objSheet.getRow(iRowCounter);
				if(row !=null) {
					int numOfCellPerRow=row.getLastCellNum();
					row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
					if(row.getCell(0).toString() !=null && !row.getCell(0).toString().isEmpty()) {
						Map<String, String> objRowData = new HashMap<String, String>();
						String sCurTestCaseName = row.getCell(0).toString();
						if (sCurTestCaseName.equalsIgnoreCase(sTestCaseName)) {
							sPreviousTestCaseName = sCurTestCaseName;
							for (int iColCounter = 0; iColCounter < numOfCellPerRow; iColCounter++) {
								String sKey = objSheet.getRow(0).getCell(iColCounter).getStringCellValue();
								sKey = sKey.trim();
								System.out.println(sKey);
								Cell cell = row.getCell(iColCounter,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
								//if (cell.getStringCellValue() != "") {
									String sValue =row.getCell(iColCounter).toString();
									sValue = sValue.trim();
									if ((!sValue.equalsIgnoreCase(null)) && (sValue.trim().length() != 0)) {
										objRowData.put(sKey, sValue);
										System.out.println(objRowData);
									}
								//}
							}
							objTestData.put("Row" + iRowNo, objRowData);
							objRowData = null;
							iRowNo++;
							System.out.println(iRowNo);
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return objTestData;
	}
	
	public String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		return sdf.format(cal.getTime());
	}
}
