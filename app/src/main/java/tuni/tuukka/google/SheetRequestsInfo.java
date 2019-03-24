package tuni.tuukka.google;

import java.util.ArrayList;
import java.util.List;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190324
 * @since       1.8
 *
 * Used to communicate target sheets id and range. Holds information about tabs and ranges used.
 */
public class SheetRequestsInfo {

    /**
     * Tab name of work times and comments.
     */
    public static final String WORK_TAB = "work";

    /**
     * Tab name of categories of work.
     */
    public static final String CATEGORIES_TAB = "categories";

    /**
     * Range of work information is held in work tab.
     */
    public static final String WORK_RANGE = WORK_TAB + "!A:D";

    /**
     * Range of category information is held in categories tab.
     */
    public static final String CATEGORIES_RANGE = CATEGORIES_TAB+"!A:A";

    /**
     * Target sheet id.
     */
    String sheetID;

    /**
     * Range to act upon target sheet id.
     */
    String range;

    /**
     * List of ranges. Used if targeting both ranges in sheet.
     */
    List<String> ranges;

    /**
     * Returns list of all ranges used by the app.
     * @return List of ranges where data is held.
     */
    public static List<String> getRanges() {
        List<String> list = new ArrayList<>();
        list.add(WORK_RANGE);
        list.add(CATEGORIES_RANGE);
        return list;
    }

    /**
     * Sets parameters to class variables.
     * @param sheetID Id of target sheet.
     * @param range Range to act on target sheet.
     */
    public SheetRequestsInfo(String sheetID, String range) {
        this.sheetID = sheetID;
        this.range = range;
    }

    /**
     * Sets parameters to class variables.
     * @param sheetID Id of target sheet.
     * @param ranges Ranges to act on target sheet.
     */
    public SheetRequestsInfo(String sheetID, List<String> ranges) {
        this.sheetID = sheetID;
        this.ranges = ranges;
    }

}
