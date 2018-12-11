FROM java:8

ARG JAR_FILE

CMD mdkir -p /data/logs
CMD mkdir /data/web

VOLUME /data

# Copy local file to image
COPY ${JAR_FILE} /data/web/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/data/web/app.jar"]

HEALTHCHECK --interval=5s --timeout=3s --retries=5 CMD curl --fail http://localhost:8080/health || exit 1

CMD [""]