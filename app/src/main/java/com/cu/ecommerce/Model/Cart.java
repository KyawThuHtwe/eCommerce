package com.cu.ecommerce.Model;

public class Cart {
    String sid,pid,category,pname,image,price,quantity,discount,date,time;

    public Cart() {
    }

    public Cart(String sid, String pid, String category, String pname, String image, String price, String quantity, String discount, String date, String time) {
        this.sid = sid;
        this.pid = pid;
        this.category = category;
        this.pname = pname;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.date = date;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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
}
