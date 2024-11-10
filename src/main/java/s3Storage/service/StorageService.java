package s3Storage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import s3Storage.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageService {

    private final AmazonS3 s3Client;

    @Value("${application.bucket.name}")
    String bucketName;

    @Autowired
    public StorageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }
    public void uploadFile(User user, MultipartFile file) throws Exception {
        String key = user.getUsername() + "/" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
    }

    public S3Object downloadFile(User user, String filename) {
        String key = user.getUsername() + "/" + filename;
        return s3Client.getObject(bucketName, key);
    }

    public List<String> searchFiles(User user, String keyword) {
        String prefix = user.getUsername() + "/";
        ListObjectsV2Request req = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix);

        ListObjectsV2Result result = s3Client.listObjectsV2(req);
        return result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .filter(key -> key.contains(keyword))
                .map(key -> key.substring(prefix.length()))
                .collect(Collectors.toList());
    }
}
