package tuni.tuukka.google;

import java.util.List;

public class SheetRequestsInfo {
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
