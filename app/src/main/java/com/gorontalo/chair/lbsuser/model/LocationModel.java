package com.gorontalo.chair.lbsuser.model;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private String id, name, email, idgrup, namagrup, latitude, longitude;

    public LocationModel() {
    }

    public LocationModel(String name, String latitude, String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdgrup() {
        return idgrup;
    }

    public void setIdgrup(String idgrup) {
        this.idgrup = idgrup;
    }

    public String getNamagrup() {
        return namagrup;
    }

    public void setNamagrup(String namagrup) {
        this.namagrup = namagrup;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
