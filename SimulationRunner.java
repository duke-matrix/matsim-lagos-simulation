// File: src/main/java/com/example/matsim/SimulationRunner.java
package com.example.matsim;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitScheduleWriter;
import org.matsim.vehicles.VehicleWriterV1;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimulationRunner {

    public static void main(String[] args) throws java.io.IOException {
        String outputDirectory = "output";
        String simulationOutput = outputDirectory + "/simulation_output";
        String networkFile = outputDirectory + "/lagos_network.xml.gz";
        String populationFile = outputDirectory + "/lagos_population.xml.gz";
        String transitScheduleFile = outputDirectory + "/lagos_transit_schedule.xml.gz";
        String transitVehiclesFile = outputDirectory + "/lagos_transit_vehicles.xml.gz";
        String osmFile = "input/lagos-latest.osm.pbf";

        Files.createDirectories(Paths.get(outputDirectory));

        if (!new File(networkFile).exists()) {
            NetworkBuilder.build(osmFile, networkFile);
        }
        if (!new File(populationFile).exists()) {
             Config tempConfig = ConfigUtils.createConfig();
             Scenario tempScenario = ScenarioUtils.createScenario(tempConfig);
             new PopulationBuilder(tempScenario.getPopulation().getFactory()).build(tempScenario.getPopulation(), 10000); // 10k agents
             new PopulationWriter(tempScenario.getPopulation()).write(populationFile);
        }
        if (!new File(transitScheduleFile).exists() || !new File(transitVehiclesFile).exists()) {
             Config tempConfig = ConfigUtils.createConfig();
             Scenario tempScenario = ScenarioUtils.createScenario(tempConfig);
             new TransitBuilder(tempScenario).build(tempScenario.getTransitSchedule(), tempScenario.getVehicles());
             new TransitScheduleWriter(tempScenario.getTransitSchedule()).writeFile(transitScheduleFile);
             new VehicleWriterV1(tempScenario.getVehicles()).writeFile(transitVehiclesFile);
        }

        Config config = ConfigUtils.createConfig();
        config.network().setInputFile(networkFile);
        config.plans().setInputFile(populationFile);
        config.transit().setUseTransit(true);
        config.transit().setTransitScheduleFile(transitScheduleFile);
        config.transit().setVehiclesFile(transitVehiclesFile);

        config.controler().setOutputDirectory(simulationOutput);
        config.controler().setFirstIteration(0);
        config.controler().setLastIteration(10);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

        addActivityParams(config, "home", 12 * 3600);
        addActivityParams(config, "work", 8 * 3600);
        addActivityParams(config, "market", 1 * 3600);
        addActivityParams(config, "school", 1.5 * 3600);
        
        addStrategy(config, "ChangeExpBeta", 0.8, 0);
        addStrategy(config, "ReRoute", 0.1, 0);
        addStrategy(config, "SubtourModeChoice", 0.1, 7);

        Scenario scenario = ScenarioUtils.loadScenario(config);
        Controler controler = new Controler(scenario);
        
        System.out.println("Starting MATSim simulation...");
        controler.run();
        System.out.println("Simulation finished. Output located in: " + simulationOutput);
    }

    private static void addActivityParams(Config config, String type, double typicalDuration) {
        PlanCalcScoreConfigGroup.ActivityParams params = new PlanCalcScoreConfigGroup.ActivityParams(type);
        params.setTypicalDuration(typicalDuration);
        config.planCalcScore().addActivityParams(params);
    }

    private static void addStrategy(Config config, String name, double weight, int disableAfter) {
        StrategyConfigGroup.StrategySettings settings = new StrategyConfigGroup.StrategySettings();
        settings.setStrategyName(name);
        settings.setWeight(weight);
        if (disableAfter > 0) {
            settings.setDisableAfter(disableAfter);
        }
        config.strategy().addStrategySettings(settings);
    }
}