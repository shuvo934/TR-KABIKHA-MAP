package ttit.com.shuvo.trkabikhamap;

public class Division {

    private String div_id;
    private String div_name;

    public Division(String div_id, String div_name) {
        this.div_id = div_id;
        this.div_name = div_name;
    }

    public String getDiv_id() {
        return div_id;
    }

    public void setDiv_id(String div_id) {
        this.div_id = div_id;
    }

    public String getDiv_name() {
        return div_name;
    }

    public void setDiv_name(String div_name) {
        this.div_name = div_name;
    }
}
