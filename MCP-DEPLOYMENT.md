# KK Credit Card MCP Server - Deployment Guide

## Overview

This application is a **Model Context Protocol (MCP) Server** that provides credit card information and application tools to AI clients like Claude Desktop.

## MCP Server Endpoints

The MCP server automatically exposes these endpoints:

- **SSE Endpoint**: `http://localhost:8080/sse` - For MCP client connections
- **Message Endpoint**: `http://localhost:8080/mcp/message` - For MCP protocol messages

## Available MCP Tools

| Tool Name | Description |
|-----------|-------------|
| `getCards` | Get all available credit cards with details |
| `getCard` | Get a single credit card by ID |
| `submitApplication` | Submit a credit card application |
| `getBonuses` | Get card names and signup bonuses only |

## Building the Application

```bash
mvn clean package
```

This creates `mcp-0.0.1-SNAPSHOT.jar` in the `target/` directory.

## Running the MCP Server

### Option 1: Direct Java Execution
```bash
java -jar target/mcp-0.0.1-SNAPSHOT.jar
```

### Option 2: Maven Spring Boot Plugin
```bash
mvn spring-boot:run
```

### Option 3: Docker Deployment
```bash
docker build -t kk-mcp-server .
docker run -p 8080:8080 kk-mcp-server
```

## Connecting Claude Desktop

Add this configuration to your Claude Desktop config file:

**macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`
**Windows**: `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "kk-credit-cards": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/your/mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

**Important**: Replace `/absolute/path/to/your/` with the actual path to your JAR file.

## Testing the MCP Server

### 1. Verify Server Status
```bash
curl http://localhost:8080/mcp-test/status
```

### 2. Test MCP SSE Endpoint
```bash
curl http://localhost:8080/sse
```
You should receive an SSE connection response.

### 3. Check Available Tools
```bash
curl http://localhost:8080/mcp-test/info
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | 8080 |

## Configuration

Key MCP server configurations in `application.properties`:

```properties
# MCP Server Configuration
spring.ai.mcp.server.name=kreditkarte
spring.ai.mcp.server.version=1.0.0
spring.ai.mcp.server.instructions=KK Credit Card MCP Server provides tools for credit card information and applications
spring.ai.mcp.server.type=SYNC

# Enable both STDIO and HTTP transports
spring.ai.mcp.server.stdio=true

# MCP HTTP Transport Configuration
spring.ai.mcp.server.sse-endpoint=/sse
spring.ai.mcp.server.sse-message-endpoint=/mcp/message
```

## Demo Features (Optional)

The application also includes demo features:

- **Web Interface**: `http://localhost:8080/` - Interactive demo
- **REST API**: `http://localhost:8080/api/*` - Traditional REST endpoints
- **SSE Streams**: Real-time updates for the web demo

These are separate from the MCP server functionality and can be used for testing.

## Deployment Architecture

```
AI Client (Claude Desktop)
    ↓ MCP Protocol
MCP Server (/sse, /mcp/message)
    ↓ Tool Calls
McpCardService (@Tool methods)
    ↓ Business Logic
CardService (Data Layer)
```

## Troubleshooting

### 1. MCP Endpoints Not Available
- Ensure you're using `spring-ai-starter-mcp-server-webmvc` dependency
- Check that `spring.main.web-application-type=servlet` is set

### 2. Claude Desktop Connection Issues
- Verify the JAR path in the Claude Desktop config is absolute
- Check Claude Desktop logs for error messages
- Ensure Java is available in the PATH

### 3. Tools Not Registered
- Verify `@Tool` annotations are present in `McpCardService`
- Check that `McpServerConfig` bean is properly configured
- Look for MCP-related logs with debug level enabled

## Production Considerations

1. **Security**: Add authentication/authorization for production deployments
2. **Monitoring**: Enable application metrics and health checks
3. **Scaling**: Consider load balancing for multiple instances
4. **Logging**: Configure structured logging for better observability

## Support

For issues with the MCP server functionality, check:
1. Spring AI MCP Server documentation
2. Application logs with debug level enabled
3. MCP protocol specification 