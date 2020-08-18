# Пример работы с Google документами

## Сборка проекта

Выполнить:
```
./gradlew clean deploy
```

## Deploy
Задеплоить полученный артефакт на портал через: Панель управления - Приложения - Менеджер приложений.

## Создание учетных данных для работы с Google API

### Создать Google-приложение
Создать новый проект через [google консоль](https://console.developers.google.com/)

![Создать проект](docs/images/gsheet-create-project.png)

### Подключить Google Sheets API

Добавляем API для работы с Google таблицами:
![Google Sheets API](docs/images/gsheet-add-google-sheets-api.png)

### Создать учетные данные

Нужно добавить идентификатор клиента OAuth
![Создать учетные данные](docs/images/gsheet-create-client-credentials.png)

Создаем идентификатор клиента OAuth:
![Создать идентификатор клиента OAuth](docs/images/gsheet-create-client-credentials2.png)

Получаем идентификатор клиента и секрет:
![Идентификатор клиента OAuth](docs/images/gsheet-oauth-credentials.png)

## Настроить портлет

Зайти в конфигурацию портлета и настроить параметры:

- Идентификатор клиента: 792491835068-gdpgaombsqsbje3rmqkktvajm6slgme2.apps.googleusercontent.com
- Секрет: haUEOWPFsWYr1W4oZKa2SB-S
- Идентификатор таблицы: [1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms](https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms)
- Диапазон ячеек: A1:F

![Настройки портлета](docs/images/gsheet-portlet-settings.png)

