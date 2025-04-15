package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class DataComparator {

  static class Record {
    String countryRegion;
    String caseCreateHour;
    String caseCreateDt;

    Record(String countryRegion, String caseCreateHour, String caseCreateDt) {
      this.countryRegion = countryRegion != null ? countryRegion.trim() : "";
      this.caseCreateHour = caseCreateHour != null ? caseCreateHour.trim() : "";
      this.caseCreateDt = caseCreateDt != null ? caseCreateDt.trim() : "";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Record record = (Record) o;
      return countryRegion.equals(record.countryRegion) &&
          caseCreateHour.equals(record.caseCreateHour) &&
          caseCreateDt.equals(record.caseCreateDt);
    }

    @Override
    public int hashCode() {
      return Objects.hash(countryRegion, caseCreateHour, caseCreateDt);
    }

    @Override
    public String toString() {
      return "Record{" + countryRegion + ", " + caseCreateHour + ", " + caseCreateDt + "}";
    }
  }

  public static void main(String[] args) {
    try {
      // Print working directory for debugging
      System.out.println("Current working directory: " + System.getProperty("user.dir"));

      // Step 1: Read JSON file
      List<Record> jsonRecords = readJsonFile("D:\\Projects\\venkat\\JavaUtility\\src\\main\\resources\\datasets\\response.json");
      System.out.println("Total records in JSON: " + jsonRecords.size());
      // Debug: Print JSON records
//      System.out.println("JSON records: " + jsonRecords);

      // Step 2: Read Excel file
      List<Record> excelRecords = readExcelFile("D:\\Projects\\venkat\\JavaUtility\\src\\main\\resources\\datasets\\SampleResponse.xlsx");
      System.out.println("Total records in Excel: " + excelRecords.size());
      // Debug: Print Excel records
//      System.out.println("Excel records: " + excelRecords);

      // Step 3: Compare records
      Set<Record> jsonSet = new HashSet<>(jsonRecords);
      Set<Record> excelSet = new HashSet<>(excelRecords);

      // Find common records
      Set<Record> commonRecords = new HashSet<>(jsonSet);
      commonRecords.retainAll(excelSet);
      System.out.println("Total common (matched) records: " + commonRecords.size());
      // Debug: Print common records
//      System.out.println("Common records: " + commonRecords);

      // Find records in Excel but missing in JSON
      Set<Record> excelNotInJson = new HashSet<>(excelSet);
      excelNotInJson.removeAll(jsonSet);
      System.out.println("Total records in Excel but missing in JSON: " + excelNotInJson.size());
      // Debug: Print Excel records not in JSON
//      System.out.println("Excel records not in JSON: " + excelNotInJson);

    } catch (Exception e) {
      System.out.println("Error during comparison: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static List<Record> readJsonFile(String filePath) throws IOException {
    List<Record> records = new ArrayList<>();
    StringBuilder jsonContent = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        jsonContent.append(line);
      }
    }
    JSONObject jsonObject = new JSONObject(jsonContent.toString());
    JSONArray dataArray = jsonObject.getJSONArray("data");
    for (int i = 0; i < dataArray.length(); i++) {
      JSONObject record = dataArray.getJSONObject(i);
      String countryRegion = record.getString("hk_36100_Country_Region");
      String caseCreateHour = record.getString("hk_36100_Case_Create_Hour");
      String caseCreateDt = record.getString("hk_36100_Case_Create_Dt");
      records.add(new Record(countryRegion, caseCreateHour, caseCreateDt));
    }
    return records;
  }

  private static List<Record> readExcelFile(String filePath) throws IOException {
    List<Record> records = new ArrayList<>();
    try (FileInputStream fis = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(fis)) {
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rowIterator = sheet.iterator();
      if (rowIterator.hasNext()) {
        rowIterator.next(); // Skip header
      }
      while (rowIterator.hasNext()) {
        Row row = rowIterator.next();
        String countryRegion = getCellValueAsString(row.getCell(0));
        String caseCreateHour = getCellValueAsString(row.getCell(1));
        String caseCreateDt = getCellValueAsString(row.getCell(2));
        records.add(new Record(countryRegion, caseCreateHour, caseCreateDt));
      }
    }
    return records;
  }

  private static String getCellValueAsString(Cell cell) {
    if (cell == null) {
      return "";
    }
    // Use DataFormatter to get the cell value as a string, exactly as it appears in Excel
    DataFormatter formatter = new DataFormatter();
    return formatter.formatCellValue(cell);
  }
}