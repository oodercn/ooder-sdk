package net.ooder.agent.client.iot.ct;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.agent.client.command.CommandFactory;
import net.ooder.agent.client.home.client.CommandClient;
import net.ooder.agent.client.iot.Area;
import net.ooder.agent.client.iot.HomeException;
import net.ooder.agent.client.iot.Place;
import net.ooder.agent.client.iot.ZNode;
import net.ooder.agent.client.iot.enums.DeviceStatus;
import net.ooder.common.JDSException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CtPlace implements Place {

    private String placeid;
    private String parentId;
    private String name;
    private String userid;
    private String orgid;
    private String memo;
    private String start;

    private Set<String> areaIds;

    private Set<String> childIds;

    private Set<String> gatewayIds;

    private Set<String> indexSensorIdList;

    public CtPlace(Place rmPlace) {

        this.placeid = rmPlace.getPlaceid();
        this.name = rmPlace.getName();
        this.userid = rmPlace.getUserid();
        this.orgid = rmPlace.getUserid();
        this.memo = rmPlace.getMemo();
        this.start = rmPlace.getStart();
        this.parentId = rmPlace.getParentId();


        this.gatewayIds=rmPlace.getGatewayIds();
        if (gatewayIds==null){
            gatewayIds=new LinkedHashSet<>();
        }


        this.areaIds = rmPlace.getAreaIds();
        if (areaIds==null){
            areaIds=new LinkedHashSet<>();
        }

        this.childIds = rmPlace.getChildIds();
        if (childIds==null){
            childIds=new LinkedHashSet<>();
        }

        this.indexSensorIdList = rmPlace.getIndexSensorIdList();
        if (indexSensorIdList==null){
            indexSensorIdList=new LinkedHashSet<>();
        }
        CtIotCacheManager.getInstance().placeCache.put(placeid,this);
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Set<String> getAreaIds() {
        return areaIds;
    }

    public void setAreaIds(Set<String> areaIds) {
        this.areaIds = areaIds;
    }


    public void setChildIds(Set<String> childIds) {
        this.childIds = childIds;
    }

    @Override
    public Set<String> getGatewayIds() {
        return gatewayIds;
    }

    public void setGatewayIds(Set<String> gatewayIds) {
        this.gatewayIds = gatewayIds;
    }


    public Set<String> getIndexSensorIdList() {
        return indexSensorIdList;
    }

    public void setIndexSensorIdList(Set<String> indexSensorIdList) {
        this.indexSensorIdList = indexSensorIdList;
    }

    @JSONField(serialize = false)
    
    public List<Area> getAreas() {
        List<Area> areas = new ArrayList<Area>();
        Set<String> areaIds = this.getAreaIds();
        for (String areaId : areaIds) {
            try {
                if (areaId != null) {
                    Area area = CtIotCacheManager.getInstance().getAreaById(areaId);
                    if (area != null) {
                        areas.add(area);
                    }
                }

            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return areas;
    }

    @JSONField(serialize = false)
    
    public List<ZNode> getIndexSensorList() {
        List<ZNode> znodes = new ArrayList<ZNode>();
        Set<String> znodeIds = this.getIndexSensorIdList();
        for (String znodeId : znodeIds) {
            try {
                znodes.add(CtIotCacheManager.getInstance().getZNodeById(znodeId));
            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return znodes;
    }

    @JSONField(serialize = false)
    
    public List<ZNode> getGateways() {
        List<ZNode> znodes = new ArrayList<ZNode>();
        Set<String> gatewayIds= this.getGatewayIds();

        for (String gatewayId : gatewayIds) {


            ZNode    znode= null;
            try {
                znode = CtIotCacheManager.getInstance().getZNodeById(gatewayId);
            } catch (HomeException e) {
                e.printStackTrace();
            }
            if (znode != null) {
                znode.setStatus(DeviceStatus.OFFLINE);
                CommandClient commandClient = null;
                try {
                    commandClient = CommandFactory.getInstance().getCommandClientByieee(znode.getEndPoint().getDevice().getSerialno());
                } catch (JDSException e) {
                    e.printStackTrace();
                }
                if (commandClient == null) {
                    znode.setStatus(DeviceStatus.ONLINE);
                }

                znodes.add(znode);
            }

        }
        return znodes;

    }

    @JSONField(serialize = false)
    public List<ZNode> getSensors() {
        Set<ZNode> sensorList = new LinkedHashSet<>();
        List<Area> areas = this.getAreas();
        for (Area area : areas) {
            sensorList.addAll(area.getSensors());
        }
        return new ArrayList<>(sensorList);

    }

    @Override
    public Set<String> getChildIds() {
        return this.childIds;
    }

    @Override
    
    public List<Place> getChilders() {
        List<Place> places = new ArrayList<Place>();
        Set<String> childIds = this.getChildIds();
        for (String placeId : childIds) {
            try {
                if (placeId != null) {
                    Place place = CtIotCacheManager.getInstance().getPlaceById(placeId);
                    if (place != null) {
                        places.add(place);
                    }

                }

            } catch (JDSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return places;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    @JSONField(serialize = false)
    
    public Place getParent() {
        try {
            return CtIotCacheManager.getInstance().getPlaceById(parentId);
        } catch (JDSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
