package ru.samsung.itschool.mdev.notesrealtime;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ru.samsung.itschool.mdev.notesrealtime.databinding.FragmentAddNoteBinding;
import ru.samsung.itschool.mdev.notesrealtime.models.Note;
import ru.samsung.itschool.mdev.notesrealtime.models.User;

public class AddNoteFragment extends BaseFragment {
    private static final String TAG = "NewNoteFragment";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private FragmentAddNoteBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.fabSubmitPost.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        final String title = binding.fieldTitle.getText().toString();
        final String body = binding.fieldBody.getText().toString();
        if (TextUtils.isEmpty(title)) {
            binding.fieldTitle.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(body)) {
            binding.fieldBody.setError(REQUIRED);
            return;
        }
        setEditingEnabled(false);
        Toast.makeText(getContext(), "Posting...", Toast.LENGTH_SHORT).show();
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(getContext(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewPost(userId, user.username, title, body);
                        }
                        setEditingEnabled(true);
                        NavHostFragment.findNavController(AddNoteFragment.this)
                                .navigate(R.id.action_addNoteFragment_to_noteListFragment);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        binding.fieldTitle.setEnabled(enabled);
        binding.fieldBody.setEnabled(enabled);
        if (enabled) {
            binding.fabSubmitPost.show();
        } else {
            binding.fabSubmitPost.hide();
        }
    }

    private void writeNewPost(String userId, String username, String title, String body) {
        // Создание записи в /user-notes/$userid/$postid
        String key = mDatabase.child("user-notes").push().getKey();
        Note note = new Note(userId, username, title, body);
        Map<String, Object> noteValues = note.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-notes/" + userId + "/" + key, noteValues);
        mDatabase.updateChildren(childUpdates);
    }
}
