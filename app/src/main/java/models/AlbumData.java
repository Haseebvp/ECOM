package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by haseeb on 20/2/17.
 */

public class AlbumData {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("next")
    @Expose
    private Boolean next;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Boolean getNext() {
        return next;
    }

    public void setNext(Boolean next) {
        this.next = next;
    }
}
