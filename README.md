![FNS logo](https://github.com/fnsneuralsimulator/fns-documentation_and_utilities/blob/master/FNSlogo.png?raw=true "FNS logo") FNS neural simulator
=====

FNS is an event-driven Spiking Neural Network framework, oriented to 
data-driven brain simulations. FNS combines spiking/synaptic level 
description with the event-driven approach, allowing the user to define 
heterogeneous modules and multi-scale connectivity with delayed connections 
and plastic synapses, providing fast simulations at the same time.
FNS is written in JAVA.
It comes with the GPLv3 (please see LICENSE).

* Official website: http://www.fnsneuralsimulator.org
* User guide: https://docs.google.com/document/d/1-oJK6dzu6KIggYonajqVq8xA6mUZ3ZZdBMq7zVMyTcA/export?format=pdf
This site was built using [GitHub Pages](https://pages.github.com/)
* For questions and support: fnsneuralsimulator@gmail.com


Please cite FNS
------------

When using FNS, please cite us as follows:

Gianluca Susi, Pilar Garcés, Alessandro Cristini, Emanuele Paracone, Mario 
Salerno, Fernando Maestú, Ernesto Pereda (2020). "FNS: an event-driven spiking 
neural network simulator based on the LIFL neuron model". Paper under review.
Laboratory of Cognitive and Computational Neuroscience, UPM-UCM Centre for 
Biomedical Technology, Technical University of Madrid; University of Rome 
"Tor Vergata".   



License & Copyright (Version 3.x)
-------------------

(c) 2020, Gianluca Susi, Emanuele Paracone, Mario Salerno, 
 Alessandro Cristini, Fernando Maestú.

FNS is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FNS is free software: you can redistribute it and/or modify it under the terms 
of the GNU General Public License version 3 as published by  the Free Software 
Foundation.

FNS is distributed in the hope that it will be useful, but WITHOUT ANY 
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License along with 
FNS. If not, see <http://www.gnu.org/licenses/>.



Run using Docker
------------


To run FNS with Docker, you can use the public Docker Hub image:  
`docker run --rm -v $(pwd)/experiments:/usr/local/fns/experiments -it --name fns fnsneuralsimulator/fns-simulator:nightly-latest fns experiments/myexp`  
You can also compile the FNS simulator through the docker image:  
`docker run --rm -v $(pwd)/.m2/:/root/.m2 -it --name fns fnsneuralsimulator/fns-simulator:nightly-latest compile_fns`

where:
* **experiments** could be any path in which you collect your run configurations (no matter if you use another name)
* **myexp** is an example of configuration for the present run 
* **.m2** is the .m2 directory for your maven repositories
