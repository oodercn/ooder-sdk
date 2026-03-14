package net.ooder.agent.client.iot.json.device;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BindInfo {

    String ieee;

    Integer count;

    List<ClusterInfo> list = new ArrayList<ClusterInfo>();

    public static class ClusterInfo {
        Integer index;

        String clusterid;

        String destieee;

        public String getClusterid() {
            return clusterid;
        }

        public void setClusterid(String clusterid) {
            this.clusterid = clusterid;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public String getDestieee() {
            return destieee;
        }

        public void setDestieee(String destieee) {
            this.destieee = destieee;
        }

    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getIeee() {
        return ieee;
    }

    public void setIeee(String ieee) {
        this.ieee = ieee;
    }

    public List<ClusterInfo> getList() {
        return list;
    }

    public void setList(List<ClusterInfo> list) {
        this.list = list;
    }

    /**
     * @param args
     * @throws IOException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        BindInfo bindInfo=new BindInfo();
        bindInfo.setIeee("000124399780000");
        List <ClusterInfo> clusterInfos=new ArrayList<ClusterInfo>();
        ClusterInfo clusterInfo=new ClusterInfo();
        clusterInfo.setClusterid("0007");
        clusterInfo.setDestieee("00012439978000111");
        clusterInfo.setIndex(0);
        clusterInfos.add(clusterInfo);
        bindInfo.setList(clusterInfos);

       String json=  JSONObject.toJSONString(bindInfo);
        System.out.println(json);

    }
}

