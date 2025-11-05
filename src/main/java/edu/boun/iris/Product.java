package edu.boun.iris;

import java.util.ArrayList;

public class Product {
    ArrayList<String> propertyName;
    ArrayList<String> propertyValue;
    String imgLink;
    ArrayList<String> features;
    ArrayList<String> includes;
    String title;
    String brand;
    String productID;
    String description;

    public Product() {
        propertyName = new ArrayList<String>();
        propertyValue = new ArrayList<String>();
        features = new ArrayList<String>();
        includes = new ArrayList<String>();
        title = "";
        brand = "";
        productID = "";
        imgLink = "";
        description = "";
    }

    public void addPropertyName(String propertyName) {
        this.propertyName.add(propertyName);
    }

    public void addPropertyValue(String propertyValue) {
        this.propertyValue.add(propertyValue);
    }

    public void addFeature(String feature) {
        this.features.add(feature);
    }

    public void addIncludes(String includes) {
        this.includes.add(includes);
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setProductDescription(String description) {
        this.description = description;
    }
}
