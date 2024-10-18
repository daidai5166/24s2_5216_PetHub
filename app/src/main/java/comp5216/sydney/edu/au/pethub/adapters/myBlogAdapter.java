package comp5216.sydney.edu.au.pethub.adapters;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;

public class myBlogAdapter extends RecyclerView.Adapter<mypetAdapter.PetViewHolder> {

    @NonNull
    @Override
    public mypetAdapter.PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull mypetAdapter.PetViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
