package ttit.com.shuvo.trkabikhamap;

public class District {

    private String dist_div_id;
    private String dist_id;
    private String dist_name;

    public District(String dist_div_id, String dist_id, String dist_name) {
        this.dist_div_id = dist_div_id;
        this.dist_id = dist_id;
        this.dist_name = dist_name;
    }

    public String getDist_div_id() {
        return dist_div_id;
    }

    public void setDist_div_id(String dist_div_id) {
        this.dist_div_id = dist_div_id;
    }

    public String getDist_id() {
        return dist_id;
    }

    public void setDist_id(String dist_id) {
        this.dist_id = dist_id;
    }

    public String getDist_name() {
        return dist_name;
    }

    public void setDist_name(String dist_name) {
        this.dist_name = dist_name;
    }
}
