![FNS logo](https://github.com/fnsneuralsimulator/FNS-scripts_and_tools/blob/master/FNSlogo.png?raw=true "FNS logo") FNS neural simulator
=====

FNS is an event-driven Spiking Neural Network framework, oriented to 
data-driven brain simulations. FNS combines spiking/synaptic level 
description with the event-driven approach, allowing the user to define 
heterogeneous modules and multi-scale connectivity with delayed connections 
and plastic synapses, providing fast simulations at the same time.
FNS is written in JAVA.
It comes with the GPLv3 (please see LICENSE).

* **Official website** [here](http://www.fnsneuralsimulator.org)
* **User guide** [here](https://docs.google.com/document/d/1-oJK6dzu6KIggYonajqVq8xA6mUZ3ZZdBMq7zVMyTcA/export?format=pdf) (please wait a moment after the click)
* **For questions and support**: fnsneuralsimulator@gmail.com


Please cite FNS
------------

When using FNS, please cite us as follows:

Gianluca Susi, Pilar Garcés, Emanuele Paracone, Alessandro Cristini, Mario 
Salerno, Fernando Maestú, Ernesto Pereda (2020). "FNS: an event-driven spiking 
neural network simulator based on the LIFL neuron model". Paper under review.
Laboratory of Cognitive and Computational Neuroscience, UPM-UCM Centre for 
Biomedical Technology, Technical University of Madrid; University of Rome 
"Tor Vergata".   


Run using Docker
------------

To run FNS with [Docker](https://docs.docker.com/install/), you can use the public Docker Hub image. 

Please navigate the terminal until the FNS folder (where you placed the `[SIMULATION_FOLDER]` ) and type the following command (consider the prefix `sudo` for linux privileges):

`docker run --rm -v $(pwd)/[SIMULATION_FOLDER]:/usr/local/fns/[SIMULATION_FOLDER] -it -e JAVA_OPTS="" --name fns fnsneuralsimulator/fns-simulator:latest fns [SIMULATION_FOLDER/EXPERIMENT][SWITCHES]`

* replace `-it` with `-d` if you prefer to detach and run FNS in the background;
* specify the field `JAVA_OPTS` in case you need to modify the heap size.

where `[SIMULATION_FOLDER]` is the folder which contains the simulation packages, `[EXPERIMENT]` is the package which contains the set of configuration files for a single simulation, and `.m2` is the directory for your maven repositories. 

To make sure you are using the latest version of Docker, type:

`docker pull fnsneuralsimulator/fns-simulator:latest`

Please refer to the [user guide](https://docs.google.com/document/d/1-oJK6dzu6KIggYonajqVq8xA6mUZ3ZZdBMq7zVMyTcA/export?format=pdf) for additional information.

License & Copyright (Version 3.x)
-------------------

(c) 2020, Gianluca Susi, Emanuele Paracone, Mario Salerno, 
 Alessandro Cristini, Fernando Maestú.

FNS is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FNS is distributed in the hope that it will be useful, but WITHOUT ANY 
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License along with 
FNS. If not, see <http://www.gnu.org/licenses/>.

