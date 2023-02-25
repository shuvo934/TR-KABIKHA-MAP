package ttit.com.shuvo.trkabikhamap;

public class NameAndCount {

    private String name;
    private String namebefore;
    private String count;

    public NameAndCount(String name, String namebefore, String count) {
        this.name = name;
        this.namebefore = namebefore;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamebefore() {
        return namebefore;
    }

    public void setNamebefore(String namebefore) {
        this.namebefore = namebefore;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
