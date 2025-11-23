# Project CRUD API

## Описание

CRUD операции для управления проектами с поддержкой пагинации и мультиязычности (EN, RU, KZ).

## Endpoints

### 1. Создать проект
**POST** `/api/projects`
- **Требуется авторизация**: Да (только ADMIN)
- **Request Body**:
```json
{
  "slug": "speech-recognition",
  "title": {
    "en": "Kazakh Speech Recognition",
    "ru": "Распознавание казахской речи",
    "kz": "Қазақ сөйлеуін тану"
  },
  "shortDescription": {
    "en": "Voice-to-text system for Kazakh language with high accuracy",
    "ru": "Система распознавания речи для казахского языка с высокой точностью",
    "kz": "Жоғары дәлдікпен қазақ тіліне арналған дауысты мәтінге айналдыру жүйесі"
  },
  "fullDescription": {
    "en": "Developing an advanced automatic speech recognition system...",
    "ru": "Разработка передовой системы автоматического распознавания речи...",
    "kz": "Қазақ тіліне арнайы жасалған озық автоматты сөйлеуді тану жүйесін әзірлеу..."
  },
  "image": "/projects/KZH.svg",
  "tags": ["Speech Recognition", "Audio Processing", "AI", "Acoustic Models"],
  "status": "active",
  "startDate": "2024-03-15",
  "endDate": null,
  "team": ["Dias Nurakhmet", "Nurlan Bekzhanov"],
  "objectives": {
    "en": [
      "Collect diverse speech data from Kazakh speakers",
      "Train acoustic and language models",
      "Achieve 90%+ word accuracy rate",
      "Support real-time transcription"
    ],
    "ru": [
      "Собрать разнообразные речевые данные от говорящих на казахском языке",
      "Обучить акустические и языковые модели",
      "Достичь точности распознавания слов 90%+",
      "Поддержка транскрипции в реальном времени"
    ],
    "kz": [
      "Қазақ тілінде сөйлейтіндерден әртүрлі сөйлеу деректерін жинау",
      "Акустикалық және тілдік модельдерді үйрету",
      "Сөздерді тану дәлдігінің 90%+ деңгейіне жету",
      "Нақты уақыттағы транскрипцияны қолдау"
    ]
  },
  "results": {
    "en": ["Result 1", "Result 2"],
    "ru": ["Результат 1", "Результат 2"],
    "kz": ["Нәтиже 1", "Нәтиже 2"]
  }
}
```

**Response** (HTTP 201):
```json
{
  "id": 1,
  "slug": "speech-recognition",
  "title": {
    "en": "Kazakh Speech Recognition",
    "ru": "Распознавание казахской речи",
    "kz": "Қазақ сөйлеуін тану"
  },
  "shortDescription": { "en": "...", "ru": "...", "kz": "..." },
  "fullDescription": { "en": "...", "ru": "...", "kz": "..." },
  "image": "/projects/KZH.svg",
  "tags": ["Speech Recognition", "Audio Processing", "AI", "Acoustic Models"],
  "status": "active",
  "startDate": "2024-03-15",
  "endDate": null,
  "team": ["Dias Nurakhmet", "Nurlan Bekzhanov"],
  "objectives": {
    "en": ["...", "..."],
    "ru": ["...", "..."],
    "kz": ["...", "..."]
  },
  "results": {
    "en": ["...", "..."],
    "ru": ["...", "..."],
    "kz": ["...", "..."]
  },
  "createdAt": "2025-11-23T10:00:00",
  "updatedAt": "2025-11-23T10:00:00"
}
```

### 2. Получить проект по ID
**GET** `/api/projects/{id}`
- **Требуется авторизация**: Нет
- **Response**: аналогичен ответу на создание

### 3. Получить проект по slug
**GET** `/api/projects/slug/{slug}`
- **Требуется авторизация**: Нет
- **Пример**: `/api/projects/slug/speech-recognition`
- **Response**: аналогичен ответу на создание

### 4. Получить все проекты (с пагинацией)
**GET** `/api/projects?page=0&size=10&sortBy=id&direction=ASC`
- **Требуется авторизация**: Нет
- **Query Parameters**:
  - `page` (default: 0) - номер страницы
  - `size` (default: 10) - размер страницы
  - `sortBy` (default: "id") - поле для сортировки
  - `direction` (default: "ASC") - направление сортировки (ASC/DESC)
- **Response**:
```json
{
  "content": [
    {
      "id": 1,
      "slug": "speech-recognition",
      "title": { "en": "...", "ru": "...", "kz": "..." },
      "shortDescription": { "en": "...", "ru": "...", "kz": "..." },
      "fullDescription": { "en": "...", "ru": "...", "kz": "..." },
      "image": "/projects/KZH.svg",
      "tags": ["..."],
      "status": "active",
      "startDate": "2024-03-15",
      "endDate": null,
      "team": ["..."],
      "objectives": { "en": ["..."], "ru": ["..."], "kz": ["..."] },
      "results": { "en": ["..."], "ru": ["..."], "kz": ["..."] },
      "createdAt": "2025-11-23T10:00:00",
      "updatedAt": "2025-11-23T10:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 50,
  "totalPages": 5,
  "last": false
}
```

### 5. Обновить проект
**PUT** `/api/projects/{id}`
- **Требуется авторизация**: Да (только ADMIN)
- **Request Body**: аналогичен POST
- **Response**: обновленный проект

### 6. Удалить проект
**DELETE** `/api/projects/{id}`
- **Требуется авторизация**: Да (только ADMIN)
- **Response**: HTTP 204 No Content

## Валидация

### Поле `slug`
- Обязательное поле
- Может содержать только строчные буквы, цифры и дефисы
- Должно быть уникальным

### Поле `status`
- Обязательное поле
- Допустимые значения: `active`, `completed`, `upcoming`

### Поле `team`
- Обязательное поле
- Массив строк (может содержать имена или ID)
- Минимум 1 элемент

### Поле `tags`
- Обязательное поле
- Массив строк
- Минимум 1 элемент

### Поле `objectives`
- Обязательное поле
- Требуется для всех трех языков (en, ru, kz)
- Каждый массив должен содержать минимум 1 элемент

### Поле `results`
- Необязательное поле
- Используется для завершенных проектов

## Структура базы данных

### Таблица `projects`:
- `id` - BIGSERIAL (Primary Key)
- `slug` - VARCHAR(255) UNIQUE
- `image` - VARCHAR(500)
- `tags` - TEXT (JSON array)
- `status` - VARCHAR(50)
- `start_date` - DATE
- `end_date` - DATE (nullable)
- `team` - TEXT (JSON array)
- `created_at` - TIMESTAMP
- `updated_at` - TIMESTAMP

### Таблица `translations`:
Хранит мультиязычные данные для:
- `TITLE` - заголовок проекта
- `SHORT_DESCRIPTION` - краткое описание
- `FULL_DESCRIPTION` - полное описание
- `OBJECTIVES` - цели проекта (JSON array)
- `RESULTS` - результаты проекта (JSON array, опционально)

Для каждого поля создаются записи для трех языков: EN, RU, KZ.

## Миграции

Файлы миграций:
- `src/main/resources/changelog/v1.0/06-create-table-projects.sql` - создание таблицы
- `src/main/resources/changelog/v1.0/07-insert-initial-project-kzh.sql` - пример данных

## Пример использования

### Создание проекта (cURL):
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "slug": "speech-recognition",
    "title": {
      "en": "Kazakh Speech Recognition",
      "ru": "Распознавание казахской речи",
      "kz": "Қазақ сөйлеуін тану"
    },
    "shortDescription": {
      "en": "Voice-to-text system...",
      "ru": "Система распознавания речи...",
      "kz": "Дауысты мәтінге айналдыру жүйесі..."
    },
    "fullDescription": {
      "en": "Developing an advanced...",
      "ru": "Разработка передовой...",
      "kz": "Озық жүйені әзірлеу..."
    },
    "image": "/projects/KZH.svg",
    "tags": ["Speech Recognition", "AI"],
    "status": "active",
    "startDate": "2024-03-15",
    "team": ["Dias Nurakhmet", "Nurlan Bekzhanov"],
    "objectives": {
      "en": ["Objective 1", "Objective 2"],
      "ru": ["Цель 1", "Цель 2"],
      "kz": ["Мақсат 1", "Мақсат 2"]
    }
  }'
```

### Получение проекта по slug (cURL):
```bash
curl http://localhost:8080/api/projects/slug/speech-recognition
```

## Swagger документация

После запуска приложения, полная интерактивная документация доступна по адресу:
`http://localhost:8080/swagger-ui.html`

## Запуск

1. Убедитесь, что база данных настроена
2. Запустите приложение - Liquibase автоматически создаст таблицы
3. API будет доступно на `/api/projects`

