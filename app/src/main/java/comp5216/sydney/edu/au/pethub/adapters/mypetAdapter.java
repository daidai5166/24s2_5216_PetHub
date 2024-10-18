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
        // 使用你提供的新的布局文件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mypet_card, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        String UrlMypet = "Pets/" + pet.getPetID() + "/image_0.jpg";;
        // 绑定Pet的各个属性到对应的视图元素
        holder.name.setText(pet.getPetName());
        holder.age.setText(pet.getAge() + " Years");
        holder.address.setText(pet.getAddress());

        // 从 Firebase 加载图片到 ImageView
        loadImageFromFirebaseStorageToImageView(context, holder.imageView, UrlMypet,
                e -> {
                    holder.imageView.setImageResource(R.drawable.ic_dog);
        });

        // 设置编辑按钮和删除按钮的点击事件
        holder.editButton.setOnClickListener(v -> {
            // 编辑逻辑
        });

        holder.deleteButton.setOnClickListener(v -> {
            // 删除逻辑
            ConnectDatabase connectDatabase;
            connectDatabase = new ConnectDatabase();
            String petID = pet.getPetID();
            connectDatabase.deletePetAdoptionPost(petID,
                    aVoid -> {
                        // 删除成功后执行的逻辑
                        petList.remove(position);  // 从列表中移除该项
                        notifyItemRemoved(position);  // 通知适配器该项已移除
                        notifyItemRangeChanged(position, petList.size());  // 更新剩余项的范围

                        // 显示成功提示
                        Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    },
                    e -> {
                        // 删除失败后的逻辑
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
            // 初始化视图
            imageView = itemView.findViewById(R.id.pet_image);  // 绑定ImageView
            name = itemView.findViewById(R.id.pet_name);
            address = itemView.findViewById(R.id.pet_address);
            age = itemView.findViewById(R.id.pet_age);
            editButton = itemView.findViewById(R.id.btn_edit_pet1);
            deleteButton = itemView.findViewById(R.id.btn_delete_pet1);
        }
    }
}
