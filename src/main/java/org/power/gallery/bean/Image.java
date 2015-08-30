package org.power.gallery.bean;

public class Image {

    private String id;
    private String src;
    private String alt;
    private String href;

    public Image() {
    }

    public Image(String src, String alt) {
        this.src = src;
        this.alt = alt;
    }

    public Image(String id, String src, String alt) {
        this.id = id;
        this.src = src;
        this.alt = alt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
