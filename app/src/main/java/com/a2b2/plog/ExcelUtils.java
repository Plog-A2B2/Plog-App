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

public class ExcelUtils {

    public static Map<String, Location> readExcelFile(Context context, String fileName) {
        Map<String, Location> locationMap = new HashMap<>();
        List<String> addressList = new ArrayList<>();

        try {
            InputStream inputStream = context.getAssets().open(fileName);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            Log.d("ExcelUtils", "Sheet Name: " + sheet.getSheetName());


            for (Row row : sheet) {
                if (row.getRowNum() < 1) {
                    continue;
                }

                String city = row.getCell(0).getStringCellValue();
                Cell latitudeCell = row.getCell(2);
                Cell longitudeCell = row.getCell(3);

                double longitude = getNumericCellValue(longitudeCell);
                double latitude = getNumericCellValue(latitudeCell);

                locationMap.put(city, new Location(longitude, latitude));
                addressList.add(city); // 주소 리스트에 도시 추가
                Log.d("ExcelParser", "Parsed city: " + city + ", latitude: " + latitude + ", longitude: " + longitude); // 로그 추가
            }

            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ExcelParser", "Error parsing Excel file", e); // 에러 로그 추가
        }

        return locationMap; // 리스트 대신 맵을 반환
    }

    private static double getNumericCellValue(Cell cell) {
        if (cell == null) {
            return 0.0;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    Log.e("ExcelParser", "Invalid numeric format", e);
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }
}
