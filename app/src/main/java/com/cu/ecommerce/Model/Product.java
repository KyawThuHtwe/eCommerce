package com.cu.ecommerce.Model;

public class Product {
    String sid,pid,name,description,price,image,category,date,time,productState;

    public Product() {
    }

    public Product(String sid, String pid, String name, String description, String price, String image, String category, String date, String time, String productState) {
        this.sid = sid;
        this.pid = pid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.date = date;
        this.time = time;
        this.productState = productState;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }
}
