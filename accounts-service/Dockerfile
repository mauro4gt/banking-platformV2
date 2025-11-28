  accounts-service:
    build:
      context: ./accounts-service
      dockerfile: Dockerfile
    container_name: accounts-service
    depends_on:
      - accounts-db
      # - kafka   # (hablamos de esto abajo)
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://accounts-db:5432/accounts
      SPRING_DATASOURCE_USERNAME: accounts
      SPRING_DATASOURCE_PASSWORD: accounts
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    ports:
      - "8082:8082"
    networks:
      - banking-net
