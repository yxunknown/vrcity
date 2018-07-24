package com.hercat.mevur.vrcity.tools;

import android.support.annotation.Nullable;

import com.hercat.mevur.vrcity.R;
import com.hercat.mevur.vrcity.entity.PointInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PointPool {
    private Map<String, PointInfo> pointInfoMap;
    private static PointPool pointPool;

    private PointPool() {
        pointInfoMap = new HashMap<>();
        PointInfo pointInfo = new PointInfo();
        pointInfo.setName("重庆路威科技发展有限公司");
        pointInfo.setDistance(10);
        pointInfo.setLogoUrl(R.drawable.logo_leway);
        pointInfo.setDirectionAngel(-1);
        this.pointInfoMap.put("重庆路威科技发展有限公司", pointInfo);

    }

    public synchronized static PointPool instance() {
        if (null == pointPool) {
            pointPool = new PointPool();
        }
        return pointPool;
    }

    public void put(String key, PointInfo value) {
        this.pointInfoMap.put(key,value);
    }

    @Nullable
    public PointInfo get(String key) {
        return this.pointInfoMap.get(key);
    }

    public Set<Map.Entry<String, PointInfo>> getEntrySet() {
        return this.pointInfoMap.entrySet();
    }


}
