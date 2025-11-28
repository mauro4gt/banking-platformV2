# Banking Platform – Microservicios de Clientes y Cuentas

Este proyecto contiene una implementación de ejemplo basada en la conversación:
- `customers-service`: CRUD de clientes, publicación de eventos a Kafka.
- `accounts-service`: cuentas y movimientos con reglas de negocio (crédito/débito, saldo no disponible).
- Módulo `common`: DTOs, eventos y excepciones compartidas.
- Script `BaseDatos.sql` para crear las tablas principales.
