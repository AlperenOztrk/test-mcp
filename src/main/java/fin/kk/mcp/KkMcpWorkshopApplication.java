package fin.kk.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * KK Credit Card MCP Server Application
 * 
 * This application provides:
 * 1. MCP Server endpoints at /sse and /mcp/message for AI clients
 * 2. Demo web interface and REST API endpoints for testing
 * 
 * MCP Tools available:
 * - getCards: Get all available credit cards
 * - getCard: Get single card by ID  
 * - submitApplication: Submit credit card application
 * - getBonuses: Get card names and signup bonuses
 */
@SpringBootApplication
public class KkMcpWorkshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkMcpWorkshopApplication.class, args);
	}

}
