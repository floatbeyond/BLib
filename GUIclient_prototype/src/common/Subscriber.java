package common; // or package server;

import java.io.Serializable;

public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L;
    private int sub_id;
    private String sub_name;
    private int detailed_sub_history;
    private String sub_phone_num;
    private String sub_email;

    public Subscriber(int sub_id, String sub_name, int detailed_sub_history, String sub_phone_num, String sub_email) {
        this.sub_id = sub_id;
        this.sub_name = sub_name;
        this.detailed_sub_history = detailed_sub_history;
        this.sub_phone_num = sub_phone_num;
        this.sub_email = sub_email;
    }

    public int getSub_id() {
        return sub_id;
    }

    public String getSub_name() {
        return sub_name;
    }

    public int getDetailed_sub_history() {
        return detailed_sub_history;
    }

    public String getSub_phone_num() {
        return sub_phone_num;
    }

    public String getSub_email() {
        return sub_email;
    }

    @Override
    public String toString() {
        return sub_id + " " + sub_name + " " + detailed_sub_history + " " + sub_phone_num + " " + sub_email;
    }
}