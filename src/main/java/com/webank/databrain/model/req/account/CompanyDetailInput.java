package com.webank.databrain.model.req.account;

import lombok.Data;

@Data
public class CompanyDetailInput {

    private String companyName;

    private String companyDesc;

    private String logoUrl;

    private String contact;

    private String certType;

    private String certNo;

}
