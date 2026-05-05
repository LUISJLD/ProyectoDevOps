package com.backend.demo;

public class TestVulnerable {
    public String PASSWORD="admin123";
    public void run(String input) throws Exception {
        Runtime.getRuntime().exec(input);
    }
}