package com.xxx.rpc.registry.zookeeper;

import java.io.Serializable;

public class AddressEntity implements Serializable{

    private String address;
    private Long timeStamp;

    public AddressEntity(String address, Long timeStamp) {
        this.address = address;
        this.timeStamp = timeStamp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AddressEntity that = (AddressEntity) o;

        return getAddress() != null ? getAddress().equals(that.getAddress())
            : that.getAddress() == null;
    }

    @Override
    public int hashCode() {
        return getAddress() != null ? getAddress().hashCode() : 0;
    }
}
