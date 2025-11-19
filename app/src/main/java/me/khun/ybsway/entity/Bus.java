package me.khun.ybsway.entity;

import java.io.Serializable;
import java.util.List;

public class Bus implements Serializable {
    private static final long serialVersionUID = 1L;

    private String routeId;
    private String nameMM;
    private String nameEN;
    private String subNameMM;
    private String subNameEN;
    private String originNameMM;
    private String originNameEN;
    private String destinationNameMM;
    private String destinationNameEN;
    private String agencyId;
    private String hexColorCode;
    private boolean isActive;
    private List<Integer> busStopIdList;
    private List<Coordinate> routeCoordinateList;
    private Route route;

    public Bus() {}

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getNameMM() {
        return nameMM;
    }

    public void setNameMM(String nameMM) {
        this.nameMM = nameMM;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getSubNameMM() {
        return subNameMM;
    }

    public void setSubNameMM(String subNameMM) {
        this.subNameMM = subNameMM;
    }

    public String getSubNameEN() {
        return subNameEN;
    }

    public void setSubNameEN(String subNameEN) {
        this.subNameEN = subNameEN;
    }

    public void setOriginNameMM(String originNameMM) {
        this.originNameMM = originNameMM;
    }

    public String getOriginNameEN() {
        return originNameEN;
    }

    public void setOriginNameEN(String originNameEN) {
        this.originNameEN = originNameEN;
    }

    public void setDestinationNameMM(String destinationNameMM) {
        this.destinationNameMM = destinationNameMM;
    }

    public String getDestinationNameEN() {
        return destinationNameEN;
    }

    public void setDestinationNameEN(String destinationNameEN) {
        this.destinationNameEN = destinationNameEN;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getOriginNameMM() {
        return originNameMM;
    }

    public String getDestinationNameMM() {
        return destinationNameMM;
    }

    public String getHexColorCode() {
        return hexColorCode;
    }

    public void setHexColorCode(String hexColorCode) {
        this.hexColorCode = hexColorCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Integer> getBusStopIdList() {
        return List.copyOf(busStopIdList);
    }

    public void setBusStopIdList(List<Integer> busStopIdList) {
        this.busStopIdList = busStopIdList;
    }

    public List<Coordinate> getRouteCoordinateList() {
        return routeCoordinateList;
    }

    public void setRouteCoordinateList(List<Coordinate> routeCoordinateList) {
        this.routeCoordinateList = routeCoordinateList;
    }
}
