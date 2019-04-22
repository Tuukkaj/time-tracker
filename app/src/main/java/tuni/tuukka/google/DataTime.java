package tuni.tuukka.google;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190323
 * @since       1.8
 *
 * Used to pass information to SheetApi.appendSheet(). Has data of work starting time,
 * work ending time, category of work and comment.
 */
public class DataTime extends Data {

    /**
     * Timestamp value of when work was started
     */
    public float time;

    /**
     * Category of work
     */
    public String category;


    public String date;

    /**
     * Sets parameters to class variables.
     *
     * @param time Time worked.
     * @param category Category of work.
     * @param info Target sheetId and range.
     */
    public DataTime(float time, String category, SheetRequestsInfo info) {
        super(info);
        this.time = time;
        this.category = category;
        date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    }

    /**
     * Sets parameters to class variables.
     *
     * @param time Time worked.
     * @param category Category of work.
     * @param date Date when time was recorded.
     * @param info Target sheetId and range.
     */
    public DataTime(float time, String category, String date, SheetRequestsInfo info) {
        super(info);
        this.time = time;
        this.category = category;
        this.date = date;
    }
}
