package tuni.tuukka.google;

import java.util.ArrayList;
import java.util.List;

public class SheetRequestsInfo {
    public static final String WORK_TAB = "work";
    public static final String CATEGORIES_TAB = "categories";
    public static final String WORK_RANGE = WORK_TAB + "!A:D";
    public static final String CATEGORIES_RANGE = CATEGORIES_TAB+"!A:A";

    String sheetID;
    String range;
    List<String> ranges;

    public static List<String> getRanges() {
        List<String> list = new ArrayList<>();
        list.add(WORK_RANGE);
        list.add(CATEGORIES_RANGE);
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
