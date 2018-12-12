package jp.elestyle.elepaydemoandroidjava.data;

public class Product {
    private String imageUrl;
    private String title;
    private String price;

    public Product(String imageUrl, String title, String price) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.price = price;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
