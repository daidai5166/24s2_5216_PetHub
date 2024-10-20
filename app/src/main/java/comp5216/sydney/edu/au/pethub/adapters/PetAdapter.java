package comp5216.sydney.edu.au.pethub.adapters;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.Pet;

public class PetAdapter extends BaseAdapter {
    private Context context;
    private List<Pet> petList;
    private String URL;

    // 构造方法
    public PetAdapter(Context context, List<Pet> petList) {

        this.context = context;
        if(petList != null){
            this.petList = petList;
        }
        else {
            this.petList = new ArrayList<>() ;
        }
    }

    @Override
    public int getCount() {
        return petList.size(); // Return the number of items in the GridView
    }

    @Override
    public Object getItem(int position) {
        return petList.get(position); // Return the pet object to the specified location
    }

    @Override
    public long getItemId(int position) {
        return position; // Return the ID of the item
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // Load grid_item_pet.xml using layout filler
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_pet, parent, false);

            holder = new ViewHolder();
            holder.petImage = convertView.findViewById(R.id.pet_image);
            holder.petName = convertView.findViewById(R.id.pet_name);
            holder.petDescription = convertView.findViewById(R.id.pet_description);

            convertView.setTag(holder);
        } else {
            // Reuse existing convertView
            holder = (PetAdapter.ViewHolder) convertView.getTag();
        }

        // Get the current pet object
        Pet pet = petList.get(position);

        // Bind data to view
        // holder.petImage.findViewById(R.id.pet_image);
        holder.petName.setText(pet.getPetName());
        holder.petDescription.setText(pet.getDescription());

        URL = "Pets/" + pet.getPetID() + "/image_0.jpg";

        loadImageFromFirebaseStorageToImageView(context, holder.petImage, URL);

        return convertView;
    }

    // ViewHolder mode, used for caching views
    private static class ViewHolder {
        ImageView petImage;
        TextView petName;
        TextView petDescription;
    }

    public void updatePets(List<Pet> newPets) {
        this.petList.clear();
        this.petList.addAll(newPets);
        notifyDataSetChanged();
    }
}

