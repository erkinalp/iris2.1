package edu.boun.iris;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

public class OdsUnitsReader implements UnitsSheetReader {

    @Override
    public ArrayList<UnitofMeasurement> readUnits(String filePath, boolean isAnnex1) throws IOException {
        ArrayList<UnitofMeasurement> units = new ArrayList<UnitofMeasurement>();
        
        URL url = OdsUnitsReader.class.getResource(filePath);
        if (url == null) {
            throw new IOException("File not found: " + filePath);
        }
        
        try (InputStream is = url.openStream()) {
            SpreadSheet spreadsheet = new SpreadSheet(is);
            Sheet sheet = spreadsheet.getSheet(0);
            
            int numRows = sheet.getMaxRows();
            for (int j = 0; j < numRows; j++) {
                String cell0 = getCellAsString(sheet, 0, j);
                String cell1 = getCellAsString(sheet, 1, j);
                String cell2 = getCellAsString(sheet, 2, j);
                String cell3 = getCellAsString(sheet, 3, j);
                String cell4 = getCellAsString(sheet, 4, j);
                String cell5 = getCellAsString(sheet, 5, j);
                String cell6 = getCellAsString(sheet, 6, j);
                
                UnitofMeasurement um;
                if (isAnnex1) {
                    String cell7 = getCellAsString(sheet, 7, j);
                    String cell8 = getCellAsString(sheet, 8, j);
                    String cell9 = getCellAsString(sheet, 9, j);
                    String cell10 = getCellAsString(sheet, 10, j);
                    
                    um = new UnitofMeasurement(cell0, cell1, cell2, cell3, cell4, cell5, cell6,
                            cell7, cell8, cell9, cell10);
                } else {
                    um = new UnitofMeasurement("", "", "", "", cell4, cell0, cell1, cell2, cell6,
                            cell5, cell3);
                }
                units.add(um);
            }
        }
        
        return units;
    }
    
    private String getCellAsString(Sheet sheet, int col, int row) {
        Object value = sheet.getRange(row, col).getValue();
        if (value == null) {
            return "";
        }
        return value.toString().trim();
    }
}
