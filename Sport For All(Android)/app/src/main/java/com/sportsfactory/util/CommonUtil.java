package com.sportsfactory.util;

import java.io.File;

public class CommonUtil {

    /**
     *
     * 파일 이름 + 확장자 추출
     * @param fileName
     * @return
     */
    public static String getFileNameWithoutExtension(String fileName) {
        File tmpFile = new File(fileName);
        tmpFile.getName();
        int whereDot = tmpFile.getName().lastIndexOf('.');
        if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2 ) {

            String extension = fileName.substring(whereDot+1);

            return tmpFile.getName().substring(0, whereDot)+"."+extension;
        }
        return "";
    }
}
