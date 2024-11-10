package s3Storage.dataobject.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    private String name;
    private String username;
    private String password;

}
