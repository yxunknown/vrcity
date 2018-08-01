package com.hercat.mevur.vrcity.tools;

import android.support.annotation.Nullable;

import com.hercat.mevur.vrcity.R;
import com.hercat.mevur.vrcity.entity.PointInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PointPool {
    private Map<String, PointInfo> pointInfoMap;
    private static PointPool pointPool;

    private String poiInfo;

    private PointPool() {
        pointInfoMap = new HashMap<>();
        PointInfo pointInfo = new PointInfo();
        pointInfo.setName("重庆路威科技发展有限公司");
        pointInfo.setDistance(10);
        pointInfo.setLogoUrl(R.drawable.logo_leway);
        pointInfo.setDirectionAngel(-1);
        this.pointInfoMap.put("重庆路威科技发展有限公司", pointInfo);

        poiInfo = "[{\n" +
                "    \"lng\": 106.561761,\n" +
                "    \"lat\": 29.527604,\n" +
                "    \"name\": \"重庆市职业病防治院\"\n" +
                "}, {\n" +
                "    \"lng\": 106.559506,\n" +
                "    \"lat\": 29.527521,\n" +
                "    \"name\": \"新世纪百货五小区店\"\n" +
                "}, {\n" +
                "    \"lng\": 106.560359,\n" +
                "    \"lat\": 29.526257,\n" +
                "    \"name\": \"重庆路威土木工程设计有限公司\"\n" +
                "}, {\n" +
                "    \"lng\": 106.561568,\n" +
                "    \"lat\": 29.526677,\n" +
                "    \"name\": \"重庆市第六人民医院 - 综合楼\"\n" +
                "}, {\n" +
                "    \"lng\": 106.559012,\n" +
                "    \"lat\": 29.524870,\n" +
                "    \"name\": \"怡丰实验学校\"\n" +
                "}, {\n" +
                "    \"lng\": 106.560562,\n" +
                "    \"lat\": 29.524689,\n" +
                "    \"name\": \"聚丰花园\"\n" +
                "}, {\n" +
                "    \"lng\": 106.562776,\n" +
                "    \"lat\": 29.529804,\n" +
                "    \"name\": \"重庆爱尔麦格眼科医院\"\n" +
                "}, {\n" +
                "    \"lng\": 106.561770,\n" +
                "    \"lat\": 29.524497,\n" +
                "    \"name\": \"扬子江花园\"\n" +
                "}, {\n" +
                "    \"lng\": 106.562408,\n" +
                "    \"lat\": 29.525494,\n" +
                "    \"name\": \"大石路万州烤鱼\"\n" +
                "}, {\n" +
                "    \"lng\": 106.564873,\n" +
                "    \"lat\": 29.524799,\n" +
                "    \"name\": \"金山花园\"\n" +
                "}]";

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

    public List<PointInfo> getData() {
        try {
            List<PointInfo> res = new ArrayList<>();
            JSONArray array = new JSONArray(poiInfo);
            for (int index = 0; index < array.length(); index++) {
                JSONObject obj = array.getJSONObject(index);
                PointInfo p = new PointInfo();
                p.setLng(obj.getDouble("lng"));
                p.setLat(obj.getDouble("lat"));
                p.setName(obj.getString("name"));
                if (index % 2 == 0) {
                    p.setType(PointInfo.TYPE_TEXT);
                }
                if (index % 2 == 1) {
                    p.setType(PointInfo.TYPE_TXT_IMAGE);
                    if (index % 3 == 1) {
                        p.setLogoUrl(R.drawable.logo_leway);
                    } else {
                        p.setLogoUrl(R.drawable.img_small);
                    }
                }
                res.add(p);
            }
            return res;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
