// File: src/main/java/com/example/matsim/NetworkBuilder.java
package com.example.matsim;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.contrib.osm.networkReader.OsmNetworkReader;

public class NetworkBuilder {
    private static final String WGS84 = "EPSG:4326";
    private static final String UTM31N = "EPSG:32631";

    public static void build(String osmFilePath, String outputNetworkPath) {
        System.out.println("Starting network generation from OSM data...");
        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
        Network network = scenario.getNetwork();
        CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(WGS84, UTM31N);

        OsmNetworkReader reader = new OsmNetworkReader(network, ct);
        reader.setHighwayDefaults(1, "motorway", 2, 120.0/3.6, 1.0, 2000, true);
        reader.setHighwayDefaults(2, "trunk", 2, 120.0/3.6, 1.0, 2000, true);
        reader.setHighwayDefaults(3, "primary", 1, 80.0/3.6, 1.0, 1500, false);
        reader.setHighwayDefaults(4, "secondary", 1, 60.0/3.6, 1.0, 1200, false);
        reader.setHighwayDefaults(5, "tertiary", 1, 50.0/3.6, 1.0, 1000, false);
        reader.setHighwayDefaults(6, "unclassified", 1, 40.0/3.6, 1.0, 800, false);
        reader.setHighwayDefaults(6, "residential", 1, 30.0/3.6, 1.0, 600, false);
        reader.parse(osmFilePath);

        new NetworkWriter(network).write(outputNetworkPath);
        System.out.println("Network generation complete. File saved to: " + outputNetworkPath);
    }
}