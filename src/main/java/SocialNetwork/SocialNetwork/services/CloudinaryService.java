package SocialNetwork.SocialNetwork.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public List<String> uploadImage(MultipartFile[] images) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : images) {
            try {
                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.emptyMap());

                String url = uploadResult.get("secure_url").toString();
                urls.add(url);

            } catch (IOException e) {
                throw new RuntimeException("Upload thất bại: " + e.getMessage(), e);
            }
        }
        return urls;
    }
}
