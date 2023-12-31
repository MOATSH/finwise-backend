package com.scu.finwise.core.mapper;

import com.scu.finwise.core.pojo.dto.ExcelDictDTO;
import com.scu.finwise.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author MOATSH
 * @since 2023-10-26
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(List<ExcelDictDTO> list);

}
