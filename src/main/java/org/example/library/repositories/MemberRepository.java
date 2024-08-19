package org.example.library.repositories;

import io.micrometer.common.lang.NonNull;
import org.example.library.entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("select distinct m from Member m left join fetch m.books where m.id=:id")
    Optional<Member> findById(@NonNull Long id);

    @Query("select distinct m from Member m left join fetch m.books where m.name=:name")
    Member findByName(String name);
}
