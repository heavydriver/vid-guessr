package com.example.vidguessr.vidguessr;

import org.jxmapviewer.viewer.TileFactoryInfo;

public class MyMapTileFactoryInfo extends TileFactoryInfo {
    public static final int MAX_ZOOM = 19;

    public MyMapTileFactoryInfo(String name, String baseURL) {
        super(name, 0, MAX_ZOOM, MAX_ZOOM, 256, true, true, baseURL, "x", "y", "z");
    }

    @Override
    public String getTileUrl(int x, int y, int zoom) {
        int invZoom = MAX_ZOOM - zoom;
        String url = this.baseURL + "/" + invZoom + "/" + x + "/" + y + ".png?key=nfdP3ckwixP3ay8VUKSS";

        return url;
    }

    @Override
    public String getAttribution() {
        return "© MapTiler © OpenStreetMap contributors";
    }

    @Override
    public String getLicense() {
        return "Creative Commons Attribution-ShareAlike 2.0";
    }
}
