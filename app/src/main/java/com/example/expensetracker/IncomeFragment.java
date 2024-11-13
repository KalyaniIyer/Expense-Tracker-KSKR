package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class IncomeFragment extends Fragment {
    private IncomeViewModel incomeViewModel;
    private FirebaseRecyclerAdapter<Data, MyViewHolder> adapter;
    private TextView incomeTotalSum;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        uid = mUser != null ? mUser.getUid() : null;

        incomeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new IncomeViewModel(uid);
            }
        }).get(IncomeViewModel.class);

        incomeTotalSum = myview.findViewById(R.id.income_txt_result);
        incomeViewModel.getTotalIncome().observe(getViewLifecycleOwner(), total ->
                incomeTotalSum.setText(total + ".00")
        );

        RecyclerView recyclerView = myview.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(incomeViewModel.getOptions()) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.income_recycler_data, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(MyViewHolder holder, int position, @NonNull Data model) {
                holder.setAmount(model.getAmount());
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());

                // Declare these variables as final before using them in the lambda expression
                holder.mView.setOnClickListener(v -> {
                    final String post_key = getRef(position).getKey();  // Declare as final
                    final String type = model.getType();  // Declare as final
                    final String note = model.getNote();  // Declare as final
                    final int amount = model.getAmount();  // Declare as final

                    // Now these variables can be safely used in the lambda
                    showUpdateDialog(post_key, type, note, amount);
                });
            }
        };

        recyclerView.setAdapter(adapter);

        // Call setupSwipeToDelete to enable swipe functionality
        setupSwipeToDelete(recyclerView);

        return myview;
    }

    // Swipe-to-delete method
    private void setupSwipeToDelete(RecyclerView incomeRecyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No move operation, only swipe
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                // Get the key of the item at this position
                String itemKey = adapter.getRef(position).getKey();

                // Remove the item from Firebase
                DatabaseReference incomeRef = FirebaseDatabase.getInstance().getReference("IncomeData").child(uid);
                incomeRef.child(itemKey).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Income item deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                        });
            }
        };

        // Attach the ItemTouchHelper to the RecyclerView
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(incomeRecyclerView);
    }

    private void showUpdateDialog(String post_key, String type, String note, int amount) {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        View myview = LayoutInflater.from(getActivity()).inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        EditText editAmount = myview.findViewById(R.id.amount_edit);
        EditText editNote = myview.findViewById(R.id.note_edit);
        Spinner typeSpinner = myview.findViewById(R.id.type_spinner);

        editAmount.setText(String.valueOf(amount));
        editNote.setText(note);

        // Initialize the Spinner with categories and set current selection based on type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.payment_categories, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        int spinnerPosition = adapter.getPosition(type);
        typeSpinner.setSelection(spinnerPosition);

        Button btnUpdate = myview.findViewById(R.id.btnUpdate);
        Button btnDelete = myview.findViewById(R.id.btnDelete);

        final AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(view -> {
            String selectedType = typeSpinner.getSelectedItem().toString();
            String updatedNote = editNote.getText().toString().trim();
            int newAmount = Integer.parseInt(editAmount.getText().toString().trim());
            String date = DateFormat.getDateInstance().format(new Date());

            // Create a new Data object with updated values
            Data data = new Data(newAmount, selectedType, updatedNote, post_key, date);
            incomeViewModel.updateDataItem(data);

            dialog.dismiss();
        });

        btnDelete.setOnClickListener(view -> {
            incomeViewModel.deleteDataItem(post_key);
            dialog.dismiss();
        });
        dialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        void setAmount(int amount) {  // Renamed to setAmount
            TextView mAmount = mView.findViewById(R.id.ammount_txt_income);
            mAmount.setText(String.valueOf(amount));
        }
    }
}