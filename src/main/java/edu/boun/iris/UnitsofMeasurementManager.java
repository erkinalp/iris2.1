package edu.boun.iris;

import java.io.IOException;
import java.util.ArrayList;

public class UnitsofMeasurementManager {

    private ArrayList<UnitofMeasurement> unitsOfMeasurement;

    public UnitsofMeasurementManager() {
        unitsOfMeasurement = new ArrayList<UnitofMeasurement>();
        
        try {
            readUnitsFile("/resources/annex1", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            readUnitsFile("/resources/annex2", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<UnitofMeasurement> getUnitsofMeasurement() {
        return unitsOfMeasurement;
    }

    private void readUnitsFile(String baseFilePath, boolean isAnnex1) throws IOException {
        UnitsSheetReader reader = null;
        String filePath = null;
        
        if (UnitsofMeasurementManager.class.getResource(baseFilePath + ".ods") != null) {
            reader = new OdsUnitsReader();
            filePath = baseFilePath + ".ods";
        } else if (UnitsofMeasurementManager.class.getResource(baseFilePath + ".xls") != null) {
            reader = new XlsUnitsReader();
            filePath = baseFilePath + ".xls";
        } else {
            throw new IOException("Neither .ods nor .xls file found for: " + baseFilePath);
        }
        
        ArrayList<UnitofMeasurement> units = reader.readUnits(filePath, isAnnex1);
        unitsOfMeasurement.addAll(units);
    }
}
