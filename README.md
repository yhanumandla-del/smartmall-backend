# SmartMall Backend

REST API for **SmartMall**, a digital shopping mall platform where customers browse stores, products and offers, sellers manage their own shops, and administrators oversee the platform.

The React frontend lives in a separate repository: `smartmall-frontend`.

## Technology stack
- Java 17, Spring Boot 3.5
- Spring Data JPA / Hibernate, MySQL 8+
- Spring Security with JWT authentication
- REST / JSON

## Run locally

1. Start MySQL and create the database (or let the app create it automatically):
   ```sql
   CREATE DATABASE IF NOT EXISTS smartmall;
   ```
   The same statement is in `create-database.sql`.
2. If your MySQL credentials differ from `root` / `root`, set the environment variables below (or edit `src/main/resources/application.properties`).
3. Start the API:
   ```bash
   ./mvnw spring-boot:run
   ```
   On Windows you can run `mvnw.cmd spring-boot:run` or double-click `START_BACKEND.cmd`.

The API runs at `http://localhost:8080`. On startup, sample categories, stores, products, offers and demo accounts are created.

## Demo accounts
- **Admin:** `admin@smartmall.com` / `admin123`
- **Store owner:** `owner@smartmall.com` / `owner123`
- **Customer:** `customer@smartmall.com` / `customer123`

## Environment variables

| Variable | Purpose | Default |
|---|---|---|
| `PORT` | Server port | `8080` |
| `DB_URL` | JDBC URL of the MySQL database | local `smartmall` database |
| `DB_USER` | Database username | `root` |
| `DB_PASSWORD` | Database password | `root` |
| `JWT_SECRET` | Secret used to sign JWT tokens | demo value, **replace in production** |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins allowed to call the API | localhost, `*.vercel.app`, the existing Netlify site |

See `.env.example` for a template.

## Deployment
A `Dockerfile` is included (multi-stage Maven build, Java 17 runtime). On Railway, Render or any Docker host:

1. Connect this repository.
2. Set `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` and `CORS_ALLOWED_ORIGINS` (add your deployed frontend URL).
3. Deploy. The service listens on the `PORT` the host provides.

After deploying, give the public API URL (ending in `/api`) to the frontend as `VITE_API_URL`.
