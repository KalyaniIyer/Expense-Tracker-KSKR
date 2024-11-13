package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class IncomeViewModel extends ViewModel {
    private final IncomeRepository repository;
    private final MutableLiveData<Integer> totalIncome = new MutableLiveData<>();
    private final FirebaseRecyclerOptions<Data> options;

    public IncomeViewModel(String uid) {
        repository = new IncomeRepository(uid);
        options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(repository.getIncomeDatabase(), Data.class)
                .build();

        calculateTotalIncome();
    }

    public FirebaseRecyclerOptions<Data> getOptions() {
        return options;
    }

    public LiveData<Integer> getTotalIncome() {
        return totalIncome;
    }

    public void calculateTotalIncome() {
        repository.getIncomeDatabase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    if (data != null) {
                        total += data.getAmount();
                    }
                }
                totalIncome.setValue(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public void updateDataItem(Data data) {
        repository.updateDataItem(data);
    }

    public void deleteDataItem(String postKey) {
        repository.deleteDataItem(postKey);
    }
}