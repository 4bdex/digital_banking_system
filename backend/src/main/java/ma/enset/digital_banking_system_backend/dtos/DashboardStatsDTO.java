package ma.enset.digital_banking_system_backend.dtos;

import lombok.Data;
import java.util.Map;

@Data
public class DashboardStatsDTO {
    private long totalCustomers;
    private long totalAccounts;
    private double totalBalance;
    private Map<String, Long> accountTypeCounts; // e.g. {"SAVING": 10, "CURRENT": 20}
    private Map<String, Long> monthlyNewAccounts; // e.g. {"2025-01": 5, "2025-02": 7}
    private Map<String, Double> monthlyTransactions; // e.g. {"2025-01": 10000.0, "2025-02": 12000.0}
}
