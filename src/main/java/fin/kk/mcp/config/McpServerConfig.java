package fin.kk.mcp.config;

import fin.kk.mcp.service.McpCardService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for MCP Server setup
 */
@Configuration
public class McpServerConfig {

    /**
     * Register MCP tools from the McpCardService
     */
    @Bean
    public ToolCallbackProvider mcpTools(McpCardService mcpCardService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpCardService)
                .build();
    }
} 