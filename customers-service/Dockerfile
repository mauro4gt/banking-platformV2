  customers-service:
    build:
      context: ./customers-service
      dockerfile: Dockerfile
    container_name: customers-service
    depends_on:
      - customers-db
      # - kafka
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://customers-db:5432/customers
      SPRING_DATASOURCE_USERNAME: customers
      SPRING_DATASOURCE_PASSWORD: customers
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    ports:
      - "8081:8081"
    networks:
      - banking-net
