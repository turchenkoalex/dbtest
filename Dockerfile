FROM amazoncorretto:21-alpine3.21 AS builder

ADD . /app
WORKDIR /app

RUN /app/gradlew --no-daemon distTar && \
    cd /app/build/distributions/ && \
    tar -xf /app/build/distributions/dbtest-1.0-SNAPSHOT.tar && \
    rm /app/build/distributions/dbtest-1.0-SNAPSHOT.tar

FROM amazoncorretto:21-alpine3.21
COPY --from=builder /app/build/distributions/dbtest-1.0-SNAPSHOT /app
WORKDIR /app/bin
ENTRYPOINT ["/app/bin/dbtest"]
