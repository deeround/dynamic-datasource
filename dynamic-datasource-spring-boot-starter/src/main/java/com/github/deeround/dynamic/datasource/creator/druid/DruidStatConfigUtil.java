/*
 * Copyright © 2018 organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.deeround.dynamic.datasource.creator.druid;

import com.alibaba.druid.filter.stat.StatFilter;
import com.github.deeround.dynamic.datasource.utils.MapUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Druid监控配置工具类
 *
 * @author Taoyu
 * @since 3.5.0
 */
@Slf4j
public final class DruidStatConfigUtil {
    /**
     * 根据当前的配置和全局的配置生成druid防火墙配置
     *
     * @param c 当前配置
     * @param g 全局配置
     * @return StatFilter
     */
    public static StatFilter toStatFilter(Map<String, Object> c, Map<String, Object> g) {
        try {
            StatFilter filter = null;
            MapUtil.putAll(c, g);
            boolean enabled = MapUtil.getBool("enabled", c);
            if (enabled) {
                c.remove("enabled");
                filter = new StatFilter();
                MapUtil.setTargetFromMap(filter, c);
            }
            return filter;
        } catch (Exception e) {
            log.error("init statFilter error", e);
            return null;
        }
    }
}