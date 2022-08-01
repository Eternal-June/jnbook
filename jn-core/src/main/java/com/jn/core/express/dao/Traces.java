package com.jn.core.express.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 物流轨迹实体
 */
public class Traces {

    //	轨迹描述
    @JsonProperty("AcceptStation")
    private String acceptStation;
    //    到达时间
    @JsonProperty("AcceptTime")
    private String acceptTime;

    public String getAcceptStation() {
        return acceptStation;
    }

    public void setAcceptStation(String acceptStation) {
        this.acceptStation = acceptStation;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

}