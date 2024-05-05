/**
 * Custom map tiles using MapTiler.com
 * @author Varun Mange
 * Collaborators: Azfar Islam, Martin Steiger
 * Teacher Name: Ms. Bailey
 * Period: 5th
 * Due Date: 5/10/2024
 */

package com.example.vidguessr.vidguessr.map;

import org.jxmapviewer.viewer.TileFactoryInfo;

public class MyMapTileFactoryInfo extends TileFactoryInfo
{
    public static final int MAX_ZOOM = 19;
    public static final int TILE_SIZE = 256;

    private static final String MAP_TILER_KEY = "nfdP3ckwixP3ay8VUKSS";

    /**
     * Initializes the MyMapTileFactoryInfo object
     * @param name the name of the map tile factory
     * @param baseURL the base url of the map tile provider
     */
    public MyMapTileFactoryInfo(String name, String baseURL)
    {
        super(name, 0, MAX_ZOOM, MAX_ZOOM, TILE_SIZE, true, true,
                baseURL, "x", "y", "z");
    }

    /**
     * Modifies the tile url with the api key
     * @param x the x value, measured from left to right
     * @param y the y value, measured from top to bottom
     * @param zoom the zoom level
     * @return the modified tile url
     */
    @Override
    public String getTileUrl(int x, int y, int zoom)
    {
        int invZoom = MAX_ZOOM - zoom;
        String url = this.baseURL + "/" + invZoom + "/" + x + "/" + y +
                ".png?key=" + MAP_TILER_KEY;

        return url;
    }

    /**
     * Gets the providers of the map tiles
     * @return the sources of the map
     */
    @Override
    public String getAttribution()
    {
        return "© MapTiler © OpenStreetMap contributors";
    }

    /**
     * Gets the license for the map tiles
     * @return the license to use the map sources
     */
    @Override
    public String getLicense()
    {
        return "Creative Commons Attribution-ShareAlike 2.0";
    }
}
