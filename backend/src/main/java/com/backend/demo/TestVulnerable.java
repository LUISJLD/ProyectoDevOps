package com.backend.demo;

public class TestVulnerable {

    // Hardcoded password
    private String password = "123456";

    // Hardcoded API key (más detectable)
    private String apiKey = "sk-1234567890abcdef";

    public void printSecrets() {
        System.out.println("Password: " + password);
        System.out.println("API Key: " + apiKey);
    }
}