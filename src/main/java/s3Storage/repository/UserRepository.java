package s3Storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s3Storage.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);

}
