
# Movies
### Простой проект, представляющий из себя базу с фильмами, актерами, режиссерами.
### Проект покрыт Unit и Integration тестами.
### Для авторизации используется Spring Security
### Структура базы описана и реализована с Liquibase


# Развертывание проекта

### Поднять Postgres
```shell
docker run --name movies-db -p 5434:5432 -e POSTGRES_USER=movies -e POSTGRES_PASSWORD=movies postgres:14
```



