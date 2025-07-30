package me.khun.ybsway.entity;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class BusStop implements Serializable {
    private Integer id;
    private String nameMM;
    private String nameEN;
    private String streetMM;
    private String streetEN;
    private String townshipMM;
    private String townshipEN;
    private Coordinate coordinate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameMM() {
        return nameMM;
    }

    public void setNameMM(String nameMM) {
        this.nameMM = nameMM;
    }

    public String getStreetMM() {
        return streetMM;
    }

    public void setStreetMM(String streetMM) {
        this.streetMM = streetMM;
    }

    public String getTownshipMM() {
        return townshipMM;
    }

    public void setTownshipMM(String townshipMM) {
        this.townshipMM = townshipMM;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getStreetEN() {
        return streetEN;
    }

    public void setStreetEN(String streetEN) {
        this.streetEN = streetEN;
    }

    public String getTownshipEN() {
        return townshipEN;
    }

    public void setTownshipEN(String townshipEN) {
        this.townshipEN = townshipEN;
    }

    public Double getLatitude() {
        return coordinate == null ? null : coordinate.getLatitude();
    }

    public Double getLongitude() {
        return coordinate == null ? null : coordinate.getLongitude();
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof BusStop)) {
            return false;
        }
        return Objects.equals(id, ((BusStop) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
