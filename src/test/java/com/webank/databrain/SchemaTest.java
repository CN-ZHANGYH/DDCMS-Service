package com.webank.databrain;

import cn.hutool.json.JSONUtil;
import com.webank.databrain.enums.AccountType;
import com.webank.databrain.model.account.RegisterRequestVO;
import com.webank.databrain.model.common.Paging;
import com.webank.databrain.model.common.PagingResult;
import com.webank.databrain.model.dataschema.CreateDataSchemaRequest;
import com.webank.databrain.model.dataschema.DataSchemaDetail;
import com.webank.databrain.model.dataschema.DataSchemaDetailWithVisit;
import com.webank.databrain.service.AccountService;
import com.webank.databrain.service.DataSchemaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaTest extends ServerApplicationTests{


    @Autowired
    private AccountService accountService;

    @Autowired
    private DataSchemaService schemaService;


    @Test
    void schemaQueryTest() throws Exception {


        PagingResult<DataSchemaDetail> result =  schemaService.pageQuerySchema(new Paging(1,1), null,null,null,"数据");
        System.out.println(JSONUtil.toJsonStr(result));

        DataSchemaDetailWithVisit visit = schemaService.getDataSchemaById("AAE5B5xx/WADdsUVkcPZlkdSCQJzdLfKf0u1jMMiZxM=");
        System.out.println(JSONUtil.toJsonStr(visit));
    }

    @Test
    void schemaCreateTest() throws Exception {
        RegisterRequestVO request = new RegisterRequestVO();
        request.setAccountType(AccountType.Enterprise);
        request.setPassword("123456123");
        request.setUsername("abcdeed1121");
        request.setDetailJson("{\"name\" : \"aause123123r\"}");
        String userId = accountService.registerAccount(request);

        accountService.auditAccount("abcdeed1121",true);

        CreateDataSchemaRequest schemaRequest  = new CreateDataSchemaRequest();
        schemaRequest.setSchema("{\"test24\":\"String\"....}");
        schemaRequest.setSchemaName("测试目录1245151");
        schemaRequest.setCondition("查询条件");
        schemaRequest.setPrice(123);
        schemaRequest.setEffectTime(LocalDateTime.now());
        schemaRequest.setExpireTime(LocalDateTime.now());
        schemaRequest.setDescription("描述");
        schemaRequest.setTagName("科技");
        schemaRequest.setType(1);
        schemaRequest.setProductId("AAE5B5xx/WADdsUVkcPZlkdSCQJzdLfKf0u1jMMiZGM=");
        schemaRequest.setProviderId(userId);
        schemaRequest.setVisible(1);
        schemaRequest.setDid(userId);
        schemaRequest.setUsage("test");
        schemaRequest.setUri("127.0.0.1");

        String id = schemaService.createDataSchema(schemaRequest);
        assertThat(id).isNotNull();
    }



}
