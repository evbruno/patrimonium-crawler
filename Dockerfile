FROM openjdk:10

ENV SCALA_VERSION='2.12.8'

RUN wget --quiet "https://downloads.lightbend.com/scala/${SCALA_VERSION}/scala-${SCALA_VERSION}.deb" && \
    dpkg -i "scala-${SCALA_VERSION}.deb" && \
    apt-get update && \
    apt-get install scala

RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list  && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823  && \
    apt-get  update && \
    apt-get install -y sbt

VOLUME /app
WORKDIR /app

COPY . /app/

RUN ls -la /app/

# RUN sbt compile
