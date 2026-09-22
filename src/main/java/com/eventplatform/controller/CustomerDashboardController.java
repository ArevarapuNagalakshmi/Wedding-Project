package com.eventplatform.controller;

import com.eventplatform.dto.ApiResponse;
import com.eventplatform.dto.dashboard.CustomerDashboardDto;
import com.eventplatform.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Dashboard Controller for Customer Dashboard
 * Provides aggregated data for the customer dashboard UI
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerDashboardController {

    private final DashboardService dashboardService;

    /**
     * Get customer dashboard data
     * Includes metrics, recent bookings, saved vendors, and insights
     */
    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<CustomerDashboardDto>> getCustomerDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Fetching dashboard data for user: {}", userDetails.getUsername());

        CustomerDashboardDto dashboardData = dashboardService
                .getCustomerDashboard(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.success(
                        dashboardData,
                        "Dashboard data retrieved successfully"
                )
        );
    }

    /**
     * Get dashboard metrics (quick stats)
     * Total bookings, saved vendors, services browsed, etc.
     */
    @GetMapping("/customer/metrics")
    public ResponseEntity<ApiResponse<?>> getMetrics(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Fetching metrics for user: {}", userDetails.getUsername());

        var metrics = dashboardService.getMetrics(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.success(
                        metrics,
                        "Metrics retrieved successfully"
                )
        );
    }

    /**
     * Get recent bookings for the customer
     */
    @GetMapping("/customer/recent-bookings")
    public ResponseEntity<ApiResponse<?>> getRecentBookings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "5") Integer limit) {

        log.info("Fetching recent bookings for user: {}", userDetails.getUsername());

        var bookings = dashboardService.getRecentBookings(userDetails.getUsername(), limit);

        return ResponseEntity.ok(
                ApiResponse.success(
                        bookings,
                        "Recent bookings retrieved successfully"
                )
        );
    }

    /**
     * Get saved vendors for quick access
     */
    @GetMapping("/customer/saved-vendors")
    public ResponseEntity<ApiResponse<?>> getSavedVendors(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "6") Integer limit) {

        log.info("Fetching saved vendors for user: {}", userDetails.getUsername());

        var vendors = dashboardService.getSavedVendors(userDetails.getUsername(), limit);

        return ResponseEntity.ok(
                ApiResponse.success(
                        vendors,
                        "Saved vendors retrieved successfully"
                )
        );
    }

    /**
     * Get dashboard insights (booking readiness, planning tips, etc.)
     */
    @GetMapping("/customer/insights")
    public ResponseEntity<ApiResponse<?>> getInsights(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Fetching insights for user: {}", userDetails.getUsername());

        var insights = dashboardService.getInsights(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.success(
                        insights,
                        "Insights retrieved successfully"
                )
        );
    }
}
