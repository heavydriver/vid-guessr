package com.example.vidguessr.vidguessr;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import javafx.scene.control.Button;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.*;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

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
                waypointPainter.setRenderer(new MyWaypointRenderer("player"));

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

    public void drawRoute(GeoPosition markerPos, GeoPosition actualPos) {
        List<GeoPosition> track = Arrays.asList(markerPos, actualPos);
        MyRoutePainter routePainter = new MyRoutePainter(track);

        // Set the focus
        mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);

        // Create waypoints from the geo-positions
        Set<Waypoint> playerWaypoints = new HashSet<Waypoint>(List.of(new DefaultWaypoint(markerPos)));
        WaypointPainter<Waypoint> playerWaypointsPainter = new WaypointPainter<Waypoint>();
        playerWaypointsPainter.setWaypoints(playerWaypoints);
        playerWaypointsPainter.setRenderer(new MyWaypointRenderer("player"));

        Set<Waypoint> actualWaypoints = new HashSet<Waypoint>(List.of(new DefaultWaypoint(actualPos)));
        WaypointPainter<Waypoint> actualWaypointsPainter = new WaypointPainter<Waypoint>();
        actualWaypointsPainter.setWaypoints(actualWaypoints);
        actualWaypointsPainter.setRenderer(new MyWaypointRenderer("actualLocation"));

        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
        painters.add(routePainter);
        painters.add(playerWaypointsPainter);
        painters.add(actualWaypointsPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        mapViewer.setOverlayPainter(painter);
    }
}