package com.webank.databrain.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2023-03-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_tag_info")
public class TagInfoDataObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pk_id", type = IdType.AUTO)
    private Long pkId;

    /**
     * 标签名
     */
    @TableField("tag_name")
    private String tagName;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;


}
