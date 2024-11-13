package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExpenseViewModel extends ViewModel {
    private final ExpenseRepository repository;
    private final MutableLiveData<Integer> totalExpense = new MutableLiveData<>();
    private final FirebaseRecyclerOptions<Data> options;

    public ExpenseViewModel(String uid) {
        repository = new ExpenseRepository(uid);
        options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(repository.getExpenseDatabase(), Data.class)
                .build();

        calculateTotalExpense();
    }

    public FirebaseRecyclerOptions<Data> getOptions() {
        return options;
    }

    public LiveData<Integer> getTotalIncome() {
        return totalExpense;
    }

    public void calculateTotalExpense() {
        repository.getExpenseDatabase().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Data data = snap.getValue(Data.class);
                    if (data != null) {
                        total += data.getAmount();
                    }
                }
                totalExpense.setValue(total);
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