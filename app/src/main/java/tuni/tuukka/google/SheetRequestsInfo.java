package tuni.tuukka.google;

import java.util.List;

public class SheetRequestsInfo {
    public static final String TIME_RANGE = "Sheet1!A:A";
    public static final String CATEGORY_RANGE = "Sheet1!G:G";
    String sheetID;
    String range;
    List<String> ranges;

    public SheetRequestsInfo(String sheetID, String range) {
        this.sheetID = sheetID;
        this.range = range;
    }

    public SheetRequestsInfo(String sheetID, List<String> ranges) {
        this.sheetID = sheetID;
        this.ranges = ranges;
    }

}
