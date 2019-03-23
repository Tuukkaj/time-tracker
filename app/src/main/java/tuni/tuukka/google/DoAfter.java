package tuni.tuukka.google;

public interface DoAfter<T> extends OnFail{
    void onSuccess(T value);
}