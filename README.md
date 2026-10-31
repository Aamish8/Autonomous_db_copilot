# DB Copilot

Ask questions about a database in plain English. The copilot finds the relevant tables
(RAG over the schema plus similar example queries), writes a read-only SQL query with a local
LLM, validates it, runs it, and explains the result.

Stack: Java 17, Spring Boot 4, Spring AI + Ollama, PostgreSQL + pgvector, plain HTML/JS frontend.

## Run it with Docker

1. Install Docker Desktop and Ollama, then pull the models once:
   ```
   ollama pull qwen2.5-coder:7b
   ollama pull nomic-embed-text
   ```
   (If Ollama listens only on localhost and the app cannot reach it, set the environment
   variable `OLLAMA_HOST=0.0.0.0` and restart Ollama.)
2. Stop the app in Eclipse if it is running (port 8080).
3. Copy `.env.example` to `.env` and set `DEMO_PASSWORD`, `COPILOT_CRYPTO_PASSWORD`, `COPILOT_CRYPTO_SALT`.
4. Start everything:
   ```
   docker compose up -d --build
   ```
   To run Ollama in Docker as well, set `OLLAMA_URL=http://ollama:11434` in `.env` and use
   `docker compose --profile ollama up -d --build`.
5. Open http://localhost:8080 and sign in as `demo` with your `DEMO_PASSWORD`.

On the first start the app registers a demo database (`sales_dw`), indexes its tables and loads
example queries. Watch `docker compose logs -f app` for "Demo database ready".

## Share it

With the app running, in another terminal:
```
cloudflared tunnel --url http://localhost:8080
```
It prints a public https link. Share the link and the `demo` password. The link only works while
your PC, Docker and the tunnel are running.

## How it works

question -> embed -> retrieve relevant tables and similar example queries (pgvector)
-> LLM writes SQL -> validate (single SELECT, blocked functions) -> run with a read-only
user and a 100-row cap -> on error, retry with the error message -> summarize the rows.

Saved database passwords are AES-encrypted. Chat history is stored per database.
