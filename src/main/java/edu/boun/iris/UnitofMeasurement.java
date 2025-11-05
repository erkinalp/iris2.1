package edu.boun.iris;

public class UnitofMeasurement {
    String groupNumber = "";
    String sector = "";
    String groupId = "";
    String quantity = "";
    String level_Category = "";
    String status = "";
    String commonCode = "";
    String name = "";
    String conversionFactor = "";
    String symbol = "";
    String description = "";

    public UnitofMeasurement(String c0, String c1, String c2, String c3, String c4, String c5, String c6, String c7,
            String c8, String c9, String c10) {
        this.groupNumber = c0;
        this.sector = c1;
        this.groupId = c2;
        this.quantity = c3;
        this.level_Category = c4;
        this.status = c5;
        this.commonCode = c6;
        this.name = c7;
        this.conversionFactor = c8;
        this.symbol = c9;
        this.description = c10;
    }
}
