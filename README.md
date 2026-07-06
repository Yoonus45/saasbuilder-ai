# SaaSBuilder AI

SaaSBuilder AI is a full-stack SaaS starter with a Spring Boot backend and a React + TypeScript frontend.

## AI generation setup

Real AI code generation is wired through the backend OpenRouter integration.

### 1. Configure environment variables

Set the following environment variables before starting the backend:

```bash
export OPENROUTER_API_KEY=your_openrouter_api_key
export OPENROUTER_MODEL=deepseek/deepseek-chat-v3-0324:free
export OPENROUTER_ENDPOINT=https://openrouter.ai/api/v1/chat/completions
export OPENROUTER_TIMEOUT_SECONDS=30
```

On Windows PowerShell:

```powershell
$env:OPENROUTER_API_KEY="your_openrouter_api_key"
$env:OPENROUTER_MODEL="deepseek/deepseek-chat-v3-0324:free"
```

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

### 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

### 4. Use the feature

Create a new project from the UI. The app will send the title, description, prompt, and framework to the AI service, store the generated output in the project and AI history, and show the generated code on the project page.

### Notes

- If the API key is missing or the provider fails, the backend records the request as failed and returns an explanatory message instead of crashing.
- The app uses environment-based configuration so secrets stay out of source control.
