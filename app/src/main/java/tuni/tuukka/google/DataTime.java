package tuni.tuukka.google;

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
    public long start;

    /**
     * Timestamp value of when work was ended.
     */
    public long end;

    /**
     * Category of work
     */
    public String category;

    /**
     * Comment of what user did while working
     */
    public String comment;

    /**
     * Sets parameters to class variables.
     *
     * @param start Work start time.
     * @param end Work end time.
     * @param category Category of work.
     * @param comment Comment of what user did while working.
     * @param info Target sheetId and range.
     */
    public DataTime(long start, long end, String category, String comment, SheetRequestsInfo info) {
        super(info);
        this.start = start;
        this.end = end;
        this.category = category;
        this.comment = comment;
    }
}
