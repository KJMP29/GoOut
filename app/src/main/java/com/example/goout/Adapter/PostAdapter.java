package com.example.goout.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.goout.Model.Post;
import com.example.goout.Model.User;
import com.example.goout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{


    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup,false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(i);

        if (post.getDescription().equals("")){
            viewHolder.description.setVisibility(View.GONE);
        }else{
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.image_profile, viewHolder.username, viewHolder.publisher, post.getPublisher());
        star(post.getPostid(),viewHolder.start);
        nrStarts(viewHolder.starts,post.getPostid());

        viewHolder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.start.getTag().equals("estrellas")){
                    FirebaseDatabase.getInstance().getReference().child("Estrellas").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                }else{

                    FirebaseDatabase.getInstance().getReference().child("Estrellas").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public ImageView image_profile, post_image,start, comment, save;
        public TextView username, starts, publisher, description,comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
           start = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            starts = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);



        }
    }

    private  void star(String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Estrella").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_favorito);
                    imageView.setTag("Favorito");
                }else{
                    imageView.setImageResource(R.drawable.ic_favoritos);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrStarts(final TextView Estrellas, String postid ){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Estrella").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Estrellas.setText(dataSnapshot.getChildrenCount()+"estrellas");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
