# 🤖 Agentic AI Customer Service System

## 🎯 Overview

This project showcases a **truly intelligent agentic AI customer service system** for credit card services. Unlike traditional chatbots with hardcoded responses, this system features an AI agent that:

- 🧠 **Thinks autonomously** about which tools to use
- 🌍 **Communicates naturally** in any language
- 📊 **Interprets raw data** intelligently rather than using templates
- 🔗 **Chains tool calls** for comprehensive customer assistance
- 🎨 **Adapts responses** to customer communication style and context

## ✨ Key Features

### 🤖 Emma - The Intelligent Agent
- **Powered by OpenAI GPT-4** with advanced function calling
- **Proactive customer service** - anticipates needs and suggests actions
- **Multilingual support** - responds naturally in customer's language
- **Context awareness** - remembers conversation history and builds upon it
- **Agentic decision making** - autonomously chooses appropriate tools and responses

### 🛠️ Comprehensive MCP Tools
1. **`getCards`** - Browse all available credit cards
2. **`getCard`** - Get detailed information about specific cards
3. **`getBonuses`** - Compare signup bonuses across cards
4. **`submitApplication`** - Process new credit card applications
5. **`getCustomerApplications`** - Look up customer application history
6. **`getApplicationStatus`** - Check detailed application status
7. **`cancelApplication`** - Cancel pending applications
8. **`estimateApprovalTime`** - Provide approval time estimates
9. **`getApplicationsNeedingAttention`** - Find applications needing follow-up

### 🌟 Agentic Intelligence
- **Raw Data Interpretation**: Tools return raw JSON; AI interprets and presents naturally
- **No Hardcoded Responses**: Every response is generated contextually by the LLM
- **Intelligent Tool Chaining**: AI decides when to use multiple tools in sequence
- **Natural Language Generation**: Responses feel human-like and conversational

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- OpenAI API Key

### Setup
```bash
# Clone the repository
git clone https://github.com/your-org/agentic-ai-customer-service.git
cd agentic-ai-customer-service

# Set your OpenAI API key
export OPENAI_API_KEY="your-openai-api-key-here"

# Build and run
mvn spring-boot:run
```

### Access the System
- **Web Interface**: http://localhost:8080
- **API Endpoint**: http://localhost:8080/api/agent/chat

## 💬 Example Conversations

### English Customer
```
Customer: "Hi, I want to see your credit cards"
Emma: "Hello! I'd be happy to help you explore our credit card options. Let me get our current cards and their signup bonuses for you."

[Emma calls getBonuses() and getCards()]

Emma: "We have some excellent options available! Here are the highlights:

• Chase Sapphire Preferred: 60,000 bonus points (worth $750 in travel) after $4,000 spend
• Capital One Venture: $500 cash bonus after $3,000 spend  
• American Express Gold: 50,000 points after $2,000 spend

Are you looking for travel rewards, cashback, or building credit? I can help you find the perfect match for your needs."
```

### Spanish Customer
```
Customer: "Hola, quiero información sobre tarjetas de crédito"
Emma: "¡Hola! Me encantaría ayudarte a encontrar la tarjeta de crédito perfecta. Déjame revisar nuestras opciones actuales y sus bonos de registro."

[Emma calls getBonuses() and getCards()]

Emma: "Tenemos excelentes opciones disponibles:

• Chase Sapphire Preferred: 60,000 puntos de bono (equivalen a $750 en viajes) después de gastar $4,000
• Capital One Venture: $500 en efectivo después de gastar $3,000
• American Express Gold: 50,000 puntos después de gastar $2,000

¿Buscas recompensas de viaje, dinero en efectivo, o construir tu historial crediticio? Puedo ayudarte a encontrar la opción ideal."
```

### Existing Customer Check
```
Customer: "Hi, I'm John Smith. What's the status of my application?"
Emma: "Hello John! Let me check your applications right away."

[Emma calls getCustomerApplications("John Smith")]

Emma: "I found your application for the Chase Sapphire Preferred from 8 days ago. I notice it's been pending longer than usual - let me get the current status and estimated approval time."

[Emma calls getApplicationStatus() and estimateApprovalTime()]

Emma: "Good news! Your application is in final review and should be approved within 2-3 business days. The delay was due to additional income verification, which has now been completed. 

You should receive an email confirmation once approved, followed by your card within 7-10 business days. Is there anything else you'd like to know about your application or our services?"
```

## 🏗️ Architecture

### Core Components

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web Client    │───▶│  Agent Controller │───▶│  OpenAI Service │
│  (Chat UI)      │    │                  │    │                │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
                                               ┌─────────────────┐
                                               │   MCP Tools     │
                                               │  (Raw Data)     │
                                               └─────────────────┘
                                                        │
                                                        ▼
                                               ┌─────────────────┐
                                               │ OpenAI GPT-4    │
                                               │ (Intelligent    │
                                               │  Interpretation)│
                                               └─────────────────┘
```

### Data Flow
1. **User Input** → Agent Controller
2. **Function Calling** → OpenAI determines which tools to use
3. **Tool Execution** → Raw JSON data returned from MCP tools
4. **Follow-up Call** → Raw data sent back to OpenAI
5. **Intelligent Response** → GPT-4 interprets data and generates natural response
6. **User Response** → Natural, contextual answer in appropriate language

## 🔧 Technical Implementation

### Key Classes
- **`OpenAiService`** - Handles GPT-4 integration and function calling
- **`AgentController`** - REST endpoints for chat interactions
- **`McpCardService`** - MCP tool implementations
- **`ConversationContext`** - Session management and history

### Function Calling Flow
```java
// 1. Execute MCP tool and get raw data
Object result = executeMcpFunction(functionName, arguments);

// 2. Send raw JSON back to OpenAI (NO hardcoded formatting)
toolResult.put("content", objectMapper.writeValueAsString(result));

// 3. Let GPT-4 intelligently interpret and respond
String response = callOpenAiWithToolResults(toolResults);
```

### System Prompt Highlights
- **Agentic Behavior**: "You autonomously decide which tools to use based on customer needs"
- **Data Interpretation**: "When you receive raw data from tools, intelligently analyze and present it"
- **Multilingual**: "Communicate fluently in any language the customer uses"
- **Natural Communication**: "Respond in the customer's language and communication style naturally"

## 🌍 Multilingual Examples

### German
```
Customer: "Hallo, ich brauche Informationen über Kreditkarten"
Emma: "Hallo! Gerne helfe ich Ihnen bei der Suche nach der perfekten Kreditkarte. Lassen Sie mich unsere aktuellen Optionen und Anmeldeboni für Sie abrufen..."
```

### French
```
Customer: "Bonjour, je voudrais des informations sur les cartes de crédit"
Emma: "Bonjour ! Je serais ravie de vous aider à trouver la carte de crédit parfaite. Permettez-moi de consulter nos options actuelles et leurs bonus d'inscription..."
```

### Japanese
```
Customer: "こんにちは、クレジットカードについて教えてください"
Emma: "こんにちは！最適なクレジットカードを見つけるお手伝いをさせていただきます。現在のオプションとサインアップボーナスを確認いたします..."
```

## 📊 Benefits Over Traditional Chatbots

| Traditional Chatbots | Agentic AI System |
|---------------------|-------------------|
| ❌ Hardcoded responses | ✅ Dynamic, contextual responses |
| ❌ Single language support | ✅ Natural multilingual communication |
| ❌ Rigid conversation flow | ✅ Adaptive, intelligent conversations |
| ❌ Template-based answers | ✅ Interpreted, insightful responses |
| ❌ Limited tool integration | ✅ Intelligent tool chaining |
| ❌ No context awareness | ✅ Conversation memory and building |

## 🧪 Testing & Demo Scenarios

### Demo Scenarios
1. **New Customer Journey** - Card exploration and comparison
2. **Application Status Check** - Existing customer support
3. **Multilingual Support** - Same functionality in different languages
4. **Proactive Service** - Finding and addressing delayed applications
5. **Complex Inquiries** - Multi-step tool chaining for comprehensive assistance

### API Testing
```bash
# Test the chat endpoint
curl -X POST http://localhost:8080/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hi, I want to see your credit cards",
    "sessionId": "test-session-123"
  }'
```

## 🚀 Future Enhancements

- **Voice Integration** - Add speech-to-text and text-to-speech
- **Advanced Analytics** - Customer interaction insights and optimization
- **Integration APIs** - Connect with real CRM and banking systems
- **Advanced Reasoning** - Enhanced decision-making capabilities
- **Personalization** - Learn customer preferences over time

## 📝 Development Notes

### Adding New Tools
1. Add method to `McpCardService`
2. Update tool definitions in `OpenAiService.buildToolDefinitions()`
3. Add case to `executeMcpFunction()` switch statement
4. Update system prompt if needed

### Key Principles
- **Never hardcode response formatting** - Let the LLM interpret raw data
- **Trust the AI** - Allow intelligent decision-making about tool usage
- **Enhance prompts** - Guide behavior through intelligent prompting
- **Test multilingually** - Verify responses work in various languages
- **Monitor context** - Ensure conversation flows naturally

## 📚 Related Documentation

- [Agentic AI Improvements](./AGENTIC_AI_IMPROVEMENTS.md) - Technical details of the agentic implementation
- [Demo Plan](./AGENTIC_AI_DEMO_PLAN.md) - Original planning and architecture decisions
- [Deployment Guide](./DEPLOYMENT.md) - Production deployment instructions

---

**This system represents the future of customer service AI - intelligent, adaptive, and truly helpful in any language.** 🌟 