package ru.samsung.itschool.mdev.notesrealtime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import ru.samsung.itschool.mdev.notesrealtime.models.Note;

public class NoteListFragment extends Fragment {

    private static final String TAG = "NoteListFragment";

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Note, NoteViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    public NoteListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_note_list, container, false);

        // Инициализация перменной типа DatabaseReference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = rootView.findViewById(R.id.messagesList);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Инициализация переменной для получения данных из Firebase
        Query notesQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Note>()
                .setQuery(notesQuery, Note.class)
                .build();

        // Адаптер для RecyclerView
        mAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(options) {
            @Override
            public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new NoteViewHolder(inflater.inflate(R.layout.item_note, viewGroup, false));
            }
            @Override
            protected void onBindViewHolder(NoteViewHolder viewHolder, int position, final Note model) {
                viewHolder.bindToNote(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // Подключение меню
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    // Обработка onClick() в меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_noteListFragment_to_signInFragment);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // Запрос на получение пользовательских записей
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-notes").child(getUid());
    }

}
