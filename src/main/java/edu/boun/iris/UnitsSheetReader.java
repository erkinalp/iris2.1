package edu.boun.iris;

import java.io.IOException;
import java.util.ArrayList;

public interface UnitsSheetReader {
    ArrayList<UnitofMeasurement> readUnits(String filePath, boolean isAnnex1) throws IOException;
}
