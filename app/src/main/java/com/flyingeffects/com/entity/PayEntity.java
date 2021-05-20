package com.flyingeffects.com.entity;

public class PayEntity {

    private String pay_url;
    private Pay_data pay_data;
    private String msg;

    public void setPay_url(String pay_url) {
        this.pay_url = pay_url;
    }

    public String getPay_url() {
        return pay_url;
    }

    public void setPay_data(Pay_data pay_data) {
        this.pay_data = pay_data;
    }

    public Pay_data getPay_data() {
        return pay_data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public class Pay_data {

        private String appid;
        private String partnerid;
        private String prepayid;
        private String packagename;
        private String noncestr;
        private String timestamp;
        private String sign;

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getAppid() {
            return appid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPackagename(String packagename) {
            this.packagename = packagename;
        }

        public String getPackagename() {
            return packagename;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSign() {
            return sign;
        }

    }

}
