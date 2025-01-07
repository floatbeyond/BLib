package common; 

import java.io.Serializable;
import java.sql.Date;

public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L;
    private int sub_id;
    private String sub_name;
    private String sub_status;
    private String sub_phone_num;
    private String sub_email;
    private int sub_penalties;
    private Date sub_freeze;
    private Date sub_joined;
    private Date sub_expiration;

    public Subscriber(int sub_id, String sub_name, String sub_status, String sub_phone_num, String sub_email, 
            int sub_penalties, Date sub_freeze, Date sub_joined, Date sub_expiration) {       
        this.sub_id = sub_id;
        this.sub_name = sub_name;
        this.sub_status = sub_status;
        this.sub_phone_num = sub_phone_num;
        this.sub_email = sub_email;
        this.sub_penalties = sub_penalties;
        this.sub_freeze = sub_freeze;
        this.sub_joined = sub_joined;
        this.sub_expiration = sub_expiration;
    }

    public int getSub_id() {
        return sub_id;
    }

    public String getSub_name() {
        return sub_name;
    }

    public String getSub_status() {
        return sub_status;
    }

    public String getSub_phone_num() {
        return sub_phone_num;
    }

    public String getSub_email() {
        return sub_email;
    }

    public int getSub_penalties() {
        return sub_penalties;
    }

    public Date getSub_freeze() {
        return sub_freeze;
    }

    public Date getSub_joined() {
        return sub_joined;
    }

    public Date getSub_expiration() {
        return sub_expiration;
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "sub_id=" + sub_id +
                ", sub_name='" + sub_name + '\'' +
                ", sub_status='" + sub_status + '\'' +
                ", sub_phone_num='" + sub_phone_num + '\'' +
                ", sub_email='" + sub_email + '\'' +
                ", sub_penalties=" + sub_penalties +
                ", sub_freeze=" + sub_freeze +
                ", sub_joined=" + sub_joined +
                ", sub_expiration=" + sub_expiration +
                '}';
    }
}