//package com.a2b2.plog;
//
//import android.content.Context;
//import android.util.Log;
//
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ExcelUtils {
//
//    public static List<String> readExcelFile(Context context, String fileName) {
//        List<String> addressList = new ArrayList<>();
//
//        try {
//            InputStream inputStream = context.getAssets().open(fileName);
//            Workbook workbook = new XSSFWorkbook(inputStream);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            for (Row row : sheet) {
//                if (row.getRowNum() < 3) {
//                    continue; // 첫 번째 행과 두 번째 행을 건너뜁니다.
//                }
//                String city = row.getCell(3).getStringCellValue();
//
//                Cell longitudeCell = row.getCell(4);
//                Cell latitudeCell = row.getCell(5);
//
//                double longitude = getNumericCellValue(longitudeCell);
//                double latitude = getNumericCellValue(latitudeCell);
//
//                locationMap.put(city, new Location(longitude, latitude));
//                Log.d("ExcelParser", "Parsed city: " + city + ", latitude: " + latitude + ", longitude: " + longitude); // 로그 추가
//            }
//
//            workbook.close();
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("ExcelParser", "Error parsing Excel file", e); // 에러 로그 추가
//            Log.e("ExcelParser", "bad", e); // 에러 로그 추가
//
//        }
//
//        return addressList;
//    }
//}
package com.a2b2.plog;

import android.content.Context;
import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecycleExcelUtils {

    public static Map<String, com.a2b2.plog.Location> readRecyclingExcelFile(Context context, String fileName) {
        Map<String, com.a2b2.plog.Location> locationMap = new HashMap<>();

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Log.d("RecycleExcelUtils", "Sheet Name: " + sheet.getSheetName());

            int rowIndex = 0;  // 고유 인덱스

            for (Row row : sheet) {
                if (row.getRowNum() < 1) {  // 첫 번째 행은 헤더라면 건너뜀
                    continue;
                }

                Cell latitudeCell = row.getCell(0);  // 위도 셀 (인덱스 0)
                Cell longitudeCell = row.getCell(1);  // 경도 셀 (인덱스 1)

                if (latitudeCell == null || longitudeCell == null) {
                    continue;  // 셀이 비어 있으면 건너뜀
                }

                double latitude = getNumericCellValue(latitudeCell);
                double longitude = getNumericCellValue(longitudeCell);

                // 고유 키로 사용하기 위해 행 번호를 키로 사용
                String uniqueKey = "Location_" + rowIndex;

                if (latitude != 0.0 && longitude != 0.0) {
                    locationMap.put(uniqueKey, new com.a2b2.plog.Location(latitude, longitude));
                    Log.d("RecycleExcelUtils", "Parsed location: " + uniqueKey + ", latitude: " + latitude + ", longitude: " + longitude);
                }

                rowIndex++;  // 인덱스 증가
            }

            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("RecycleExcelUtils", "Error parsing Excel file", e);
        }

        return locationMap;
    }

    private static double getNumericCellValue(Cell cell) {
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException nfe) {
                Log.e("RecycleExcelUtils", "Invalid numeric format", nfe);
                return 0.0;
            }
        }
    }
}
