package tuni.tuukka.google;

public class AppendData {
    public long start;
    public long end;
    public String category;
    public String comment;
    public String range;
    public SheetRequestsInfo info;

    public AppendData(){}
    public AppendData(long start, long end, String category, String comment, SheetRequestsInfo info) {
        this.start = start;
        this.end = end;
        this.category = category;
        this.comment = comment;
        this.info = info;
    }
}
