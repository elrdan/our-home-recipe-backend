FROM openjdk:17
RUN mkdir -p /opt/our_home_recipe/config
COPY build/libs/member-0.0.1.jar /opt/our_home_recipe/member-0.0.1.jar
ENTRYPOINT ["java","-jar","/opt/our_home_recipe/member-0.0.1.jar"]