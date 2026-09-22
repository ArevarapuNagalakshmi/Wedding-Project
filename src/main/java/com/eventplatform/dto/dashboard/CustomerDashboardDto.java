package com.eventplatform.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Customer Dashboard Data Transfer Object
 * Aggregates all dashboard information for the frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDashboardDto {

    // Dashboard metrics
    private DashboardMetrics metrics;

    // Recent bookings list
    private List<?> recentBookings;

    // Saved vendors list
    private List<?> savedVendors;

    // Dashboard insights
    private DashboardInsights insights;

    /**
     * Dashboard metrics wrapper
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardMetrics {
        private Integer totalBookings;
        private Integer upcomingBookings;
        private Integer completedBookings;
        private Integer savedVendors;
        private Integer browsingHistory;
    }

    /**
     * Dashboard insights wrapper
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardInsights {
        private String planningStatus;
        private List<PlanningTip> tips;
        private BookingReadiness bookingReadiness;
    }

    /**
     * Planning tip for insights
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanningTip {
        private String id;
        private String title;
        private String description;
        private String category;
        private Integer priority;
    }

    /**
     * Booking readiness checklist
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingReadiness {
        private Boolean profileComplete;
        private Boolean contactInfoVerified;
        private Boolean paymentMethodAdded;
        private Integer completionPercentage;
    }
}
