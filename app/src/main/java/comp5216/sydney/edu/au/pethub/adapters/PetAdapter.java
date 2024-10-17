package comp5216.sydney.edu.au.pethub.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import comp5216.sydney.edu.au.pethub.R;
import comp5216.sydney.edu.au.pethub.model.Pet;
import comp5216.sydney.edu.au.pethub.database.ConnectDatabase;

public class PetAdapter extends BaseAdapter {
    private Context context;
    private List<Pet> petList;
    ConnectDatabase connectDatabase;

    // 构造方法
    public PetAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
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
        if (convertView == null) {
            // 使用布局填充器加载 grid_item_pet.xml
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_pet, parent, false);
        }

        // 获取当前宠物对象
        Pet pet = petList.get(position);

        // 绑定数据到视图
        ImageView petImage = convertView.findViewById(R.id.pet_image);
        TextView petName = convertView.findViewById(R.id.pet_name);
        TextView petDescription = convertView.findViewById(R.id.pet_description);

        // 设置宠物名称和描述
        petName.setText(pet.getPetName());
        petDescription.setText(pet.getDescription());

        // 加载图片（假设 uriStringList 存储了图片 URI 或 URL 的字符串）
        /*if (pet.getUriStringList() != null && !pet.getUriStringList().isEmpty()) {
            String imageUri = pet.getUriStringList().get(0); // 使用第一张图片
            // 使用 Glide 或 Picasso 等库加载图片
            Glide.with(context)
                    .load(imageUri) // 加载图片
                    .placeholder(R.drawable.placeholder_image) // 占位符图片
                    .error(R.drawable.error_image) // 加载失败时显示的图片
                    .into(petImage);
        } else {
            // 如果没有图片，设置默认的占位符图片
            petImage.setImageResource(R.drawable.placeholder_image);
        }*/

        return convertView; // 返回完整的视图作为 GridView 中的每一项
    }
}

