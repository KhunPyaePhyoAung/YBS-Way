package me.khun.ybsway.view;

import java.util.List;

import me.khun.ybsway.entity.Coordinate;

public class BusView {
    private String routeId;
    private String name;
    private String number;
    private String originName;
    private String destinationName;
    private String hexColorCode;
    private List<Integer> busStopIdList;
    private List<Coordinate> routeCoordinateList;
    private Integer displayIconId;
    private List<BusStopView> busStopViewList;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getHexColorCode() {
        return hexColorCode;
    }

    public void setHexColorCode(String hexColorCode) {
        this.hexColorCode = hexColorCode;
    }

    public List<Integer> getBusStopIdList() {
        return busStopIdList;
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

    public List<BusStopView> getBusStopViewList() {
        return busStopViewList;
    }

    public void setBusStopViewList(List<BusStopView> busStopViewList) {
        this.busStopViewList = busStopViewList;
    }

    public Integer getDisplayIconId() {
        return displayIconId;
    }

    public void setDisplayIconId(Integer displayIconId) {
        this.displayIconId = displayIconId;
    }
}
