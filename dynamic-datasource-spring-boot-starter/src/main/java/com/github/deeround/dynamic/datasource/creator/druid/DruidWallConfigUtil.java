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

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.github.deeround.dynamic.datasource.utils.MapUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 防火墙配置工具类
 *
 * @author Taoyu
 * @since 3.5.0
 */
@Slf4j
public final class DruidWallConfigUtil {
    /**
     * 根据当前的配置和全局的配置生成druid防火墙配置
     *
     * @param c 当前配置
     * @param g 全局配置
     * @return 防火墙配置
     */
    public static WallFilter toWallConfig(Map<String, Object> c, Map<String, Object> g) {
        try {

            MapUtil.putAll(c, g);
            boolean enabled = MapUtil.getBool("enabled", c);
            if (enabled) {
                c.remove("enabled");
                WallConfig wallConfig = new WallConfig();
                MapUtil.setTargetFromMap(wallConfig, c);
                Object dir = c.get("dir");
                if (dir != null) {
                    wallConfig.loadConfig(String.valueOf(dir));
                }
                MapUtil.setTargetFromMap(wallConfig, c);
                WallFilter wallFilter = new WallFilter();
                wallFilter.setConfig(wallConfig);
                return wallFilter;
            }
            return null;
        } catch (Exception e) {
            log.error("init wallFilter error", e);
            return null;
        }
    }
}