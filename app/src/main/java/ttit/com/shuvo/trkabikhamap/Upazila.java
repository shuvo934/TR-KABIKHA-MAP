package ttit.com.shuvo.trkabikhamap;

public class Upazila {

    private String dd_dist_id;
    private String dd_id;
    private String thana_name;

    public Upazila(String dd_dist_id, String dd_id, String thana_name) {
        this.dd_dist_id = dd_dist_id;
        this.dd_id = dd_id;
        this.thana_name = thana_name;
    }

    public String getDd_dist_id() {
        return dd_dist_id;
    }

    public void setDd_dist_id(String dd_dist_id) {
        this.dd_dist_id = dd_dist_id;
    }

    public String getDd_id() {
        return dd_id;
    }

    public void setDd_id(String dd_id) {
        this.dd_id = dd_id;
    }

    public String getThana_name() {
        return thana_name;
    }

    public void setThana_name(String thana_name) {
        this.thana_name = thana_name;
    }
}
