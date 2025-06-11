# MCP Server Migration Demo

**Overview**

This sample project demonstrates how to migrate existing Java Spring Boot REST APIs to a **Model Context Protocol (MCP)** setup. We’ll build:

* **3 REST endpoints** (legacy API):

  1. `GET /cards` – Returns a static list of credit cards
  2. `GET /cards/{id}` – Returns details for one card
  3. `POST /applications` – Validates and fakes saving a credit application

* **4 MCP functions** (new agent-exposed):

  1. `getCards` – Static list of cards (calls legacy endpoint)
  2. `getCard` – Single card lookup (calls legacy endpoint)
  3. `submitApplication` – Validates and saves application (calls legacy endpoint)
  4. `getBonuses` – Returns card names + bonus only (new capability)

**Purpose**

Use this demo to show how an AI Agent can call MCP functions backed by your Spring Boot server. The agent can chain calls, handle user queries, and demonstrate the migration path to MCP.

---

## Setup & Running

```bash
# Clone project
git clone https://github.com/your-org/mcp-server-demo.git
cd mcp-server-demo

# Build & run\mvn spring-boot:run
```

API will be available on `http://localhost:8080`.

---

### 1. Define REST Controllers

**Prompt:**

```
You are a Spring Boot expert. Create a `CardController` with three endpoints:
- GET /cards returns List<Card>
- GET /cards/{id} returns a single Card by ID
- POST /applications accepts JSON {name, surname, salary, birthday}, performs basic validation, and returns a 200 OK with a success message. Use `@RestController` and in-memory static data.
```

### 2. Create Card Model & Sample Data

**Prompt:**

```
Generate a `Card` Java class with fields: id, bankName, cardName, yearlyFee, bonus, interestRate. Then create a `CardRepository` or `CardService` class that holds a static list of 3 cards.
```

### 3. Implement Legacy Endpoints

**Prompt:**

```
In `CardController`, inject `CardService` and implement:
- `List<Card> getCards()` calls `cardService.findAll()`
- `Card getCard(Long id)` calls `cardService.findById(id)`
- `ResponseEntity<String> submitApplication(ApplicationRequest req)` validates salary > 0 and birth date not null, then returns success.
```

### 4. Scaffold MCP Function Definitions

**Prompt:**

```
Add an `McpConfig` class. Define MCP function metadata for 4 functions:
- getCards()
- getCard(id: Long)
- submitApplication(name, surname, salary, birthday)
- getBonuses()
Use JSON or annotations so the agent can discover tools.
```

### 5. Implement `getCards` MCP Function

**Prompt:**

```
Implement an MCP endpoint or handler for `getCards` that internally calls your legacy `GET /cards` endpoint (RestTemplate or WebClient) and returns the JSON list to the caller.
```

### 6. Implement `getCard` MCP Function

**Prompt:**

```
Implement `getCard(id)` in MCP: call `GET /cards/{id}` under the hood, parse the result, and forward to the agent.
```

### 7. Implement `submitApplication` MCP Function

**Prompt:**

```
Add MCP handler for `submitApplication` that POSTs to `/applications` with the incoming payload. Return the response status and message to the agent.
```

### 8. Implement `getBonuses` MCP Function

**Prompt:**

```
Create `getBonuses` MCP function that calls existing `getCards`, filters each card to only name and bonus, and returns a simplified list [{cardName, bonus}].
```

### 9. Register MCP Functions with Agent

**Prompt:**

```
In your agent bootstrap (e.g., `AgentConfig`), register each MCP function name, description, and parameter schema so the LLM can call them. Use the OpenAI function-calling format or your own MCP spec.
```

### 10. Test Agent Workflow

**Prompt:**

```
Write a test scenario for the AI Agent: user asks "Which card has the highest bonus?" Verify the agent calls `getBonuses`, sorts by bonus, and returns the top result.
```

### 11. Write a README Section: Usage Examples

**Prompt:**

```
Add a README section showing example user messages and expected agent replies:
- "Show me all cards"
- "Tell me about card with ID 2"
- "Submit application for John Doe, 50000 salary, born 1990-05-15"
- "Which card has the highest bonus?"
```

### 12. Document Migration Notes

**Prompt:**

```
Write a brief section explaining how the MCP functions wrap existing REST endpoints, and how new functions (like getBonuses) can be added without touching legacy code.
```

---

## Next Steps

* Extend agent logic to handle errors and invalid input.
* Add more MCP functions (e.g., cancelApplication, getRewards).
* Integrate with a real bot UI or CLI.

---

*End of README*
