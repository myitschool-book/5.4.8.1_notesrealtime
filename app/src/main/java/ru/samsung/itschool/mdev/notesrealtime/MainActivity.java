package ru.samsung.itschool.mdev.notesrealtime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.samsung.itschool.mdev.notesrealtime.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        fab = binding.fab;

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.nav_graph);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.noteListFragment) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(view -> navController.navigate(R.id.action_noteListFragment_to_addNoteFragment));
            } else {
                fab.setVisibility(View.GONE);
            }
        });
    }

}