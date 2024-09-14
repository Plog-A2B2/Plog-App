package com.a2b2.plog;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class MultipartUtility {

    private static final String LINE_FEED = "\r\n";
    private static final String BOUNDARY = "----WebKitFormBoundary7MA4YWxkTrZu0gW"; // 일반적으로 랜덤한 문자열

    public static String uploadFile(String requestURL, String jsonData, File uploadFile) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(requestURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            outputStream = new DataOutputStream(connection.getOutputStream());

            // JSON 데이터 추가
            addFormField(outputStream, "trashRequest", jsonData);

            // 파일 데이터 추가
            if (uploadFile != null && uploadFile.exists()) {
                addFilePart(outputStream, "image", uploadFile);
            } else {
                addFilePart(outputStream, "image", null);
            }

            // End of multipart/form-data.
            outputStream.writeBytes("--" + BOUNDARY + "--" + LINE_FEED);

            outputStream.flush();
            outputStream.close();

            // 서버 응답 읽기
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                return convertStreamToString(inputStream); // 응답 변환
            } else {
                return "Failed to upload: " + status;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Exception: " + e.getMessage();
        }
    }

    private static void addFormField(DataOutputStream outputStream, String name, String value) throws Exception {
        outputStream.writeBytes("--" + BOUNDARY + LINE_FEED);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + LINE_FEED);
        outputStream.writeBytes(LINE_FEED);
        outputStream.writeBytes(value + LINE_FEED);
    }

    private static void addFilePart(DataOutputStream outputStream, String fieldName, File uploadFile) throws Exception {
        if (uploadFile != null && uploadFile.exists()) {
            String fileName = uploadFile.getName();
            outputStream.writeBytes("--" + BOUNDARY + LINE_FEED);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + LINE_FEED);
            outputStream.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(fileName) + LINE_FEED);
            outputStream.writeBytes(LINE_FEED);

            FileInputStream fileInputStream = new FileInputStream(uploadFile);
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            byte[] buffer = new byte[Math.min(bytesAvailable, maxBufferSize)];

            int bytesRead = fileInputStream.read(buffer, 0, buffer.length);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bytesRead);
                bytesRead = fileInputStream.read(buffer, 0, Math.min(bytesAvailable, maxBufferSize));
            }

            fileInputStream.close();
            outputStream.writeBytes(LINE_FEED);
        } else {
            outputStream.writeBytes("--" + BOUNDARY + LINE_FEED);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"\"" + LINE_FEED);
            outputStream.writeBytes("Content-Type: application/octet-stream" + LINE_FEED);
            outputStream.writeBytes(LINE_FEED);
            outputStream.writeBytes(LINE_FEED);
        }
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
