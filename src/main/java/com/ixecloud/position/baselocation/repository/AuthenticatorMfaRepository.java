package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.AuthenticatorMfa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticatorMfaRepository extends JpaRepository<AuthenticatorMfa, Integer> {

    AuthenticatorMfa findAuthenticatorMfaByDeviceId(String deviceId);
}
