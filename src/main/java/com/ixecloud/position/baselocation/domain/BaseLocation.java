package com.ixecloud.position.baselocation.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "base_location")
public class BaseLocation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "lac")
    private Integer lac;

    @Column(name = "cell_id")
    private Integer cellId;

    @Column(name = "mcc")
    private Integer mcc;

    @Column(name = "mnc")
    private Integer mnc;

    @Column(name = "signal")
    private Integer signal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLac() {
        return lac;
    }

    public void setLac(Integer lac) {
        this.lac = lac;
    }

    public Integer getCellId() {
        return cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    public Integer getMcc() {
        return mcc;
    }

    public void setMcc(Integer mcc) {
        this.mcc = mcc;
    }

    public Integer getMnc() {
        return mnc;
    }

    public void setMnc(Integer mnc) {
        this.mnc = mnc;
    }

    public Integer getSignal() {
        return signal;
    }

    public void setSignal(Integer signal) {
        this.signal = signal;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("lac", lac)
                .append("cellId", cellId)
                .append("mcc", mcc)
                .append("mnc", mnc)
                .append("signal", signal).toString();
    }
}
