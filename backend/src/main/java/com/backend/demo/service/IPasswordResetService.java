package com.backend.demo.service;

public interface IPasswordResetService {
        void forgotPassword(String email);
        void resetPassword(String token, String newPassword);
}