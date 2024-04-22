package com.example.vidguessr.vidguessr;

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
    private JXMapViewer mapViewer;

    public MyMap() {
        drawMap();
    }

    private void drawMap() {
        TileFactoryInfo info = new OSMTileFactoryInfo();
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
                GeoPosition frankfurt = new GeoPosition(geoPosition.getLatitude(), geoPosition.getLongitude());

                Set<Waypoint> waypoints = new HashSet<Waypoint>(List.of(new DefaultWaypoint(frankfurt)));

                WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
                waypointPainter.setWaypoints(waypoints);
                waypointPainter.setRenderer(new MyWaypointRenderer());

                mapViewer.setOverlayPainter(waypointPainter);
            }
        });
    }

    public JXMapViewer getMap() {
        return mapViewer;
    }
}