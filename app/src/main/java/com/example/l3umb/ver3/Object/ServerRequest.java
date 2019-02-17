package com.example.l3umb.ver3.Object;

public class ServerRequest {
    private String operation;
    private User user;
    private Product product;
    private Comment comment;
    private Mail mail;
    private News news;
    private Currency currency;
    private Statiscal statiscal;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setStatiscal(Statiscal statiscal) {
        this.statiscal = statiscal;
    }

    public void setNews(News news) {
        this.news = news;
    }
}
