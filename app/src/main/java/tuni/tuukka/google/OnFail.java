package tuni.tuukka.google;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190324
 * @since       1.8
 *
 * Interface for communicating what to do if Drive/Sheet operation fails.
 */
public interface OnFail {
    /**
     * Commands for recovery from failure to fetch/post data to Drive/Sheets.
     */
    void onFail();
}
