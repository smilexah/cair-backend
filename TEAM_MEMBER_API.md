# Team Member CRUD API

## Описание

CRUD операции для управления членами команды с поддержкой пагинации и мультиязычности (EN, RU, KZ).

## Endpoints

### 1. Создать члена команды
**POST** `/api/team-members`
- **Требуется авторизация**: Да (только ADMIN)
- **Request Body**:
```json
{
  "name": "John Doe",
  "role": {
    "en": "Senior Researcher",
    "ru": "Старший научный сотрудник",
    "kz": "Аға ғылыми қызметкер"
  },
  "bio": {
    "en": "Expert in AI and Machine Learning",
    "ru": "Эксперт в области ИИ и машинного обучения",
    "kz": "AI және машиналық оқыту саласындағы сарапшы"
  },
  "image": "https://example.com/photo.jpg",
  "expertise": ["Machine Learning", "Deep Learning", "NLP"],
  "email": "john.doe@example.com",
  "linkedin": "https://linkedin.com/in/johndoe",
  "github": "https://github.com/johndoe",
  "scholar": "https://scholar.google.com/citations?user=xxx"
}
```

### 2. Получить члена команды по ID
**GET** `/api/team-members/{id}`
- **Требуется авторизация**: Нет
- **Response**:
```json
{
  "id": 1,
  "name": "John Doe",
  "role": {
    "en": "Senior Researcher",
    "ru": "Старший научный сотрудник",
    "kz": "Аға ғылыми қызметкер"
  },
  "bio": {
    "en": "Expert in AI and Machine Learning",
    "ru": "Эксперт в области ИИ и машинного обучения",
    "kz": "AI және машиналық оқыту саласындағы сарапшы"
  },
  "image": "https://example.com/photo.jpg",
  "expertise": ["Machine Learning", "Deep Learning", "NLP"],
  "email": "john.doe@example.com",
  "linkedin": "https://linkedin.com/in/johndoe",
  "github": "https://github.com/johndoe",
  "scholar": "https://scholar.google.com/citations?user=xxx",
  "createdAt": "2025-11-23T10:00:00",
  "updatedAt": "2025-11-23T10:00:00"
}
```

### 3. Получить всех членов команды (с пагинацией)
**GET** `/api/team-members?page=0&size=10&sortBy=id&direction=ASC`
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
      "name": "John Doe",
      "role": { "en": "...", "ru": "...", "kz": "..." },
      "bio": { "en": "...", "ru": "...", "kz": "..." },
      "image": "...",
      "expertise": ["..."],
      "email": "...",
      "linkedin": "...",
      "github": "...",
      "scholar": "...",
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

### 4. Обновить члена команды
**PUT** `/api/team-members/{id}`
- **Требуется авторизация**: Да (только ADMIN)
- **Request Body**: аналогичен POST

### 5. Удалить члена команды
**DELETE** `/api/team-members/{id}`
- **Требуется авторизация**: Да (только ADMIN)
- **Response**: HTTP 204 No Content

## Структура базы данных

Таблица `team_members`:
- `id` - BIGSERIAL (Primary Key)
- `name` - VARCHAR(255)
- `role_en`, `role_ru`, `role_kz` - VARCHAR(255)
- `bio_en`, `bio_ru`, `bio_kz` - TEXT
- `image` - VARCHAR(500) (optional)
- `expertise` - TEXT (JSON array)
- `email`, `linkedin`, `github`, `scholar` - VARCHAR(255) (optional)
- `created_at`, `updated_at` - TIMESTAMP

## Миграции

Файл миграции: `src/main/resources/changelog/v1.0/04-create-table-team-members.sql`

## Запуск

1. Убедитесь, что база данных настроена
2. Запустите приложение - Liquibase автоматически создаст таблицу
3. API будет доступно на `/api/team-members`
4. Swagger документация: `/swagger-ui.html`

