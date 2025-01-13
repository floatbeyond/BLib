package common; 

import java.io.Serializable;
import java.time.LocalDate;

public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L;
    private int sub_id;
    private String sub_name;
    private String sub_status;
    private String sub_phone_num;
    private String sub_email;
    private int sub_penalties;
    private LocalDate sub_freeze;
    private LocalDate sub_joined;
    private LocalDate sub_expiration;

    public Subscriber(int sub_id, String sub_name, String sub_status, String sub_phone_num, String sub_email, 
            int sub_penalties, LocalDate sub_freeze, LocalDate sub_joined, LocalDate sub_expiration) {       
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

    public LocalDate getSub_freeze() {
        return sub_freeze;
    }

    public LocalDate getSub_joined() {
        return sub_joined;
    }

    public LocalDate getSub_expiration() {
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