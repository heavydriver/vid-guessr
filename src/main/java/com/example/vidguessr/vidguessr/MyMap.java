package com.example.vidguessr.vidguessr;

import javafx.scene.control.Button;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.*;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyMap extends JComponent {
    public static final double EARTH_RADIUS = 6371.0;

    private JXMapViewer mapViewer;
    private double markerLat;
    private double markerLng;
    private Button confirmButton;

    public MyMap(Button button) {
        confirmButton = button;
        drawMap();
    }

    private void drawMap() {
        TileFactoryInfo info = new MyMapTileFactoryInfo("", "https://api.maptiler.com/maps/streets-v2/256");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        // Setup JXMapViewer
        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        GeoPosition defaultLocation = new GeoPosition(37.0902, -95.7129);

        mapViewer.setZoom(15);
        mapViewer.setAddressLocation(defaultLocation);

        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        mapViewer.addMouseListener(new MapClickListener(mapViewer) {
            @Override
            public void mapClicked(GeoPosition geoPosition) {
                System.out.println(geoPosition.getLatitude() + " " + geoPosition.getLongitude());
                GeoPosition marker = new GeoPosition(geoPosition.getLatitude(), geoPosition.getLongitude());

                markerLat = geoPosition.getLatitude();
                markerLng = geoPosition.getLongitude();

                Set<Waypoint> waypoints = new HashSet<Waypoint>(List.of(new DefaultWaypoint(marker)));

                WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
                waypointPainter.setWaypoints(waypoints);
                waypointPainter.setRenderer(new MyWaypointRenderer());

                mapViewer.setOverlayPainter(waypointPainter);
                confirmButton.setDisable(false);
            }
        });
    }

    public JXMapViewer getMap() {
        return mapViewer;
    }

    public GeoPosition getMarkerPos() {
        return new GeoPosition(markerLat, markerLng);
    }

    public double calculateDistance(GeoPosition markerPos, GeoPosition actualPos) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = Math.toRadians(markerPos.getLatitude());
        double lon1Rad = Math.toRadians(markerPos.getLongitude());
        double lat2Rad = Math.toRadians(actualPos.getLatitude());
        double lon2Rad = Math.toRadians(actualPos.getLongitude());

        // Calculate the differences in coordinates
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        return EARTH_RADIUS * c;
    }
}