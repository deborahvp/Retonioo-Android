# 🌱 Retoño — App Android

Cliente Android de **Retoño**, una plataforma de **suscripción de ropa infantil circular**.

Los niños usan la ropa unas pocas veces y les deja de quedar por talla. En vez de tirarla, se devuelve; si está en buen estado, vuelve al catálogo para otra familia. El usuario paga una suscripción y maneja un **"batch"** (un conjunto de prendas a la vez): devuelve su batch actual y lo cambia por uno nuevo.

---

## ✨ Funcionalidad

- **Autenticación** — registro e inicio de sesión; la sesión persiste entre aperturas de la app.
- **Catálogo** — grid de prendas con imágenes, filtros por categoría y talla.
- **Detalle de prenda** — ficha completa + **línea de tiempo de la máquina de estados** (historial de la prenda).
- **Ciclo de Armario (batch)** — agrega/quita prendas, contador `X / N` según tu plan, y botón para **devolver el batch** (las prendas pasan a limpieza y vuelven al catálogo).
- **Wishlist** — guarda prendas y muévelas al batch cuando quieras.
- **Suscripción** — 3 planes (Pequeño 5 / Mediano 10 / Grande 15 prendas) que definen el tamaño del batch.

La máquina de estados de cada prenda sigue el ciclo:

```
available → reserved → rented → in_cleaning → available   (y retired)
```

---

## 🧱 Stack

| Capa | Tecnología |
|------|------------|
| Lenguaje | Kotlin |
| UI | Jetpack Compose · Material 3 |
| Arquitectura | MVVM + Repository |
| Red | Retrofit 2 · OkHttp · Gson |
| Imágenes | Coil |
| Navegación | Navigation Compose |
| Estado/async | StateFlow · Coroutines |
| Persistencia local | DataStore Preferences (token de sesión + plan) |
| Backend | Node.js / Express + Supabase (en la carpeta `backend/`) |

**Requisitos:** Android Studio reciente · JDK 11 · `minSdk 24` · `targetSdk 36`.

---

## 🏗️ Arquitectura

```
com.example.retonioandroid
├── data
│   ├── remote/        Retrofit (ApiConfig, RetonioApi, RetrofitClient, interceptor, DTOs)
│   ├── local/         SessionStore (DataStore: token + plan)
│   └── repository/    Auth, Garment, Cart, Wishlist
├── domain/model/      Modelos limpios + enums (GarmentStatus, SubscriptionPlan…)
├── di/                Graph (contenedor de dependencias manual)
└── ui
    ├── theme/         Paleta neominimalista + tipografía
    ├── navigation/    NavHost + barra inferior
    ├── components/    Reutilizables (GarmentCard, StatusChip, estados…)
    ├── auth/          Login + Registro
    ├── catalog/       Catálogo
    ├── detail/        Detalle + timeline de estados
    ├── wardrobe/      Batch + Wishlist
    ├── subscription/  Selección de plan
    └── profile/       Perfil
```

Cada pantalla expone su estado con un `StateFlow<UiState>` desde su `ViewModel`, y los repositorios devuelven `Result<T>` con mensajes de error legibles. Un interceptor de OkHttp adjunta el header `Authorization: Bearer` leyendo el token del `SessionStore`.

---

## 🚀 Cómo correrlo

La app **consume el backend**, así que necesitas **dos cosas corriendo a la vez**: el servidor y la app en el emulador.

### 1. Backend

```bash
cd backend
npm install            # solo la primera vez
# crea backend/.env a partir de backend/.env.example y rellena tus credenciales de Supabase
node src/server.js
```

Debe imprimir `🌱 Retoño backend corriendo en http://localhost:3000`. Verifícalo con:

```bash
curl http://localhost:3000/health
```

> La base de datos se prepara con los scripts de `supabase/`: ejecuta `schema.sql` y luego `seed.sql` en el **SQL Editor** de tu proyecto Supabase.

### 2. App Android

1. Abre la carpeta del proyecto en **Android Studio** y espera el *Gradle sync*.
2. Inicia un **emulador** (Device Manager).
3. **Run ▶**.

#### Configuración de red (importante)

- El emulador ve el `localhost` de tu PC como **`10.0.2.2`**, por eso la URL base es `http://10.0.2.2:3000/` (definida en `data/remote/ApiConfig.kt`).
- Como en desarrollo se usa HTTP, se habilita *cleartext* **solo** para ese host vía `res/xml/network_security_config.xml`.
- Para un **dispositivo físico**, cambia la base URL en `ApiConfig.kt` por la IP de tu PC en la red local (p. ej. `http://192.168.1.X:3000/`).

> Si la ruta del proyecto contiene caracteres no-ASCII (acentos), el `gradle.properties` incluye `android.overridePathCheck=true` para permitir el build en Windows.

---

## 📡 API que consume

Base: `http://10.0.2.2:3000`

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/auth/register` | — | Registro |
| POST | `/auth/login` | — | Login (devuelve `access_token`) |
| GET | `/garments` | — | Catálogo (filtros `category`, `size`, `status`) |
| GET | `/garments/{id}` | — | Detalle de una prenda |
| GET | `/garments/{id}/states` | — | Historial de estados |
| GET | `/cart` | Bearer | Batch actual |
| POST | `/cart` | Bearer | Agregar al batch |
| DELETE | `/cart/{garment_id}` | Bearer | Quitar del batch |
| POST | `/cart/return` | Bearer | Devolver batch (→ limpieza) |
| GET / POST / DELETE | `/wishlist` | Bearer | Lista de deseos |

---

## 🎨 Diseño

Neominimalismo: superficies limpias, mucho espacio en blanco, jerarquía tipográfica clara, esquinas suaves y color semántico por estado de prenda. Paleta inspirada en "Retoño" (brote/naturaleza), con verde `#3A5A40` como acento sobre fondo hueso `#FAF9F6`.

---

## 📁 Estructura del repositorio

```
RetonioAndroid/
├── app/          App Android (este cliente)
├── backend/      Servidor Node/Express (Supabase)
├── supabase/     schema.sql, seed.sql, update_images.sql
└── docs/         Documentación
```
