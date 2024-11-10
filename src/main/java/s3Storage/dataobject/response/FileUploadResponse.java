package s3Storage.dataobject.response;

import lombok.Getter;

@Getter
public class FileUploadResponse extends BaseResponse{

    private String uploadedFileName;

    public FileUploadResponse(Integer statusCode, String message, String uploadedFileName) {
        super(statusCode, message);
        this.uploadedFileName = uploadedFileName;
    }
}
