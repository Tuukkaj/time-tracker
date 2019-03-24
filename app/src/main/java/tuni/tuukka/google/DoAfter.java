package tuni.tuukka.google;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190324
 * @since       1.8
 *
 * Interface for calling DriveApi and SheetApi. Used to pass commands for what to do in case of
 * success on Drive/Sheet operation or in case of failure.
 * @param <T> What onSuccess() accepts as a result.
 */
public interface DoAfter<T> extends OnFail{
    /**
     * Called when operation was successful.
     * @param value Result value from operation.
     */
    void onSuccess(T value);
}