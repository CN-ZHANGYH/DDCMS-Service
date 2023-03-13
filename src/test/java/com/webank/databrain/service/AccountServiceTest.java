package com.webank.databrain.service;

import com.webank.databrain.ServerApplicationTests;
import com.webank.databrain.enums.AccountStatus;
import com.webank.databrain.enums.AccountType;
import com.webank.databrain.vo.common.CommonResponse;
import com.webank.databrain.vo.request.account.*;
import com.webank.databrain.vo.response.account.RegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.transaction.tools.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AccountServiceTest extends ServerApplicationTests {
    @Autowired
    private AccountService accountService;
    @Test
    public void testPersonalUserRegister() throws Exception{
        String username = "personalUser0001";
        String password = "12345678";
        RegisterRequest request = new RegisterRequest();
        request.setUserName(username);
        request.setPassword(password);
        request.setAccountType(AccountType.Personal);
        PersonalDetailRequest orgUserDetail = new PersonalDetailRequest();
        orgUserDetail.setName("张三");
        request.setDetailJson(JsonUtils.toJson(orgUserDetail));
        Object result = accountService.registerAccount(request);
        System.out.println(JsonUtils.toJson(result));
    }

    @Test
    public void testEnterpriseUserRegister() throws Exception{
        String username = "companyUser00001";
        String password = "12345678";
        RegisterRequest request = new RegisterRequest();
        request.setUserName(username);
        request.setPassword(password);
        request.setAccountType(AccountType.Company);
        CompanyDetailRequest companyDetail = new CompanyDetailRequest();
        companyDetail.setCompanyName("阿里");
        companyDetail.setCompanyDesc("某公司");
        companyDetail.setCertNo("123456");
        request.setDetailJson(JsonUtils.toJson(companyDetail));
        Object result = accountService.registerAccount(request);
        System.out.println(JsonUtils.toJson(result));
    }

    @Test
    public void testRegistration() throws Exception{
        String requestStr = "{\"accountType\":\"0\",\"username\":\"1\",\"password\":\"1\",\"detailJson\":\"{\\\"name\\\":\\\"1\\\",\\\"contact\\\":\\\"1\\\",\\\"email\\\":\\\"a@1.com\\\",\\\"certType\\\":\\\"nationalID\\\",\\\"certNum\\\":\\\"1\\\"}\"}";
        RegisterRequest request = JsonUtils.fromJson(requestStr, RegisterRequest.class);


        Object result = accountService.registerAccount(request);
        System.out.println(JsonUtils.toJson(result));
    }

    @Test
    public void testLogin() throws Exception{
        String username = "companyUser00001";
        String password = "12345678";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName(username);
        loginRequest.setPassword(password);
        Object loginResult = accountService.login(loginRequest);
        System.out.println(loginResult);

        loginResult = accountService.login(loginRequest);
        System.out.println(loginResult);

    }

    @Test
    public void testHotCompanies() throws Exception{
        Object response = accountService.listHotCompanies(5);
        System.out.println(JsonUtils.toJson(response));
    }

    @Test
    public void testListCompanyByPage() throws Exception{
        PageQueryCompanyRequest request = new PageQueryCompanyRequest();
        request.setPageNo(1);
        request.setPageSize(2);
        Object response = accountService.listCompanyByPage(request);
        System.out.println(JsonUtils.toJson(response));

    }

    @Test
    public void testQueryByUsername() throws Exception{
        QueryByUsernameRequest request = new QueryByUsernameRequest();
        request.setUsername("personalUser0002");

        Object personResponse = accountService.getPersonByUsername(request.getUsername());
        System.out.println(JsonUtils.toJson(personResponse));

        request.setUsername("companyUser00001");
        Object companyResponse = accountService.getCompanyByUsername(request.getUsername());
        System.out.println(JsonUtils.toJson(companyResponse));

    }

    @Test
    public void testSearch() throws Exception{
        SearchCompanyRequest companyRequest = new SearchCompanyRequest();
        companyRequest.setPageNo(1);
        companyRequest.setPageSize(5);
        companyRequest.setCondition(new SearchCompanyRequest.SearchCondition(AccountStatus.Registered.name()));
        System.out.println(JsonUtils.toJson(companyRequest));

        SearchCompanyRequest r = JsonUtils.fromJson(JsonUtils.toJson(companyRequest), SearchCompanyRequest.class);
//        SearchCompanyResponse companyResponse = accountService.searchCompanies(companyRequest);
//        System.out.println(JsonUtils.toJson(companyResponse));
//
//        SearchPersonRequest personRequest = new SearchPersonRequest();
//        personRequest.setPageNo(1);
//        personRequest.setPageSize(5);
//        personRequest.setCondition(new SearchPersonRequest.SearchCondition(AccountStatus.Approved.name()));
//
//        SearchPersonResponse personResponse = accountService.searchPersons(personRequest);
//        System.out.println(JsonUtils.toJson(personResponse));

//        request.setUsername("companyUser00001");
//        QueryCompanyByUsernameResponse companyResponse = accountService.getCompanyByUsername(request.getUsername());
//        System.out.println(JsonUtils.toJson(companyResponse));

    }

    @Test
    public void testAudit() throws Exception{
        String username = "personalUser0016";
        String password = "12345678";
        RegisterRequest request = new RegisterRequest();
        request.setUserName(username);
        request.setPassword(password);
        request.setAccountType(AccountType.Personal);
        PersonalDetailRequest orgUserDetail = new PersonalDetailRequest();
        orgUserDetail.setName("李四");
        request.setDetailJson(JsonUtils.toJson(orgUserDetail));
        CommonResponse<RegisterResponse> result = accountService.registerAccount(request);
        String did = result.getData().getDid();

        ApproveAccountRequest approveAccountRequest = new ApproveAccountRequest();
        approveAccountRequest.setApproved(true);
        approveAccountRequest.setDid(did);

        accountService.approveAccount(approveAccountRequest);

    }



}
