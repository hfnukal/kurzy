# Kurzy

Aplikace načte kurzy z ČNB a zobrazí je v grafu pro vybraný rok.

## Instalace

### Prerekvizity

    - Java
    - Gradle
    - npm
    - Node.js

### Sestavení

#### Frontend Vue

```
cd src/main/vue/kurzy
npm i

# Vytvoří build v src/main/resources/static
npm run build

# Spustí dev mode, s proxy na localhost:8080
npm run dev

```

#### Backend

Pokud je přdem zbuildovaný frontend, je dostupný na http://localhost:8080

```
./gradlew bootRun
```
