package comp5216.sydney.edu.au.pethub.adapters;

import static comp5216.sydney.edu.au.pethub.database.ConnectDatabase.loadImageFromFirebaseStorageToImageView;

import android.content.Context;
import android.util.Log;
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
        return petList.size(); // 返回GridView中项目的数量
    }

    @Override
    public Object getItem(int position) {
        return petList.get(position); // 返回指定位置的宠物对象
    }

    @Override
    public long getItemId(int position) {
        return position; // 返回项的ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // 使用布局填充器加载 grid_item_pet.xml
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_pet, parent, false);

            holder = new ViewHolder();
            holder.petImage = convertView.findViewById(R.id.pet_image);
            holder.petName = convertView.findViewById(R.id.pet_name);
            holder.petDescription = convertView.findViewById(R.id.pet_description);

            convertView.setTag(holder);
        } else {
            // 复用已有的 convertView
            holder = (PetAdapter.ViewHolder) convertView.getTag();
        }

        // 获取当前宠物对象
        Pet pet = petList.get(position);

        // 绑定数据到视图
//        holder.petImage.findViewById(R.id.pet_image);
        holder.petName.setText(pet.getPetName());
        holder.petDescription.setText(pet.getDescription());

        URL = "Pets/" + pet.getPetID() + "/image_0.jpg";

        loadImageFromFirebaseStorageToImageView(context, holder.petImage, URL);

        return convertView;
    }

    // ViewHolder 模式，用于缓存视图
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

