package com.example.expensetracker;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.expensetracker.Model.Data;
import com.example.expensetracker.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class DashBoardFragment extends Fragment {

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;
    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    private boolean isOpen = false;
    private DatabaseReference budgetGoalRef;
    private DatabaseReference totalExpenseRef;
    private int budgetGoal;
    private boolean isGoalSet = false;
    private int totalExpense;
    private int totalsumExp;
    private Animation FadOpen, FadeClose;
    private EditText budgetGoalEditText;
    private TextView totalIncomeResult, totalExpenseResult;
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private RecyclerView mRecyclerIncome, mRecyclerExpense;
    private FirebaseRecyclerAdapter mIncomeAdapter, mExpenseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);
        budgetGoalEditText = myview.findViewById(R.id.editText_budget_amount);

        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_Ft_btn);

        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);

        FadOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        FadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        // Initialize Firebase references
        budgetGoalRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("BudgetGoal");
        totalExpenseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("ExpenseData");


        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                if (isOpen) {
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;
                } else {
                    fab_income_btn.startAnimation(FadOpen);
                    fab_expense_btn.startAnimation(FadOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadOpen);
                    fab_expense_txt.startAnimation(FadOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;
                }
            }
        });

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum = 0;
                for (DataSnapshot mysnap : snapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    totalsum += data.getAmount();

                    String stResult = String.valueOf(totalsum);
                    totalIncomeResult.setText(stResult + ".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalsumExp = 0;
                for (DataSnapshot mysnapshot : snapshot.getChildren()) {
                    Data data = mysnapshot.getValue(Data.class);
                    totalsumExp += data.getAmount();
                    String stResult = String.valueOf(totalsumExp);
                    totalExpenseResult.setText(stResult + ".00");
                    showBudgetExceededWarning();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Button saveGoalButton = myview.findViewById(R.id.button_save_goal); // Assuming this button exists in your layout
        saveGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoal(totalsumExp);
            }
        });

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        mIncomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {

            public IncomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income, parent, false));
            }

            protected void onBindViewHolder(IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setmIncomeAmount(model.getAmount());
                holder.setmIncomeType(model.getType());
                holder.setmIncomeDate(model.getDate());
            }
        };
        mRecyclerIncome.setAdapter(mIncomeAdapter);

        FirebaseRecyclerOptions<Data> optionsExp = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        mExpenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(optionsExp) {

            public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense, parent, false));
            }

            protected void onBindViewHolder(ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setmExpenseAmount(model.getAmount());
                holder.setmExpenseType(model.getType());
                holder.setmExpenseDate(model.getDate());
            }
        };
        mRecyclerExpense.setAdapter(mExpenseAdapter);
        return myview;
    }



    private void ftAnimation() {
        if (isOpen) {
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;

        } else {
            fab_income_btn.startAnimation(FadOpen);
            fab_expense_btn.startAnimation(FadOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadOpen);
            fab_expense_txt.startAnimation(FadOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }

    private void addData() {
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }

    public void incomeDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        final EditText edtAmount = myview.findViewById(R.id.amount_edit);
        final Spinner typeSpinner = myview.findViewById(R.id.type_spinner);
        final EditText edtNote = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeSpinner.getSelectedItem().toString().trim();
                String amountStr = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    Toast.makeText(getActivity(), "Type is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(amountStr)) {
                    edtAmount.setError("Required Field.");
                    return;
                }

                int amount = Integer.parseInt(amountStr);

                if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Required Field.");
                    return;
                }

                String id = mIncomeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(amount, type, note, id, mDate);

                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Income Data Added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void saveGoal(int totalsumExp) {
        String goalText = budgetGoalEditText.getText().toString();
        if (TextUtils.isEmpty(goalText)) {
            return;
        }

        try {
            budgetGoal = Integer.parseInt(goalText);
            // Save to Firebase
            budgetGoalRef.setValue(budgetGoal);
            Toast.makeText(getContext(), "Budget Goal Saved: " + budgetGoal, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid budget goal", Toast.LENGTH_SHORT).show();
        }

        if (budgetGoal > 0 && totalsumExp > budgetGoal) {
            showBudgetExceededWarning();
        }
    }



    private void showBudgetExceededWarning() {
        if (budgetGoal > 0 && totalsumExp > budgetGoal) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Budget Exceeded");
            builder.setMessage("Your total expenses have exceeded your budget goal. Please review your expenses.");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.setIcon(R.drawable.baseline_warning_24); // Optional: Set a warning icon if available
            builder.show();
        }
    }


    public void expenseDataInsert() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);

        final EditText edtAmount = myview.findViewById(R.id.amount_edit);
        final Spinner typeSpinner = myview.findViewById(R.id.type_spinner);
        final EditText edtNote = myview.findViewById(R.id.note_edit);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = typeSpinner.getSelectedItem().toString().trim();
                String amountStr = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)) {
                    Toast.makeText(getActivity(), "Type is required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(amountStr)) {
                    edtAmount.setError("Required Field.");
                    return;
                }

                int amount = Integer.parseInt(amountStr);

                if (TextUtils.isEmpty(note)) {
                    edtNote.setError("Required Field.");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(amount, type, note, id, mDate);

                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Expense Data Added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mIncomeAdapter.startListening();
        mExpenseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mIncomeAdapter.stopListening();
        mExpenseAdapter.stopListening();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {

        View mIncomeView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            mIncomeView = itemView;
        }

        void setmIncomeAmount(int amount) {
            TextView mAmount = mIncomeView.findViewById(R.id.amount_income_ds);
            mAmount.setText(String.valueOf(amount));
        }

        void setmIncomeType(String type) {
            TextView mType = mIncomeView.findViewById(R.id.type_Income_ds);
            mType.setText(type);
        }

        void setmIncomeDate(String date) {
            TextView mDate = mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        View mExpenseView;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            mExpenseView = itemView;
        }

        void setmExpenseAmount(int amount) {
            TextView mAmount = mExpenseView.findViewById(R.id.amount_expense_ds);
            mAmount.setText(String.valueOf(amount));
        }

        void setmExpenseType(String type) {
            TextView mType = mExpenseView.findViewById(R.id.type_expense_ds);
            mType.setText(type);
        }

        void setmExpenseDate(String date) {
            TextView mDate = mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }
}