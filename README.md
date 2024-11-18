# kafka


```shell

# Создание топика:
docker exec -it kafka kafka-topics --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Просмотр списка топиков:
docker exec -it kafka kafka-topics --list --bootstrap-server localhost:9092

# Отправка сообщения:
docker exec -it kafka kafka-console-producer --topic test-topic --bootstrap-server localhost:9092

# Получение сообщения:
docker exec -it kafka kafka-console-consumer --topic test-topic --from-beginning --bootstrap-server localhost:9092

# Просмотр логов Kafdrop
docker-compose logs kafdrop

# Проверка перед запуском докеров, если падает докер
lsof -i :9000

# Проверка доступа к Kafka
docker-compose exec kafka bash

# Список доступных топиков
kafka-topics --bootstrap-server localhost:9092 --list

# Проверкад доступа и список топиков 
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# Просмотр логов Kafka
docker-compose logs kafka


```

https://softwaremill.com/kafka-visualisation/