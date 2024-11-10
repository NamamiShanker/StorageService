package s3Storage.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import s3Storage.entity.User;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StorageServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private StorageService storageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        storageService.bucketName = "test-bucket";
    }

    @Test
    public void testDownloadFile() {
        User user = new User();
        user.setUsername("testuser");
        String filename = "testfile.txt";
        String expectedKey = "testuser/testfile.txt";

        S3Object s3Object = new S3Object();
        s3Object.setBucketName("test-bucket");
        s3Object.setKey(expectedKey);

        when(s3Client.getObject("test-bucket", expectedKey)).thenReturn(s3Object);

        S3Object result = storageService.downloadFile(user, filename);

        verify(s3Client).getObject("test-bucket", expectedKey);
        assertEquals(s3Object, result);
    }

    @Test
    public void testSearchFiles() {
        User user = new User();
        user.setUsername("testuser");
        String keyword = "logistics";
        String prefix = "testuser/";

        List<S3ObjectSummary> summaries = Arrays.asList(
                createS3ObjectSummary("testuser/logistics_report.pdf"),
                createS3ObjectSummary("testuser/logistics_data.csv"),
                createS3ObjectSummary("testuser/other_file.txt")
        );

        ListObjectsV2Result result = new ListObjectsV2Result();
        result.getObjectSummaries().addAll(summaries);

        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(result);

        List<String> files = storageService.searchFiles(user, keyword);

        ArgumentCaptor<ListObjectsV2Request> captor = ArgumentCaptor.forClass(ListObjectsV2Request.class);
        verify(s3Client).listObjectsV2(captor.capture());
        assertEquals("test-bucket", captor.getValue().getBucketName());
        assertEquals(prefix, captor.getValue().getPrefix());

        assertEquals(2, files.size());
        assertTrue(files.contains("logistics_report.pdf"));
        assertTrue(files.contains("logistics_data.csv"));
    }

    private S3ObjectSummary createS3ObjectSummary(String key) {
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setBucketName("test-bucket");
        summary.setKey(key);
        return summary;
    }
}
