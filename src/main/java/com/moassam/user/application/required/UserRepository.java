package com.moassam.user.application.required;

import com.moassam.user.domain.Provider;
import com.moassam.user.domain.User;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, Long> {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);

    List<User> findAllByIdIn(Collection<Long> ids);
}