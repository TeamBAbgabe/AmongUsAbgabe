package com.example.BackendAmongUs.Lobby.Avatars;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Component
public class ListOfAvatars {
    private List<String> avatars = new ArrayList<>();
    private Random rnd = new Random();

    public ListOfAvatars(){
        avatars.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQRDj6hYj7pKwEbVEkTRl4LC9av_4kVpremyk56vrEqBg&s");
        avatars.add("https://cdn-icons-png.flaticon.com/512/1205/1205647.png");
        avatars.add("https://www.amongusavatarcreator.com/assets/img/characters/600/cyan.png");
        avatars.add("https://i.imgur.com/5KbFv1r.png");
        avatars.add("https://substackcdn.com/image/fetch/f_auto,q_auto:good,fl_progressive:steep/https%3A%2F%2Fsubstack-post-media.s3.amazonaws.com%2Fpublic%2Fimages%2F88d26018-fa1a-4b92-a8b9-d8ed3f9e178e_3840x2160.png");
        avatars.add("https://www.writeups.org/wp-content/uploads/Doomguy-doom-portrait-featured-sprite.jpg");
        avatars.add("https://i.pinimg.com/736x/56/4b/71/564b71050fb2c79e66e766148577b7e3.jpg");
    }

    public String getRandomAvatar(){
        int randomnumber = rnd.nextInt(avatars.size());
        return avatars.get(randomnumber);
    }

}
