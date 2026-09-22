package com.eventplatform.service.dashboard;

import com.eventplatform.dto.dashboard.CustomerDashboardDto;
import com.eventplatform.entity.Booking;
import com.eventplatform.entity.User;
import com.eventplatform.exception.ResourceNotFoundException;
import com.eventplatform.repository.BookingRepository;
import com.eventplatform.repository.UserRepository;
import com.eventplatform.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Dashboard Service
 * Provides aggregated data for customer dashboard
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final VendorProfileRepository vendorProfileRepository;

    /**
     * Get complete customer dashboard data
     */
    public CustomerDashboardDto getCustomerDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return CustomerDashboardDto.builder()
                .metrics(getMetricsInternal(user.getId()))
                .recentBookings(getRecentBookingsInternal(user.getId(), 5))
                .savedVendors(getSavedVendorsInternal(user.getId(), 6))
                .insights(getInsightsInternal(user.getId()))
                .build();
    }

    /**
     * Get metrics for the user
     */
    public Map<String, Object> getMetrics(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        CustomerDashboardDto.DashboardMetrics metrics = getMetricsInternal(user.getId());

        return Map.of(
                "totalBookings", metrics.getTotalBookings(),
                "upcomingBookings", metrics.getUpcomingBookings(),
                "completedBookings", metrics.getCompletedBookings(),
                "savedVendors", metrics.getSavedVendors(),
                "browsingHistory", metrics.getBrowsingHistory()
        );
    }

    /**
     * Get recent bookings
     */
    public List<?> getRecentBookings(String email, Integer limit) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return getRecentBookingsInternal(user.getId(), limit);
    }

    /**
     * Get saved vendors
     */
    public List<?> getSavedVendors(String email, Integer limit) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return getSavedVendorsInternal(user.getId(), limit);
    }

    /**
     * Get insights
     */
    public Map<String, Object> getInsights(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        CustomerDashboardDto.DashboardInsights insights = getInsightsInternal(user.getId());

        return Map.of(
                "planningStatus", insights.getPlanningStatus(),
                "tips", insights.getTips(),
                "bookingReadiness", insights.getBookingReadiness()
        );
    }

    // ==================== Internal Methods ====================

    private CustomerDashboardDto.DashboardMetrics getMetricsInternal(Long userId) {
        List<Booking> bookings = bookingRepository.findByUser_Id(userId);

        Integer totalBookings = bookings.size();
        Integer upcomingBookings = (int) bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()) || "PENDING".equals(b.getStatus()))
                .count();
        Integer completedBookings = (int) bookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus()))
                .count();
        Integer savedVendors = Math.toIntExact(vendorProfileRepository.count());
        Integer browsingHistory = Math.min(totalBookings * 2, 100);

        return CustomerDashboardDto.DashboardMetrics.builder()
                .totalBookings(totalBookings)
                .upcomingBookings(upcomingBookings)
                .completedBookings(completedBookings)
                .savedVendors(savedVendors)
                .browsingHistory(browsingHistory)
                .build();
    }

    private List<?> getRecentBookingsInternal(Long userId, Integer limit) {
        // This will be replaced with actual booking data
        return new ArrayList<>();
    }

    private List<?> getSavedVendorsInternal(Long userId, Integer limit) {
        // This will be replaced with actual saved vendor data
        return new ArrayList<>();
    }

    private CustomerDashboardDto.DashboardInsights getInsightsInternal(Long userId) {
        List<CustomerDashboardDto.PlanningTip> tips = Arrays.asList(
                CustomerDashboardDto.PlanningTip.builder()
                        .id("tip-1")
                        .title("Complete Your Profile")
                        .description("Add a profile picture and bio to help vendors understand your needs")
                        .category("PROFILE")
                        .priority(1)
                        .build(),
                CustomerDashboardDto.PlanningTip.builder()
                        .id("tip-2")
                        .title("Set Your Budget")
                        .description("Define your wedding budget to get relevant vendor recommendations")
                        .category("BUDGET")
                        .priority(2)
                        .build()
        );

        CustomerDashboardDto.BookingReadiness readiness = CustomerDashboardDto.BookingReadiness.builder()
                .profileComplete(true)
                .contactInfoVerified(true)
                .paymentMethodAdded(true)
                .completionPercentage(100)
                .build();

        return CustomerDashboardDto.DashboardInsights.builder()
                .planningStatus("IN_PROGRESS")
                .tips(tips)
                .bookingReadiness(readiness)
                .build();
    }
}
