// File: src/main/java/com/example/matsim/PopulationBuilder.java
package com.example.matsim;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;

import java.util.Random;

public class PopulationBuilder {
    private final Random random = new Random();
    private final PopulationFactory populationFactory;
    private final CoordinateTransformation ct;
    private static final double MIN_LON = 3.0, MAX_LON = 4.0, MIN_LAT = 6.4, MAX_LAT = 6.7;

    public PopulationBuilder(PopulationFactory populationFactory) {
        this.populationFactory = populationFactory;
        this.ct = TransformationFactory.getCoordinateTransformation("EPSG:4326", "EPSG:32631");
    }

    public void build(Population population, int numPersons) {
        System.out.println("Starting population generation for " + numPersons + " persons...");
        for (int i = 0; i < numPersons; i++) {
            Person person = populationFactory.createPerson(Id.createPersonId("person_" + i));
            Plan plan = populationFactory.createPlan();
            Coord homeCoord = createRandomCoord();

            plan.addActivity(createActivity("home", homeCoord, 6 * 3600 + random.nextDouble() * 3600));
            plan.addLeg(createLeg());
            plan.addActivity(createActivity("work", createRandomCoord(), 14 * 3600 + random.nextDouble() * 3600));
            plan.addLeg(createLeg());
            plan.addActivity(createActivity("market", createRandomCoord(), 16 * 3600 + random.nextDouble() * 1800));
            plan.addLeg(createLeg());
            plan.addActivity(createActivity("school", createRandomCoord(), 17 * 3600 + random.nextDouble() * 1800));
            plan.addLeg(createLeg());
            plan.addActivity(populationFactory.createActivityFromCoord("home", homeCoord));

            person.addPlan(plan);
            population.addPerson(person);
        }
        System.out.println("Population generation complete.");
    }

    private Activity createActivity(String type, Coord coord, double endTime) {
        Activity activity = populationFactory.createActivityFromCoord(type, coord);
        activity.setEndTime(endTime);
        return activity;
    }

    private Leg createLeg() {
        return populationFactory.createLeg(getRandomTransportMode());
    }

    private Coord createRandomCoord() {
        double lon = MIN_LON + random.nextDouble() * (MAX_LON - MIN_LON);
        double lat = MIN_LAT + random.nextDouble() * (MAX_LAT - MIN_LAT);
        return ct.transform(new Coord(lon, lat));
    }

    private String getRandomTransportMode() {
        double rand = random.nextDouble();
        if (rand < 0.4) return "car";
        if (rand < 0.8) return "bus";
        return "walk";
    }
}