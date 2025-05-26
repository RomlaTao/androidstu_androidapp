package com.example.heath_android.viewmodel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heath_android.model.ChangePassRequest;
import com.example.heath_android.model.ChangePassResponse;
import com.example.heath_android.repository.ChangePassRepository;
public class ChangePassAuthViewModel extends ViewModel {
    private final ChangePassRepository repository = new ChangePassRepository();

    public MutableLiveData<ChangePassResponse> responseLiveData = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public void changePass(String token, String matKhauCu, String matKhauMoi) {
        ChangePassRequest request = new ChangePassRequest(matKhauCu, matKhauMoi);
        repository.doiMatKhau(token, request, responseLiveData, errorLiveData);
    }
}
