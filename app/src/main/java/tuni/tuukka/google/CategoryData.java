package tuni.tuukka.google;

public class CategoryData {
    public CategoryData(){}

    public CategoryData(String category, SheetRequestsInfo info) {
        this.category = category;
        this.info = info;
    }

    public String category;
    public SheetRequestsInfo info;
}
