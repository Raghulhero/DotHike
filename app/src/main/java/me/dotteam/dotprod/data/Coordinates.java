package me.dotteam.dotprod.data;

import android.content.ContentValues;

/**
 * Data structure to represent Geographical Locations
 */
public class Coordinates {

    protected double longitude;
    protected double latitude;
    protected double altitude;


    /**
     * Default constructor
     */
    public Coordinates(double longitude, double lat, double alt) {
        this.longitude=longitude;
        latitude=lat;
        altitude=alt;
    }

    /**
     * Method to obtain an object representing the Coordinates instance to be used for storage
     * @return ContentValues object with correct Key (String) to Value pairings
     */
    public ContentValues toStorage(int ID) {
        ContentValues retVal = new ContentValues();
        retVal.put(DBAssistant.HIKE_ID,ID);
        retVal.put(DBAssistant.LONG_COL,this.longitude);
        retVal.put(DBAssistant.LAT_COL,this.latitude);
        retVal.put(DBAssistant.ALT_COL,this.altitude);
        return retVal;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public String toString(){
        return String.format("Lat %.3f Long %.3f Alt %.3f",latitude,longitude,altitude);
    }
}