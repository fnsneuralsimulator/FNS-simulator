FROM maven:3-openjdk-15

ENV workdir /usr/local/fns
ADD ./* ${workdir}
WORKDIR ${workdir}

