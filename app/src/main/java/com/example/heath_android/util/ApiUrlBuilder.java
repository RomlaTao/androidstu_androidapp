package com.example.heath_android.util;

import android.content.Context;
import com.example.heath_android.data.local.DatabaseInformation;

/**
 * Utility class for building API URLs with user ID
 * Provides methods to create URLs for various service endpoints
 */
public class ApiUrlBuilder {
    private final DatabaseInformation databaseInformation;

    public ApiUrlBuilder(Context context) {
        this.databaseInformation = new DatabaseInformation(context);
    }

    /**
     * Get the current user's ID from local storage
     * @return userId if available, null if not found
     */
    public String getCurrentUserId() {
        return databaseInformation.getUserId();
    }

    /**
     * Create URL for user-specific service endpoints
     * @param baseUrl The base URL of your backend (e.g., "http://localhost:8080")
     * @param servicePath The service path (e.g., "userservice", "workoutservice", etc.)
     * @return Complete URL with userId, or null if userId not available
     */
    public String createServiceUrl(String baseUrl, String servicePath) {
        String userId = getCurrentUserId();
        if (userId != null && !userId.isEmpty()) {
            return String.format("%s/%s/%s", baseUrl.replaceAll("/$", ""), servicePath, userId);
        }
        return null;
    }

    /**
     * Create URL for user service endpoint: /userservice/{userId}
     * @param baseUrl The base URL of your backend
     * @return Complete URL for user service endpoint
     */
    public String createUserServiceUrl(String baseUrl) {
        return createServiceUrl(baseUrl, "users");
    }

    /**
     * Create URL for auth service endpoint: /authservice/{userId}
     * @param baseUrl The base URL of your backend
     * @return Complete URL for auth service endpoint
     */
    public String createAuthServiceUrl(String baseUrl) {
        return createServiceUrl(baseUrl, "auth");
    }

    /**
     * Create URL for workout service endpoint: /workoutservice/{userId}
     * @param baseUrl The base URL of your backend
     * @return Complete URL for workout service endpoint
     */
    public String createWorkoutServiceUrl(String baseUrl) {
        return createServiceUrl(baseUrl, "workouts");
    }

    /**
     * Create URL for meal service endpoint: /mealservice/{userId}
     * @param baseUrl The base URL of your backend
     * @return Complete URL for meal service endpoint
     */
    public String createMealServiceUrl(String baseUrl) {
        return createServiceUrl(baseUrl, "meals");
    }

    /**
     * Create URL for analyst service endpoint: /analystservice/{userId}
     * @param baseUrl The base URL of your backend
     * @return Complete URL for analyst service endpoint
     */
    public String createAnalystServiceUrl(String baseUrl) {
        return createServiceUrl(baseUrl, "analystics");
    }

    /**
     * Create URL with additional path parameters
     * @param baseUrl The base URL of your backend
     * @param servicePath The service path
     * @param additionalPath Additional path to append after userId
     * @return Complete URL with userId and additional path
     */
    public String createServiceUrlWithPath(String baseUrl, String servicePath, String additionalPath) {
        String baseServiceUrl = createServiceUrl(baseUrl, servicePath);
        if (baseServiceUrl != null && additionalPath != null && !additionalPath.isEmpty()) {
            return String.format("%s/%s", baseServiceUrl, additionalPath.replaceAll("^/", ""));
        }
        return baseServiceUrl;
    }

    /**
     * Check if user ID is available for creating URLs
     * @return true if userId is available, false otherwise
     */
    public boolean isUserIdAvailable() {
        String userId = getCurrentUserId();
        return userId != null && !userId.isEmpty();
    }
} 