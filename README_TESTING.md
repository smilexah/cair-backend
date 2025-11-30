# 🧪 Быстрое тестирование CAIR Backend API

## 📋 Краткая инструкция

### 1️⃣ Запуск приложения

```bash
# Запуск всех сервисов (PostgreSQL, Redis, Backend)
docker-compose up -d

# Проверка статуса
docker-compose ps

# Просмотр логов
docker-compose logs -f backend
```

**Приложение будет доступно на:** `http://localhost:8080/api`

---

### 2️⃣ Swagger UI (Рекомендуется для быстрого тестирования)

1. Откройте в браузере: **http://localhost:8080/api/swagger-ui.html**

2. **Авторизация:**
   - Нажмите кнопку **Authorize** (замок в правом верхнем углу)
   - Сначала выполните Login: `/auth/login` → Try it out → Execute
   - Скопируйте `accessToken` из ответа
   - Вставьте в поле авторизации: `Bearer <accessToken>`
   - Нажмите **Authorize**

3. **Тестирование endpoints:**
   - Выберите endpoint
   - Нажмите **Try it out**
   - Заполните параметры (если нужно)
   - Нажмите **Execute**

---

### 3️⃣ Postman (Рекомендуется для автоматизированного тестирования)

1. **Импорт коллекции:**
   - Откройте Postman
   - File → Import → Выберите `CAIR_API.postman_collection.json`

2. **Настройка переменных:**
   - Нажмите на коллекцию → Variables
   - Проверьте `base_url`: `http://localhost:8080/api`

3. **Тестирование:**
   - Запустите `Authentication → Login` (токен сохранится автоматически)
   - Тестируйте любые endpoints (токен будет использоваться автоматически)

📖 **Полное руководство:** `POSTMAN_GUIDE.md`

---

### 4️⃣ cURL (Быстрое тестирование из командной строки)

#### Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

**Скопируйте `accessToken` из ответа**

#### Получить все проекты:
```bash
curl http://localhost:8080/api/projects?page=0&size=10
```

#### Получить проект по ID (требуется токен для некоторых endpoints):
```bash
TOKEN="your_access_token_here"
curl http://localhost:8080/api/projects/1 \
  -H "Authorization: Bearer $TOKEN"
```

#### Создать проект (требуется ADMIN роль):
```bash
TOKEN="your_access_token_here"
curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "slug": "test-project",
    "title": {
      "en": "Test Project",
      "ru": "Тестовый проект",
      "kz": "Тестілік жоба"
    },
    "shortDescription": {
      "en": "Test",
      "ru": "Тест",
      "kz": "Тест"
    },
    "fullDescription": {
      "en": "Test description",
      "ru": "Тестовое описание",
      "kz": "Тестілік сипаттама"
    },
    "image": "/projects/test.svg",
    "tags": ["test"],
    "status": "active",
    "startDate": "2024-01-15",
    "team": ["Admin"],
    "objectives": {
      "en": ["Test objective"],
      "ru": ["Тестовая цель"],
      "kz": ["Тестілік мақсат"]
    }
  }'
```

---

## 🔍 Проверка статуса

### Health Check:
```bash
curl http://localhost:8080/api/actuator/health
```

**Ожидаемый ответ:**
```json
{"status":"UP"}
```

### Проверка логов:
```bash
# Backend логи
docker-compose logs backend

# PostgreSQL логи
docker-compose logs postgres

# Redis логи
docker-compose logs redis

# Все логи вместе
docker-compose logs -f
```

---

## 🎯 Основные Endpoints

| Метод | Endpoint | Аутентификация | Описание |
|-------|----------|----------------|----------|
| POST | `/auth/login` | Нет | Вход в систему |
| POST | `/auth/refresh-token` | Bearer | Обновление токена |
| POST | `/auth/logout` | Bearer | Выход |
| GET | `/projects` | Нет | Все проекты (с пагинацией) |
| GET | `/projects/{id}` | Нет | Проект по ID |
| GET | `/projects/slug/{slug}` | Нет | Проект по slug |
| POST | `/projects` | Bearer (ADMIN) | Создать проект |
| PUT | `/projects/{id}` | Bearer (ADMIN) | Обновить проект |
| DELETE | `/projects/{id}` | Bearer (ADMIN) | Удалить проект |
| GET | `/team-members` | Нет | Все члены команды |
| GET | `/team-members/{id}` | Нет | Член команды по ID |
| POST | `/team-members` | Bearer (ADMIN) | Создать члена команды |
| PUT | `/team-members/{id}` | Bearer (ADMIN) | Обновить члена команды |
| DELETE | `/team-members/{id}` | Bearer (ADMIN) | Удалить члена команды |

---

## 🔑 Учетные данные по умолчанию

```
Username: admin
Password: admin
Role: ADMIN
```

> ⚠️ **Важно:** Измените пароль в production!

---

## 🐛 Troubleshooting

### Проблема: "Connection refused"
```bash
# Проверьте статус контейнеров
docker-compose ps

# Перезапустите сервисы
docker-compose restart
```

### Проблема: "Database connection failed"
```bash
# Проверьте PostgreSQL
docker-compose logs postgres

# Пересоздайте БД
docker-compose down -v
docker-compose up -d
```

### Проблема: "401 Unauthorized"
```bash
# Выполните login заново и обновите токен
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

### Проблема: "403 Forbidden"
- Используйте учетную запись с ролью ADMIN
- По умолчанию: `admin:admin`

---

## 📊 Тестирование производительности

### Apache Bench (ab):
```bash
# 1000 запросов, 10 одновременно
ab -n 1000 -c 10 http://localhost:8080/api/projects

# С авторизацией
ab -n 1000 -c 10 -H "Authorization: Bearer YOUR_TOKEN" \
   http://localhost:8080/api/projects
```

### wrk:
```bash
# 30 секунд, 10 соединений, 2 потока
wrk -t2 -c10 -d30s http://localhost:8080/api/projects
```

---

## 📚 Дополнительная документация

- 📖 **POSTMAN_GUIDE.md** - Полное руководство по Postman
- 🚀 **DEPLOYMENT.md** - Развертывание в production
- ⚡ **PERFORMANCE_OPTIMIZATIONS.md** - Описание оптимизаций
- 📋 **SUMMARY.md** - Сводка выполненных работ

---

## 🎉 Готово!

Теперь вы можете:
- ✅ Тестировать все API endpoints
- ✅ Использовать Swagger UI для интерактивного тестирования
- ✅ Импортировать Postman коллекцию для автоматизированного тестирования
- ✅ Развернуть приложение в production

**Приятного тестирования!** 🚀

