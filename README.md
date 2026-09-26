# Parcel Locker System

## Команда:
- Беляева Анна Павловна
- Волков Дмитрий Алексеевич
- Кайзер Даниил Дмитриевич
- Славгородский Сергей Романович

## Инструкция запуска:
Создать базу данных и заполнить тестовыми данными:
```bash
psql -U postgres -c "CREATE DATABASE postamat_db;"
psql -U postgres -d postamat_db -f db/schema.sql
```
Запустить файл Main.java