package com.example.heath_android.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heath_android.model.LogInResponse;
import com.example.heath_android.repository.LogInRepository;

public class LogInAuthViewModel extends ViewModel {
    private final LogInRepository repository = new LogInRepository();
    private final MutableLiveData<LogInResponse> responseLiveData = new MutableLiveData<>();

    public void login(String email, String password) {
        repository.login(email, password, responseLiveData);
    }

    public LiveData<LogInResponse> getResponseLiveData() {
        return responseLiveData;
    }
}
