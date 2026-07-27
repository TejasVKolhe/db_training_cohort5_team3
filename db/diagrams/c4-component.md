# C4 Component — recon-service API

```mermaid
C4Component
title C4 Component — recon-service API

Container_Boundary(api, "recon-service API") {

    Component(tradeController, "TradeController", "REST Controller", "Handles trade APIs")
    Component(reconController, "ReconController", "REST Controller", "Handles reconciliation APIs")
    Component(authController, "AuthController", "REST Controller", "Handles authentication")
    Component(reportController, "ReportController", "REST Controller", "Handles reports")

    Component(jwtFilter, "JwtAuthFilter", "Security Filter", "Validates JWT tokens")
    Component(methodSecurity, "MethodSecurity", "Security", "Applies authorization rules")

    Component(tradeService, "TradeService", "Service", "Business logic for trades")
    Component(reconService, "ReconService", "Service", "Business logic for reconciliation")
    Component(reportService, "ReportService", "Service", "Generates reports")

    Component(tradeRepo, "TradeRepository", "Repository", "Accesses trade data")
    Component(reconRepo, "ReconRepository", "Repository", "Accesses reconciliation data")
    Component(reportRepo, "ReportRepository", "Repository", "Accesses reporting data")

    Component(kafkaProducer, "KafkaProducer", "Messaging", "Publishes reconciliation events")
    Component(kafkaConsumer, "KafkaConsumer", "Messaging", "Consumes trade events")
}

Container_Ext(ui, "Web UI", "Frontend")
ContainerDb_Ext(db, "PostgreSQL", "Database")
ContainerQueue_Ext(kafka, "Kafka", "Message Broker")

Rel(ui, tradeController, "HTTPS")
Rel(ui, reconController, "HTTPS")
Rel(ui, authController, "HTTPS")
Rel(ui, reportController, "HTTPS")

Rel(tradeController, tradeService, "Calls")
Rel(reconController, reconService, "Calls")
Rel(authController, methodSecurity, "Authenticates")
Rel(reportController, reportService, "Calls")

Rel(jwtFilter, methodSecurity, "Validates JWT")
Rel(methodSecurity, tradeService, "Authorizes")
Rel(methodSecurity, reconService, "Authorizes")

Rel(tradeService, tradeRepo, "Reads/Writes")
Rel(reconService, reconRepo, "Reads/Writes")
Rel(reportService, reportRepo, "Reads")

Rel(tradeService, kafkaProducer, "Publishes / Kafka")
Rel(kafkaConsumer, reconService, "Consumes / Kafka")

Rel(tradeRepo, db, "JDBC")
Rel(reconRepo, db, "JDBC")
Rel(reportRepo, db, "JDBC")

Rel(kafkaProducer, kafka, "Kafka")
Rel(kafka, kafkaConsumer, "Kafka")
```