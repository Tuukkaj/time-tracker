package tuni.tuukka.google;

import java.util.ArrayList;
import java.util.List;

public class SheetRequestsInfo {
    public static final String TIME_RANGE = "Sheet1!A:D";
    public static final String CATEGORY_RANGE = "Sheet2!A:A";
    String sheetID;
    String range;
    List<String> ranges;

    public static List<String> getRanges() {
        List<String> list = new ArrayList<>();
        list.add(TIME_RANGE);
        list.add(CATEGORY_RANGE);
        return list;
    }

    public SheetRequestsInfo(String sheetID, String range) {
        this.sheetID = sheetID;
        this.range = range;
    }

    public SheetRequestsInfo(String sheetID, List<String> ranges) {
        this.sheetID = sheetID;
        this.ranges = ranges;
    }

}
