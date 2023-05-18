package com.gitegg.service.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gitegg.platform.mybatis.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author gitegg
 * @since 2019-10-24
 */
@Data
@TableName("t_sys_organization_user")
@ApiModel(value = "OrganizationUser对象", description = "OrganizationUser对象")
public class OrganizationUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机构id")
    @TableField("organization_id")
    private Long organizationId;

    @ApiModelProperty(value = "角色id")
    @TableField("role_id")
    private Long roleId;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("是否主岗（1:主岗 0:兼职）")
    @TableField("is_primary")
    private Integer isPrimary;

    @ApiModelProperty("1:自动 0:手动")
    @TableField("is_auto")
    private Integer isAuto;
}
