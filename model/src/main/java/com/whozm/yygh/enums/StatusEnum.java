package com.whozm.yygh.enums;

/**
 * @author HZM
 * @date 2023/1/29
 */
public enum StatusEnum {
    //
    LOCK(0,"锁定"),
    NORMAL(1,"正常");
    private Integer status;
    private String statusString;

    public static String getStatusStringByStatus(Integer status){
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum value : values) {
            if (value.getStatus().intValue() == status.intValue()){
                return value.statusString;
            }
        }
        return "";
    }

    StatusEnum(Integer status, String statusString) {
        this.status = status;
        this.statusString = statusString;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

}
