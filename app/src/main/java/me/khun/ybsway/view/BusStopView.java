package me.khun.ybsway.view;

import androidx.annotation.Nullable;
import java.util.Objects;
import me.khun.ybsway.entity.Coordinate;

public class BusStopView {
    private Integer id;
    private String name;
    private String roadName;
    private String townshipName;
    private Coordinate coordinate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getTownshipName() {
        return townshipName;
    }

    public void setTownshipName(String townshipName) {
        this.townshipName = townshipName;
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

    public String formatText() {
        return String.format("%s %s %s", name, roadName, townshipName);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof BusStopView)) {
            return false;
        }
        return Objects.equals(id, ((BusStopView) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
