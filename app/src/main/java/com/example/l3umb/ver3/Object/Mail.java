package com.example.l3umb.ver3.Object;

public class Mail {
    private String from_user_id;
    private String to_user_id;
    private String title;
    private String content;
    private String created_date;
    private int type;
    private int mail_type;

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTitle() {
        if (getMail_type() == 2 && getType() == 1) {
            return title.replace("Received", "Transfer");
        } else {
            return title;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public int getType() {
        return type;
    }

    public int getMail_type() {
        return mail_type;
    }

    public void setMail_type(int mail_type) {
        this.mail_type = mail_type;
    }
}
