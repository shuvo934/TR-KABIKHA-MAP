package ttit.com.shuvo.trkabikhamap;

public class CountCheck {

    private String id;
    private String count;

    public CountCheck(String id, String count) {
        this.id = id;
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
