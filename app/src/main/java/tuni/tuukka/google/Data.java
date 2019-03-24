package tuni.tuukka.google;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190323
 * @since       1.8
 *
 * Data class that holds SheetRequestInfo of Sheet Id and range. Used as base class for passing
 * data to to SheetApi.AppendSheet().
 */
public abstract class Data {
    /**
     * Holds target sheetId and range
     */
    public SheetRequestsInfo info;

    /**
     * Sets parameter to class variable.
     * @param info Target sheetId and range.
     */
    public Data(SheetRequestsInfo info) {
        this.info = info;
    }
}
