# Руководство по развертыванию CAIR Backend

## Оглавление
1. [CI/CD Pipeline](#cicd-pipeline)
2. [Docker Deployment](#docker-deployment)
3. [Cloudflare Integration](#cloudflare-integration)
4. [Production Deployment](#production-deployment)
5. [Monitoring & Health Checks](#monitoring--health-checks)

---

## CI/CD Pipeline

### GitHub Actions Workflows

#### 1. CI/CD Pipeline (`ci-cd.yml`)
Автоматически запускается при push и pull request в ветки `main` и `develop`.

**Этапы:**
- ✅ **Test**: Запуск юнит-тестов с PostgreSQL и Redis
- 🏗️ **Build**: Сборка приложения с Gradle
- 🐳 **Docker**: Создание и публикация Docker образа в GitHub Container Registry
- 🔍 **Code Quality**: Анализ качества кода
- 🔒 **Security Scan**: Сканирование на уязвимости с Trivy

#### 2. Production Deployment (`deploy-production.yml`)
Ручной запуск для деплоя в production или staging.

```bash
# Через GitHub UI: Actions → Deploy to Production → Run workflow
```

#### 3. Dependency Updates (`dependency-update.yml`)
Автоматическая проверка обновлений зависимостей каждый понедельник.

### Настройка GitHub Secrets

Добавьте следующие секреты в настройках репозитория:

```
SERVER_HOST        # IP адрес сервера
SERVER_USER        # SSH пользователь
SSH_PRIVATE_KEY    # SSH ключ для доступа
APP_URL           # URL приложения (например, https://api.cair.sdu.edu.kz)
```

---

## Docker Deployment

### Локальная разработка

```bash
# Запуск всех сервисов (PostgreSQL, Redis, Backend)
docker-compose up -d

# Просмотр логов
docker-compose logs -f backend

# Остановка
docker-compose down
```

### Production с Docker Compose

```bash
# Создайте .env файл с production переменными
cp .env.example .env

# Отредактируйте .env с production значениями
nano .env

# Запуск в production режиме
SPRING_PROFILES_ACTIVE=prod docker-compose up -d

# Проверка здоровья
curl http://localhost:8080/api/actuator/health
```

### Ручная сборка Docker образа

```bash
# Сборка образа
docker build -t cair-backend:latest .

# Запуск контейнера
docker run -d \
  --name cair-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e POSTGRES_ADDRESS=postgres \
  -e POSTGRES_ADDRESS_PORT=5432 \
  -e POSTGRES_DB_NAME=cair-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=secret \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  cair-backend:latest
```

---

## Cloudflare Integration

### Вариант 1: Cloudflare Tunnel (Рекомендуется)

Cloudflare Tunnel обеспечивает безопасное подключение без открытия портов.

```bash
# Установка cloudflared
wget https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb
sudo dpkg -i cloudflared-linux-amd64.deb

# Аутентификация
cloudflared tunnel login

# Создание туннеля
cloudflared tunnel create cair-backend

# Конфигурация туннеля
cat > ~/.cloudflared/config.yml <<EOF
tunnel: YOUR_TUNNEL_ID
credentials-file: /root/.cloudflared/YOUR_TUNNEL_ID.json

ingress:
  - hostname: api.cair.sdu.edu.kz
    service: http://localhost:8080
  - service: http_status:404
EOF

# Запуск туннеля
cloudflared tunnel run cair-backend

# Или как systemd сервис
cloudflared service install
systemctl enable cloudflared
systemctl start cloudflared
```

### Вариант 2: Cloudflare как CDN/Reverse Proxy

1. Добавьте домен в Cloudflare
2. Создайте A-запись, указывающую на IP сервера
3. Включите Cloudflare Proxy (оранжевое облако)
4. Настройте SSL/TLS:
   - SSL/TLS → Overview → Full (strict)
   - SSL/TLS → Edge Certificates → Always Use HTTPS: On

### Вариант 3: Cloudflare Workers (API Gateway)

Для проксирования запросов или добавления edge логики:

```javascript
// workers/api-gateway.js
export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    
    // Проксирование к backend
    const backendUrl = 'https://your-backend-server.com' + url.pathname;
    
    // Добавление CORS headers
    const response = await fetch(backendUrl, {
      method: request.method,
      headers: request.headers,
      body: request.body
    });
    
    const newResponse = new Response(response.body, response);
    newResponse.headers.set('Access-Control-Allow-Origin', '*');
    
    return newResponse;
  }
};
```

Деплой Worker:
```bash
npm install -g wrangler
wrangler login
wrangler publish
```

---

## Production Deployment

### Требования к серверу

- **OS**: Ubuntu 22.04 LTS или новее
- **RAM**: Минимум 4GB (рекомендуется 8GB+)
- **CPU**: 2+ ядра
- **Disk**: 50GB+ SSD
- **Software**: Docker, Docker Compose, Nginx (опционально)

### Шаг 1: Подготовка сервера

```bash
# Обновление системы
sudo apt update && sudo apt upgrade -y

# Установка Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Установка Docker Compose
sudo apt install docker-compose-plugin -y

# Добавление пользователя в группу docker
sudo usermod -aG docker $USER
```

### Шаг 2: Клонирование репозитория

```bash
cd /opt
sudo git clone https://github.com/your-org/cair-backend.git
cd cair-backend
```

### Шаг 3: Конфигурация

```bash
# Создание .env файла
sudo nano .env

# Добавьте production значения:
SPRING_PROFILES_ACTIVE=prod
POSTGRES_ADDRESS=postgres
POSTGRES_ADDRESS_PORT=5432
POSTGRES_DB_NAME=cair-db
POSTGRES_USER=cair_user
POSTGRES_PASSWORD=STRONG_PASSWORD_HERE
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_DATABASE=0
REDIS_TIMEOUT=60000
ADMIN_USERNAME=admin
ADMIN_PASSWORD=STRONG_ADMIN_PASSWORD
```

### Шаг 4: Запуск приложения

```bash
# Запуск сервисов
sudo docker-compose up -d

# Проверка логов
sudo docker-compose logs -f backend

# Проверка здоровья
curl http://localhost:8080/api/actuator/health
```

### Шаг 5: Настройка Nginx (опционально)

```bash
# Установка Nginx
sudo apt install nginx -y

# Конфигурация
sudo nano /etc/nginx/sites-available/cair-backend

# Добавьте конфигурацию:
server {
    listen 80;
    server_name api.cair.sdu.edu.kz;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# Активация конфигурации
sudo ln -s /etc/nginx/sites-available/cair-backend /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### Шаг 6: SSL сертификат (Let's Encrypt)

```bash
# Установка Certbot
sudo apt install certbot python3-certbot-nginx -y

# Получение сертификата
sudo certbot --nginx -d api.cair.sdu.edu.kz

# Автоматическое обновление
sudo systemctl enable certbot.timer
```

---

## Monitoring & Health Checks

### Health Check Endpoint

```bash
# Проверка здоровья приложения
curl http://localhost:8080/api/actuator/health

# Ожидаемый ответ:
{
  "status": "UP"
}
```

### Swagger UI

Документация API доступна по адресу:
```
http://localhost:8080/api/swagger-ui.html
```

### Логи

```bash
# Docker logs
docker-compose logs -f backend

# Последние 100 строк
docker-compose logs --tail=100 backend

# Логи PostgreSQL
docker-compose logs postgres

# Логи Redis
docker-compose logs redis
```

### Мониторинг ресурсов

```bash
# Использование ресурсов контейнерами
docker stats

# Использование диска
df -h

# Использование памяти
free -h
```

### Backup базы данных

```bash
# Создание backup
docker exec cair-postgres pg_dump -U postgres cair-db > backup_$(date +%Y%m%d_%H%M%S).sql

# Восстановление backup
docker exec -i cair-postgres psql -U postgres cair-db < backup.sql
```

---

## Troubleshooting

### Проблема: Приложение не запускается

```bash
# Проверка логов
docker-compose logs backend

# Проверка переменных окружения
docker-compose config

# Пересоздание контейнеров
docker-compose down
docker-compose up -d --force-recreate
```

### Проблема: База данных недоступна

```bash
# Проверка статуса PostgreSQL
docker-compose ps postgres

# Проверка логов PostgreSQL
docker-compose logs postgres

# Проверка подключения
docker exec cair-postgres psql -U postgres -c "SELECT 1"
```

### Проблема: Redis недоступен

```bash
# Проверка статуса Redis
docker-compose ps redis

# Проверка подключения
docker exec cair-redis redis-cli ping
```

---

## Best Practices

1. **Безопасность:**
   - Используйте сильные пароли
   - Регулярно обновляйте зависимости
   - Ограничьте доступ к портам (firewall)
   - Используйте HTTPS в production

2. **Мониторинг:**
   - Настройте алерты для критических ошибок
   - Мониторьте использование ресурсов
   - Регулярно проверяйте логи

3. **Backup:**
   - Автоматизируйте backup базы данных
   - Храните backups в безопасном месте
   - Регулярно тестируйте восстановление

4. **Обновления:**
   - Тестируйте обновления в staging окружении
   - Делайте backup перед обновлением
   - Следите за release notes зависимостей

---

## Дополнительные ресурсы

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [Cloudflare Tunnel](https://developers.cloudflare.com/cloudflare-one/connections/connect-apps/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/documentation)

