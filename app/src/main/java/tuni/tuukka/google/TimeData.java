package tuni.tuukka.google;

public class TimeData {
    public long start;
    public long end;
    public String category;
    public String comment;
    public SheetRequestsInfo info;

    public TimeData(){}
    public TimeData(long start, long end, String category, String comment, SheetRequestsInfo info) {
        this.start = start;
        this.end = end;
        this.category = category;
        this.comment = comment;
        this.info = info;
    }
}
