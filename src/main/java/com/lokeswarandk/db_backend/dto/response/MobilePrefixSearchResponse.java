package com.lokeswarandk.db_backend.dto.response;

import java.util.List;

public class MobilePrefixSearchResponse {

    private List<String> mobileNos;

    public MobilePrefixSearchResponse() {}

    public MobilePrefixSearchResponse(List<String> mobileNos) {
        this.mobileNos = mobileNos;
    }

    public List<String> getMobileNos() {
        return mobileNos;
    }

    public void setMobileNos(List<String> mobileNos) {
        this.mobileNos = mobileNos;
    }
}
