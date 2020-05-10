FROM maven:3-openjdk-15

ENV workdir /usr/local/fns
ADD src/ ${workdir}/src/
ADD target/ ${workdir}/target/
ADD compile ${workdir}/compile_fns
ADD LICENSE ${workdir}/LICENSE
ADD pom.xml ${workdir}/pom.xml
ADD start ${workdir}/fns
ADD VERSION.txt ${workdir}/VERSION.txt
ADD README.md ${workdir}/README.md
ENV PATH ${workdir}:$PATH
WORKDIR ${workdir}

