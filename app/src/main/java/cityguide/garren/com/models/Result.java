package cityguide.garren.com.models;

import android.location.Location;

import java.text.DecimalFormat;

public class Result {
    private String title;
    private int type;
    private double distance;
    private int rating;
    private Location location;

    public Result(String title, int type, double distance, int rating, Location location) {
        this.title = title;
        this.type = type;
        this.distance = distance;
        this.rating = rating;
        this.location = location;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDistance() {
        DecimalFormat df = new DecimalFormat(".#");
        return df.format(this.distance) + " mi";
    }

    public double getDistanceValue() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
