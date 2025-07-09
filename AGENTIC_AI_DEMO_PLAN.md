# Agentic AI Customer Care Demo - Work Plan

## 🎯 Project Overview

Create a customer care chatbot that proactively uses AI tools to help users with credit card inquiries. The AI agent will:
- Greet users and understand their needs
- Use available tools to fetch card information, applications, and bonuses
- Make intelligent decisions about what information to gather
- Proactively suggest relevant questions/actions based on context
- Maintain conversation flow with contextual awareness

## 🏗️ Architecture

```
User Input → Web Chat Interface → AI Agent Controller → OpenAI GPT-4 → Tool Selection → MCP Tools → Data Services
```

### Core Components:
1. **AI Agent Service** - OpenAI integration with function calling
2. **Enhanced Web Interface** - Real-time chat with the agent
3. **Extended MCP Tools** - Additional customer service tools
4. **Demo Data Layer** - In-memory session data for applications/users
5. **Conversation Context** - Session management for multi-turn conversations

## 📋 Detailed Implementation Plan

### Phase 1: Foundation Setup (2-3 hours)

#### 1.1 OpenAI Integration Service
**Files to create:**
- `src/main/java/fin/kk/mcp/service/OpenAiService.java`
- `src/main/java/fin/kk/mcp/config/OpenAiConfig.java`

**Implementation:**
```java
@Service
public class OpenAiService {
    // OpenAI client setup
    // Function calling configuration
    // Message history management
    // Tool execution coordination
}
```

**Features:**
- GPT-4 integration with function calling
- Conversation context management
- Tool result processing
- Error handling and fallbacks

#### 1.2 AI Agent Controller
**Files to create:**
- `src/main/java/fin/kk/mcp/controller/AgentController.java`
- `src/main/java/fin/kk/mcp/dto/ChatMessage.java`
- `src/main/java/fin/kk/mcp/dto/ChatResponse.java`

**Endpoints:**
- `POST /api/agent/chat` - Main chat endpoint
- `GET /api/agent/conversation/{sessionId}` - Get conversation history
- `DELETE /api/agent/conversation/{sessionId}` - Clear conversation

### Phase 2: Enhanced MCP Tools (1-2 hours)

#### 2.1 Customer Application Tools
**Extend `McpCardService.java` with:**

```java
@Tool(name = "getCustomerApplications", description = "Get all applications for a customer by name")
public List<ApplicationStatus> getCustomerApplications(String customerName);

@Tool(name = "getApplicationStatus", description = "Get detailed status of a specific application")
public ApplicationDetails getApplicationStatus(String applicationId);

@Tool(name = "cancelApplication", description = "Cancel a pending application")
public CancellationResult cancelApplication(String applicationId);

@Tool(name = "estimateApprovalTime", description = "Estimate approval time for an application")
public ApprovalEstimate estimateApprovalTime(String applicationId);
```

#### 2.2 Demo Data Service
**Files to create:**
- `src/main/java/fin/kk/mcp/service/DemoDataService.java`
- `src/main/java/fin/kk/mcp/model/ApplicationStatus.java`
- `src/main/java/fin/kk/mcp/model/CustomerProfile.java`

**Features:**
- In-memory customer profiles
- Mock application statuses
- Realistic demo scenarios
- Session-based data persistence

### Phase 3: Intelligent Agent Logic (2-3 hours)

#### 3.1 Agent Prompt Engineering
**System Prompt Design:**
```
You are a helpful customer service agent for KK Credit Cards. Your goal is to:
1. Greet customers warmly and understand their needs
2. Use available tools to gather relevant information
3. Proactively suggest helpful actions based on context
4. Provide clear, actionable responses

Available Tools: [Tool descriptions will be auto-generated]

Conversation Flow:
- Always start with a friendly greeting
- Ask clarifying questions when needed
- Use tools to fetch relevant data
- Anticipate follow-up questions
- Offer proactive suggestions
```

#### 3.2 Context-Aware Tool Selection
**Intelligence Features:**
- Analyze user intent from messages
- Chain tool calls intelligently
- Remember conversation context
- Suggest relevant follow-up actions
- Handle edge cases gracefully

### Phase 4: Enhanced Web Interface (1-2 hours)

#### 4.1 Improved Chat UI
**Update `src/main/resources/templates/index.html`:**
- Real-time typing indicators
- Message timestamps
- Tool execution status
- Conversation history
- Session management
- Mobile-responsive design

#### 4.2 Demo Scenarios Panel
**Add to UI:**
- Quick demo scenarios buttons
- Sample customer profiles
- Pre-filled conversation starters
- Reset/clear conversation option

### Phase 5: Demo Scenarios & Data (1 hour)

#### 5.1 Sample Customer Profiles
```java
// Demo customers with realistic data
Customer johnDoe = new Customer("John Doe", "john@email.com", 
    List.of(pendingApplication, approvedApplication));
Customer janeSmith = new Customer("Jane Smith", "jane@email.com", 
    List.of(oldApplication));
```

#### 5.2 Realistic Scenarios
1. **New Customer Inquiry**: "I want information about credit cards"
2. **Application Status Check**: "What's the status of my application?"
3. **Card Comparison**: "Which card has the best bonus?"
4. **Application Issues**: "I want to cancel my application"
5. **Follow-up Questions**: "When will I receive my card?"

## 🛠️ Technical Implementation Details

### Dependencies to Add
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

### Configuration Updates
```properties
# Add to application.properties
openai.api-key=${OPENAI_API_KEY:demo-key}
openai.model=gpt-4
openai.temperature=0.7
openai.max-tokens=1000
openai.function-calling-enabled=true

# Agent configuration
agent.system-prompt=You are a helpful customer service agent...
agent.max-conversation-turns=20
agent.session-timeout=30m
```

### Tool Function Definitions
```java
// Automatic function definition generation for OpenAI
public class ToolDefinitionGenerator {
    public static List<FunctionDefinition> generateFromMcpService(McpCardService service) {
        // Reflection-based tool discovery
        // OpenAI function schema generation
        // Parameter validation setup
    }
}
```

## 🎯 Example Conversation Flows

### Scenario 1: New Customer
```
Agent: "Hello! Welcome to KK Credit Cards. How may I help you today?"
User: "I want to get information about a card"
Agent: [Calls getBonuses() and getCards()]
Agent: "I'd be happy to help! We have several great cards available. Here are our top options with their signup bonuses: [card list]. Are you looking for a specific type of card or particular benefits?"
```

### Scenario 2: Existing Customer
```
Agent: "Hello! How may I help you today?"
User: "I applied for a card last week, what's the status?"
Agent: [Calls getCustomerApplications("user-context")]
Agent: "I found your application for the Chase Sapphire Preferred from last week. Let me check the current status..."
Agent: [Calls getApplicationStatus(applicationId)]
Agent: "Great news! Your application is currently under review and should be approved within 3-5 business days. Would you like me to set up notifications for when it's approved, or do you have questions about the card benefits?"
```

### Scenario 3: Proactive Assistance
```
Agent: "I see you have an application that's been pending for 7 days. Based on typical processing times, you should hear back soon. Would you like me to check if there are any issues with your application, or would you prefer information about what to expect once it's approved?"
```

## 🚀 Implementation Order

### Week 1: Core Infrastructure
1. **Day 1-2**: OpenAI service integration and basic agent controller
2. **Day 3**: Enhanced MCP tools and demo data service
3. **Day 4**: Agent prompt engineering and tool orchestration
4. **Day 5**: Testing and refinement

### Week 2: Polish & Enhancement
1. **Day 1-2**: Enhanced web interface and UX improvements
2. **Day 3**: Demo scenarios and realistic data
3. **Day 4**: Error handling and edge cases
4. **Day 5**: Documentation and final testing

## 📁 File Structure
```
src/main/java/fin/kk/mcp/
├── agent/
│   ├── AgentController.java
│   ├── OpenAiService.java
│   └── ConversationManager.java
├── config/
│   ├── OpenAiConfig.java
│   └── AgentConfig.java
├── dto/
│   ├── ChatMessage.java
│   ├── ChatResponse.java
│   └── ConversationContext.java
├── model/
│   ├── ApplicationStatus.java
│   ├── CustomerProfile.java
│   └── DemoScenario.java
└── service/
    ├── DemoDataService.java
    └── ToolDefinitionService.java
```

## 🎨 UI Enhancements
- Modern chat interface with bubbles
- Typing indicators during AI processing
- Tool execution status ("Looking up your applications...")
- Conversation history sidebar
- Quick action buttons for common scenarios
- Mobile-responsive design

## 🧪 Testing Strategy
1. **Unit Tests**: Individual tool functions
2. **Integration Tests**: Agent conversation flows
3. **Demo Scenarios**: Predefined conversation paths
4. **Edge Cases**: Error handling and fallbacks
5. **Performance Tests**: Response time optimization

## 📝 Success Metrics
- Agent responds within 3-5 seconds
- Conversation flows feel natural and helpful
- Tools are called appropriately based on context
- Users can complete common tasks without confusion
- Demo showcases agentic behavior effectively

## 🔧 Environment Setup
```bash
# Required environment variables
export OPENAI_API_KEY="your-openai-api-key"

# Optional demo configuration
export DEMO_MODE=true
export AGENT_DEBUG=true
```

## 📚 Learning Outcomes
This demo will showcase:
1. **Agentic AI**: LLM making autonomous decisions about tool usage
2. **Function Calling**: Real-time tool selection and execution
3. **Context Awareness**: Multi-turn conversation management
4. **Proactive Assistance**: AI suggesting relevant next steps
5. **Tool Orchestration**: Chaining multiple tool calls intelligently

## 🎯 Next Steps After Demo
1. Add more sophisticated customer profiles
2. Implement conversation analytics
3. Add voice interface capabilities
4. Integrate with real CRM systems
5. Add multi-language support

---

**Estimated Total Development Time**: 8-12 hours
**Difficulty Level**: Intermediate
**Key Technologies**: Spring Boot, OpenAI GPT-4, Function Calling, MCP Tools, WebSocket/SSE 