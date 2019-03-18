# Генерация Excel файла с данными о пользователях
## Назначение Приложения

Приложение позволяет создавать Excel файл, в котором хранятся сгенерированные данные о клиентах (от 1 до 30) в следующем порядке:
Имя, фамилия, отчество, возраст, пол (М или Ж), дата рождения, ИНН, почтовый индекс, страна, область, город, улица, дом, квартира.
Данные о клиентах берутся одним из способов:
- Случайным образом через API (https://randomuser.me). Недостающие данные (ИНН, дом, квартира) генерируются случайным образом.
- При отсутствии доступа к API из базы данных (см Подготовительные Мероприятия)
- При нехватке данных в БД из зарнее заготовленных файлов формата .txt, которые находятся в папке `resources`. Даные (возраст, пол, дата рождения, ИНН, почтовый индекс, дом, квартира) берутся не из текстовых файлов, а формируются по заданным условиям.

## Подготовительные мероприятия
Для запуска проекта необходимо установить систему сборки программ Maven, Java SDK 1.8.

## Инструкция по запуску
1. Склонировать репозиторий по указанной [ссылке](https://github.com/Fatalistka07/HW3.git).
2. Создать файл `database.properties` в корне репозитория для подключения к БД (в MySQL) `clients` со следующими данными:
```console
url = <Путь_к_БД>
username = <имя_пользователя_БД>
password = <незашифрованный_пароль_пользователя>
```
Например:
```console
url = jdbc:mysql://localhost:3306/clients?serverTimezone=Europe/Moscow
username = user
password = qwerty
```
3. Для сборки и запуска программы необходимо в консоле, находясь в корне репозитория, запустить команды:
```console
mvn compile
mvn exec:java -Dexec.mainClass="com.company.Main"
```
В выводе программы можете увидеть путь к сгенерированному Excel файлу.
