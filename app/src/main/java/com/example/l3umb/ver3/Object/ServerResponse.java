package com.example.l3umb.ver3.Object;

import java.util.List;

public class ServerResponse {
    private String result;
    private String message;
    private User user;
    private Product product;
    private Statiscal statiscal;
    private News news;
    private List<Product> products;
    private List<User> users;
    private List<Comment> comments;
    private List<Mail> mails;
    private List<News> newsList;
    private List<Banner> banners;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public Statiscal getStatiscal() {
        return statiscal;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Mail> getMails() {
        return mails;
    }

    public News getNews() {
        return news;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public List<Banner> getBanners() {
        return banners;
    }
}
