package com.webank.databrain.service;

import cn.hutool.core.codec.Base64;
import com.webank.databrain.config.SysConfig;
import com.webank.databrain.dao.bc.contract.ProductModule;
import com.webank.databrain.dao.db.entity.AccountInfoEntity;
import com.webank.databrain.dao.db.entity.ProductInfoEntity;
import com.webank.databrain.dao.db.mapper.AccountInfoMapper;
import com.webank.databrain.dao.db.mapper.ProductInfoMapper;
import com.webank.databrain.enums.CodeEnum;
import com.webank.databrain.enums.ReviewStatus;
import com.webank.databrain.handler.key.ThreadLocalKeyPairHandler;
import com.webank.databrain.vo.common.PagedResult;
import com.webank.databrain.utils.BlockchainUtils;
import com.webank.databrain.utils.SessionUtils;
import com.webank.databrain.vo.common.CommonResponse;
import com.webank.databrain.vo.common.Paging;
import com.webank.databrain.vo.request.product.ApproveProductRequest;
import com.webank.databrain.vo.request.product.CreateProductRequest;
import com.webank.databrain.vo.request.product.UpdateProductRequest;
import com.webank.databrain.vo.response.product.HotProductResponse;
import com.webank.databrain.vo.response.product.ProductDetailResponse;
import com.webank.databrain.vo.response.product.ProductIdAndNameResponse;
import com.webank.databrain.vo.response.product.ProductInfoResponse;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.CryptoSuite;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.codec.decode.TransactionDecoderInterface;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private Client client;

    @Autowired
    private ThreadLocalKeyPairHandler keyPairHandler;

    @Autowired
    private CryptoKeyPair witnessKeyPair;
    @Autowired
    private SysConfig sysConfig;

    @Autowired
    private TransactionDecoderInterface txDecoder;

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private ProductInfoMapper productInfoMapper;

    public CommonResponse getHotProducts(int topN) {
        List<ProductIdAndNameResponse> idNames = productInfoMapper.getHotProduct(topN);
        return CommonResponse.success(new HotProductResponse(idNames));
    }

    public CommonResponse pageQueryProducts(Paging paging) {
        List<ProductInfoResponse> productInfoPOList = productInfoMapper.pageQueryProduct(paging.getPageNo(),paging.getPageSize());
        List<ProductDetailResponse> productDetails = new ArrayList<>();

        productInfoPOList.forEach(product -> {
            ProductDetailResponse productDetail = new ProductDetailResponse();
            BeanUtils.copyProperties(product, productDetail);
            productDetail.setProductGid(product.getDid());
            productDetail.setProviderName(product.getCompanyName());
            productDetails.add(productDetail);
        });
        return CommonResponse.success(new PagedResult<>(productDetails,
                paging.getPageNo(),
                paging.getPageSize())
        );
    }

    public CommonResponse getProductDetail(String productId) {
        ProductInfoResponse product = productInfoMapper.getProductByGId(productId);
        ProductDetailResponse productDetail = new ProductDetailResponse();
        BeanUtils.copyProperties(product,productDetail);
        productDetail.setProviderGid(product.getDid());
        productDetail.setProviderName(product.getCompanyName());
        return CommonResponse.success(productDetail);
    }

    public CommonResponse createProduct(CreateProductRequest productRequest) throws TransactionException {
//        String did = SessionUtils.currentAccountDid();
        CryptoSuite cryptoSuite = keyPairHandler.getCryptoSuite();
        AccountInfoEntity entity = accountInfoMapper.selectByDid(productRequest.getDid());
        if (entity == null){
            return CommonResponse.error(CodeEnum.USER_NOT_EXISTS);
        }
        String privateKey = entity.getPrivateKey();
        CryptoKeyPair keyPair = cryptoSuite.loadKeyPair(privateKey);
        ProductModule productModule = ProductModule.load(
                sysConfig.getContractConfig().getProductContract(),
                client,
                keyPair);

        TransactionReceipt receipt = productModule.createProduct(cryptoSuite.hash((
                productRequest.getProductName() + productRequest.getProductDesc())
                .getBytes(StandardCharsets.UTF_8)));
        BlockchainUtils.ensureTransactionSuccess(receipt, txDecoder);

        String productId = Base64.encode(productModule.getCreateProductOutput(receipt).getValue1());
        ProductInfoEntity product = new ProductInfoEntity();
        product.setProductGid(productId);
        product.setProviderId(entity.getPkId());
        product.setStatus(ReviewStatus.NotReviewed.ordinal());
        product.setProductName(productRequest.getProductName());
        product.setProductDesc(productRequest.getProductDesc());
        product.setCreateTime(new Date());
        productInfoMapper.insertProductInfoPO(product);
        return CommonResponse.success(productId);
    }

    public CommonResponse updateProduct(UpdateProductRequest productRequest) throws TransactionException {
        String did = SessionUtils.currentAccountDid();
        AccountInfoEntity entity = accountInfoMapper.selectByDid(did);
        if (entity == null){
            return CommonResponse.error(CodeEnum.USER_NOT_EXISTS);
        }
        String privateKey = entity.getPrivateKey();
        CryptoSuite cryptoSuite = keyPairHandler.getCryptoSuite();
        CryptoKeyPair keyPair = cryptoSuite.loadKeyPair(privateKey);
        ProductModule productModule = ProductModule.load(
                sysConfig.getContractConfig().getAccountContract(),
                client,
                keyPair);

        TransactionReceipt receipt = productModule.modifyProduct(
                Base64.decode(productRequest.getProductGId()),
                cryptoSuite.hash((
                productRequest.getProductName() + productRequest.getProductDesc())
                .getBytes(StandardCharsets.UTF_8)));
        BlockchainUtils.ensureTransactionSuccess(receipt, txDecoder);

        ProductInfoEntity product = new ProductInfoEntity();
        product.setPkId(productRequest.getProductId());
        product.setProductName(productRequest.getProductName());
        product.setProductDesc(productRequest.getProductDesc());
        product.setUpdateTime(new Date());
        productInfoMapper.updateProductInfo(product);

        return CommonResponse.success(productRequest.getProductGId());
    }


    @Transactional
    public CommonResponse approveProduct(ApproveProductRequest productRequest) throws TransactionException {
//        String did = SessionUtils.currentAccountDid();
        AccountInfoEntity entity = accountInfoMapper.selectByDid(productRequest.getDid());
        if (entity == null){
            return CommonResponse.error(CodeEnum.USER_NOT_EXISTS);
        }

        CryptoKeyPair witnessKeyPair = this.witnessKeyPair;
        ProductModule productModule = ProductModule.load(
                sysConfig.getContractConfig().getProductContract(),
                client,
                witnessKeyPair);
        TransactionReceipt receipt = productModule.approveProduct(
                Base64.decode(productRequest.getProductGId()), productRequest.isAgree()
        );
        BlockchainUtils.ensureTransactionSuccess(receipt, txDecoder);

        ProductInfoEntity productInfoEntity = new ProductInfoEntity();
        productInfoEntity.setProductGid(productRequest.getProductGId());
        productInfoEntity.setPkId(productRequest.getProductId());
        productInfoEntity.setStatus(productRequest.isAgree() ? ReviewStatus.Approved.ordinal() : ReviewStatus.Denied.ordinal());
        productInfoEntity.setReviewTime(new Date());
        productInfoMapper.updateProductInfoState(productInfoEntity);
        return CommonResponse.success(productRequest.getProductGId());
    }
}
