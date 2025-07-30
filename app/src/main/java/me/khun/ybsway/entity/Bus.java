package me.khun.ybsway.entity;

import java.util.List;

public class Bus {
    private String routeId;
    private String name;
    private String start;
    private String end;
    private String number;
    private String hexColorCode;
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

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getStartName() {
        System.out.println(name);
        return name.substring(name.indexOf(") ") + 2, name.indexOf(" -"));
    }

    public String getEndName() {
        return  name.substring(name.lastIndexOf(" ") + 1);
    }

    public String getHexColorCode() {
        return hexColorCode;
    }

    public void setHexColorCode(String hexColorCode) {
        this.hexColorCode = hexColorCode;
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
