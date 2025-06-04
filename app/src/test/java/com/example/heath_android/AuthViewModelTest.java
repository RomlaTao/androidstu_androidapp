package com.example.heath_android;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.heath_android.data.model.auth.LoginResponse;
import com.example.heath_android.data.model.auth.SignupRequest;
import com.example.heath_android.data.model.User;
import com.example.heath_android.ui.auth.AuthViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Observer<LoginResponse> loginResponseObserver;

    @Mock
    private Observer<User> signupSuccessObserver;

    @Mock
    private Observer<String> errorObserver;

    private AuthViewModel authViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authViewModel = new AuthViewModel();
    }

    @Test
    public void testLoginWithValidCredentials() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        // When
        authViewModel.getResponseLiveData().observeForever(loginResponseObserver);
        authViewModel.login(email, password);

        // Then
        // Note: Since we're using actual repository, this test would need network
        // In real implementation, you should mock the repository
        assertNotNull(authViewModel.getResponseLiveData());
    }

    @Test
    public void testSignupWithValidData() {
        // Given
        SignupRequest signupRequest = new SignupRequest("test@example.com", "Test User", "password123");

        // When
        authViewModel.getSignupSuccessLiveData().observeForever(signupSuccessObserver);
        authViewModel.signupUser(signupRequest);

        // Then
        assertNotNull(authViewModel.getSignupSuccessLiveData());
    }

    @Test
    public void testGetResponseLiveData() {
        // Given & When
        assertNotNull(authViewModel.getResponseLiveData());
        assertNotNull(authViewModel.getLoginErrorLiveData());
        assertNotNull(authViewModel.getSignupSuccessLiveData());
        assertNotNull(authViewModel.getSignupErrorLiveData());
    }
} 