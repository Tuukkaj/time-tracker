package tuni.tuukka.google;

public class DataCategory extends Data {
    public String category;

    public DataCategory(String category, SheetRequestsInfo info) {
        super(info);
        this.category = category;
    }
}
