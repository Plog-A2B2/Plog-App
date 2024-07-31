package com.a2b2.plog;

import android.content.Context;
import android.content.res.AssetManager;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    public static List<String> readExcelFile(Context context, String fileName) {
        List<String> addressList = new ArrayList<>();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                String address = row.getCell(0).getStringCellValue();
                addressList.add(address);
            }

            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addressList;
    }
}
