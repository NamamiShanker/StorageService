package s3Storage.controller;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import s3Storage.dataobject.response.FileUploadResponse;
import s3Storage.entity.User;
import s3Storage.service.StorageService;
import s3Storage.service.UserService;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    @PostMapping(value = "/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@CookieValue(value = "session_id") String sessionId, @RequestParam(value = "file") MultipartFile file) {
        try {
            User user = userService.getUserBySessionId(sessionId);
            if (user != null) {
                storageService.uploadFile(user, file);
                FileUploadResponse response = new FileUploadResponse(HttpStatus.OK.value(), null, file.getOriginalFilename());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping(value = "/searchFiles")
    public ResponseEntity<List<String>> searchFiles(@CookieValue(value = "session_id") String sessionId, @RequestParam String keyword) {
        User user = userService.getUserBySessionId(sessionId);
        if(user != null) {
            List<String> results = storageService.searchFiles(user, keyword);
            return ResponseEntity.ok(results);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping(value = "/downloadFile")
    public ResponseEntity<byte []> downloadFile(@CookieValue(value = "session_id") String sessionId, @RequestParam String fileName) {
        try {
            User user = userService.getUserBySessionId(sessionId);
            if (user != null) {
                S3Object s3Object = storageService.downloadFile(user, fileName);
                InputStream is = s3Object.getObjectContent();
                byte[] content = is.readAllBytes();
                String encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(content);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
