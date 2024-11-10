package s3Storage.dataobject.response;

public class CreateUserResponse extends BaseResponse{

    public String name;
    public String username;

    public CreateUserResponse(Integer statusCode, String message, String name, String username) {
        super(statusCode, message);
        this.name = name;
        this.username = username;
    }
}
