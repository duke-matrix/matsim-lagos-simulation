// File: src/main/java/com/example/matsim/TransitBuilder.java
package com.example.matsim;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.vehicles.*;

import java.util.ArrayList;
import java.util.List;

public class TransitBuilder {
    private final TransitScheduleFactory scheduleFactory;
    private final VehiclesFactory vehiclesFactory;
    private final CoordinateTransformation ct;

    public TransitBuilder(Scenario scenario) {
        this.scheduleFactory = scenario.getTransitSchedule().getFactory();
        this.vehiclesFactory = scenario.getVehicles().getFactory();
        this.ct = TransformationFactory.getCoordinateTransformation("EPSG:4326", "EPSG:32631");
    }

    public void build(TransitSchedule schedule, Vehicles vehicles) {
        System.out.println("Starting transit schedule generation...");
        VehicleType brtBusType = createVehicleType("brt_bus_type", 50, 30);
        VehicleType danfoBusType = createVehicleType("danfo_bus_type", 14, 4);
        vehicles.addVehicleType(brtBusType);
        vehicles.addVehicleType(danfoBusType);

        createRoute(schedule, vehicles, "BRT-Line-1", brtBusType, 50, 600,
                createStop("brt_stop_1", 3.376, 6.524),
                createStop("brt_stop_2", 3.385, 6.508),
                createStop("brt_stop_3", 3.397, 6.491));

        createRoute(schedule, vehicles, "Danfo-Line-1", danfoBusType, 100, 300,
                createStop("danfo_stop_A", 3.402, 6.448),
                createStop("danfo_stop_B", 3.395, 6.455),
                createStop("danfo_stop_C", 3.388, 6.462));
        System.out.println("Transit schedule generation complete.");
    }

    private VehicleType createVehicleType(String id, int seats, int standing) {
        VehicleType type = vehiclesFactory.createVehicleType(Id.create(id, VehicleType.class));
        type.getCapacity().setSeats(seats);
        type.getCapacity().setStandingRoom(standing);
        return type;
    }

    private void createRoute(TransitSchedule schedule, Vehicles vehicles, String lineId, VehicleType type, int numVehicles, double headway, TransitStopFacility... stops) {
        for (TransitStopFacility stop : stops) {
            schedule.addStopFacility(stop);
        }
        TransitLine line = scheduleFactory.createTransitLine(Id.create(lineId, TransitLine.class));
        List<TransitRouteStop> routeStops = new ArrayList<>();
        double time = 0;
        for (int i = 0; i < stops.length; i++) {
            double arrivalOffset = (i == 0) ? 0 : time + 540; // 9 min travel
            double departureOffset = arrivalOffset + 60; // 1 min stop
            time = departureOffset;
            routeStops.add(scheduleFactory.createTransitRouteStop(stops[i], arrivalOffset, departureOffset));
        }

        TransitRoute route = scheduleFactory.createTransitRoute(Id.create(lineId + "_route", TransitRoute.class), null, routeStops, "bus");
        for (int i = 0; i < numVehicles; i++) {
            Id<Vehicle> vehId = Id.create(type.getId().toString() + "_" + i, Vehicle.class);
            vehicles.addVehicle(vehiclesFactory.createVehicle(vehId, type));
            Departure dep = scheduleFactory.createDeparture(Id.create("dep_" + lineId + "_" + i, Departure.class), 6 * 3600 + i * headway);
            dep.setVehicleId(vehId);
            route.addDeparture(dep);
        }
        line.addRoute(route);
        schedule.addTransitLine(line);
    }

    private TransitStopFacility createStop(String id, double lon, double lat) {
        Coord coord = ct.transform(new Coord(lon, lat));
        return scheduleFactory.createTransitStopFacility(Id.create(id, TransitStopFacility.class), coord, false);
    }
}