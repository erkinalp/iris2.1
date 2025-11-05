package edu.boun.iris;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class XlsUnitsReader implements UnitsSheetReader {

    @Override
    public ArrayList<UnitofMeasurement> readUnits(String filePath, boolean isAnnex1) throws IOException {
        ArrayList<UnitofMeasurement> units = new ArrayList<UnitofMeasurement>();
        
        try {
            URL url = XlsUnitsReader.class.getResource(filePath);
            if (url == null) {
                throw new IOException("File not found: " + filePath);
            }
            
            File inputWorkbook = new File(url.toURI());
            Workbook w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);
            
            for (int j = 0; j < sheet.getRows(); j++) {
                String cell0 = sheet.getCell(0, j).getContents().toString();
                String cell1 = sheet.getCell(1, j).getContents().toString();
                String cell2 = sheet.getCell(2, j).getContents().toString();
                String cell3 = sheet.getCell(3, j).getContents().toString();
                String cell4 = sheet.getCell(4, j).getContents().toString();
                String cell5 = sheet.getCell(5, j).getContents().toString();
                String cell6 = sheet.getCell(6, j).getContents().toString();
                
                UnitofMeasurement um;
                if (isAnnex1) {
                    String cell7 = sheet.getCell(7, j).getContents().toString();
                    String cell8 = sheet.getCell(8, j).getContents().toString();
                    String cell9 = sheet.getCell(9, j).getContents().toString();
                    String cell10 = sheet.getCell(10, j).getContents().toString();
                    
                    um = new UnitofMeasurement(cell0, cell1, cell2, cell3, cell4, cell5, cell6,
                            cell7, cell8, cell9, cell10);
                } else {
                    um = new UnitofMeasurement("", "", "", "", cell4, cell0, cell1, cell2, cell6,
                            cell5, cell3);
                }
                units.add(um);
            }
            w.close();
        } catch (URISyntaxException | BiffException e) {
            throw new IOException("Error reading XLS file: " + filePath, e);
        }
        
        return units;
    }
}
