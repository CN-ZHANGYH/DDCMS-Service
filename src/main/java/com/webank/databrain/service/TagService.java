package com.webank.databrain.service;

//import com.webank.databrain.db.dao.ITagDbService;
import org.springframework.stereotype.Service;

@Service
public class TagService {

//    @Autowired
//    private ITagDbService tagDbService;
//
//    public HotTagsResponse listHotTags(int topN) {
//        List<IdName> items = tagDbService.listHotTags(topN);
//        return new HotTagsResponse(items);
//    }

//    public PagingResult<TagSummary> listAllTags(Paging paging) {
//        return tagDbService.listTagsByPage(paging);
//    }
//
//    public TagDetail getTagByName(String tag){
//        TagDataObject tagDataObject = tagDbService.getOne(Wrappers.<TagDataObject>query().eq("tag",tag));
//        if(tagDataObject == null){
//            return null;
//        }
//        TagDetail tagDetail = new TagDetail();
//        BeanUtils.copyProperties(tagDataObject,tagDetail);
//        tagDetail.setTagId(tagDataObject.getPkId());
//        return tagDetail;
//    }
//
//    public void createTag(CreateTagRequest createTagRequest){
//        TagDataObject tagDataObject = new TagDataObject();
//        tagDataObject.setCreateTime(LocalDateTime.now());
//        tagDataObject.setUpdateTime(LocalDateTime.now());
//        tagDataObject.setTag(createTagRequest.getTag());
//        tagDataObject.setSchemaIdList(createTagRequest.getSchemaId());
//        tagDbService.save(tagDataObject);
//    }
//
//    public void updateTag(TagDetail tagDetail) {
//        TagDataObject tagDataObject = new TagDataObject();
//        BeanUtils.copyProperties(tagDetail,tagDataObject);
//        tagDataObject.setUpdateTime(LocalDateTime.now());
//        tagDbService.saveOrUpdate(tagDataObject);
//    }
}
