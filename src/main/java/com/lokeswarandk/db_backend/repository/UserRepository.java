package com.lokeswarandk.db_backend.repository;

import com.lokeswarandk.db_backend.model.User;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query(
            """
            SELECT DISTINCT mobile_no
            FROM "user"
            WHERE mobile_no LIKE CONCAT(:prefix, '%')
            ORDER BY mobile_no
            LIMIT :limit
            """)
    List<String> findDistinctMobileNosByPrefix(
            @Param("prefix") String prefix, @Param("limit") int limit);

    List<User> findByMobileNo(String mobileNo);
}
