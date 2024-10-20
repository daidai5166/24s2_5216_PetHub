package comp5216.sydney.edu.au.pethub.adapters;


import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Context;
import android.content.Intent;
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
import comp5216.sydney.edu.au.pethub.activity.PostpetActivity;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;

public class mypetAdapter extends RecyclerView.Adapter<mypetAdapter.PetViewHolder> {
    private Context context;
    private List<Pet> petList;
    public mypetAdapter (Context context,List<Pet> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the new layout file you provided
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypet_card, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        String UrlMypet = "Pets/" + pet.getPetID() + "/image_0.jpg";
        // Bind the various attributes of Pet to the corresponding view elements
        holder.name.setText(pet.getPetName());
        holder.age.setText(pet.getAge() + " Years");
        holder.address.setText(pet.getAddress());

        // Loading images from Firebase to ImageView
        loadImageFromFirebaseStorageToImageView(context, holder.imageView, UrlMypet,
                e -> {
                    holder.imageView.setImageResource(R.drawable.ic_dog);
        });

        // Set the click events for the edit button and delete button
        holder.editButton.setOnClickListener(v -> {
            // Edit Logic
            Intent intent = new Intent(context, PostpetActivity.class);
            intent.putExtra("selectedPet",pet);
            context.startActivity(intent);

        });

        holder.deleteButton.setOnClickListener(v -> {
            // Delete Logic
            ConnectDatabase connectDatabase;
            connectDatabase = new ConnectDatabase();
            String petID = pet.getPetID();
            connectDatabase.deletePetAdoptionPost(petID,
                    aVoid -> {
                        // Logic executed after successful deletion
                        petList.remove(position);  // Remove the item from the list
                        notifyItemRemoved(position);  // Notify adapter that this item has been removed
                        notifyItemRangeChanged(position, petList.size());  // Notify adapter that this item has been deleted

                        // Display success prompt
                        Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    },
                    e -> {
                        // Logic after deletion failure
                        Log.e("mypet_deleteError","Can't delete"+petID);
                        Toast.makeText(context, "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
            );
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    // ViewHolder类，绑定新的布局中的视图
    public static class PetViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address, age;
        public ImageView imageView;
        public Button editButton, deleteButton;

        public PetViewHolder(View itemView) {
            super(itemView);
            // Initialize View
            imageView = itemView.findViewById(R.id.pet_image);  // Bind ImageView
            name = itemView.findViewById(R.id.pet_name);
            address = itemView.findViewById(R.id.pet_address);
            age = itemView.findViewById(R.id.pet_age);
            editButton = itemView.findViewById(R.id.btn_edit_pet1);
            deleteButton = itemView.findViewById(R.id.btn_delete_pet1);
        }
    }
}
