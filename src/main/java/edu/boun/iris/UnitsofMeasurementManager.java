package edu.boun.iris;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class UnitsofMeasurementManager {

    private URL inputFile;
    private ArrayList<UnitofMeasurement> unitsOfMeasurement;

    public UnitsofMeasurementManager() {
        unitsOfMeasurement = new ArrayList<UnitofMeasurement>();
        setInputFile(UnitsofMeasurementManager.class.getResource("/resources/annex1.xls"));
        try {
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setInputFile(UnitsofMeasurementManager.class.getResource("/resources/annex2.xls"));
        try {
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UnitofMeasurement> getUnitsofMeasurement() {
        return unitsOfMeasurement;
    }

    public void setInputFile(URL inputFile) {
        this.inputFile = inputFile;
    }

    public void read() throws IOException {
        File inputWorkbook = null;
        try {
            inputWorkbook = new File(inputFile.toURI());
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        Workbook w;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);
            String cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8, cell9, cell10;
            for (int j = 0; j < sheet.getRows(); j++) {

                if (inputFile.toString().endsWith("annex1.xls")) {
                    cell0 = sheet.getCell(0, j).getContents().toString();
                    cell1 = sheet.getCell(1, j).getContents().toString();
                    cell2 = sheet.getCell(2, j).getContents().toString();
                    cell3 = sheet.getCell(3, j).getContents().toString();
                    cell4 = sheet.getCell(4, j).getContents().toString();
                    cell5 = sheet.getCell(5, j).getContents().toString();
                    cell6 = sheet.getCell(6, j).getContents().toString();
                    cell7 = sheet.getCell(7, j).getContents().toString();
                    cell8 = sheet.getCell(8, j).getContents().toString();
                    cell9 = sheet.getCell(9, j).getContents().toString();
                    cell10 = sheet.getCell(10, j).getContents().toString();

                    UnitofMeasurement um = new UnitofMeasurement(cell0, cell1, cell2, cell3, cell4, cell5, cell6,
                            cell7, cell8, cell9, cell10);
                    unitsOfMeasurement.add(um);
                }
                if (inputFile.toString().endsWith("annex2.xls")) {
                    cell0 = sheet.getCell(0, j).getContents().toString();
                    cell1 = sheet.getCell(1, j).getContents().toString();
                    cell2 = sheet.getCell(2, j).getContents().toString();
                    cell3 = sheet.getCell(3, j).getContents().toString();
                    cell4 = sheet.getCell(4, j).getContents().toString();
                    cell5 = sheet.getCell(5, j).getContents().toString();
                    cell6 = sheet.getCell(6, j).getContents().toString();

                    UnitofMeasurement um = new UnitofMeasurement("", "", "", "", cell4, cell0, cell1, cell2, cell6,
                            cell5, cell3);
                    unitsOfMeasurement.add(um);
                }

            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }
}
