package tuni.tuukka.google;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190323
 * @since       1.8
 *
 * Used to pass information to SheetApi.appendSheet(). Has only one category data, which will be
 * appended to a sheet.
 */
public class DataCategory extends Data {
    /**
     * Category data to append to a sheet
     */
    public String category;

    /**
     * Sets parameters to class variables.
     *
     * @param category Category to append.
     * @param info Target sheetId and range.
     */
    public DataCategory(String category, SheetRequestsInfo info) {
        super(info);
        this.category = category;
    }
}
