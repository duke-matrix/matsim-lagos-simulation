# MATSim Simulation for Lagos, Nigeria

This project provides a basic MATSim (Multi-Agent Transport Simulation) setup for Lagos, Nigeria. It includes modules for building a transport network from OpenStreetMap (OSM), generating a synthetic population, creating a basic public transit schedule, and running the simulation.

## Project Structure

- `pom.xml`: Maven project file defining dependencies (MATSim 13.0).
- `.gitignore`: Specifies files for Git to ignore.
- `README.md`: This documentation file.
- `.github/workflows/run_simulation.yml`: GitHub Actions workflow to automate the simulation.
- `src/main/java/com/example/matsim/`: Java source code directory.
  - `NetworkBuilder.java`: Converts OSM data into a MATSim network file.
  - `PopulationBuilder.java`: Creates a synthetic population with daily activity plans.
  - `TransitBuilder.java`: Generates a simple transit schedule for BRT and informal "danfo" buses.
  - `SimulationRunner.java`: The main class to configure and execute the MATSim simulation.

## Prerequisites

1.  **Java Development Kit (JDK)**: Version 11 or higher.
2.  **Apache Maven**: To build the project and manage dependencies.
3.  **Lagos OSM Data**: You need an OpenStreetMap file for Lagos in `.osm.pbf` format. For local runs, download it from [Geofabrik](https://download.geofabrik.de/africa/nigeria.html) and place it in an `input/` directory. The GitHub Action will download this automatically.

## Local Setup and Execution

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd matsim-lagos-simulation
