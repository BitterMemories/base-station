package com.ixecloud.position.baselocation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "authenticator_mfa")
public class AuthenticatorMfa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "mfa_credentials")
    private String mfaCredentials;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getMfaCredentials() {
        return mfaCredentials;
    }

    public void setMfaCredentials(String mfaCredentials) {
        this.mfaCredentials = mfaCredentials;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("deviceId", deviceId)
                .append("mfaCredentials", mfaCredentials).toString();
    }
}
