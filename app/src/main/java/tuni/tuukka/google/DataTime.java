package tuni.tuukka.google;

public class DataTime extends Data {
    public long start;
    public long end;
    public String category;
    public String comment;

    public DataTime(long start, long end, String category, String comment, SheetRequestsInfo info) {
        super(info);
        this.start = start;
        this.end = end;
        this.category = category;
        this.comment = comment;
    }
}
