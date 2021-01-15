package com.example.mail.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;


public class Hlht1Test {


    /**
     * @param url     发送请求的 URL
     * @param jsonStr 需要post的数据
     * @param headers 请求头
     * @return 所代表远程资源的响应结果
     */
    public static String postParam(String url, String jsonStr, Map<String, String> headers) {
        HttpPost post = new HttpPost();
        HttpResponse response = null;
        try {
            post.setURI(new URI(url));
            if (StringUtils.isNotEmpty(jsonStr)) {
                post.setEntity(new StringEntity(jsonStr, "UTF-8"));
            }
            if (MapUtils.isNotEmpty(headers)) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            HttpClient httpClient = HttpClients.createDefault();
            response = httpClient.execute(post);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("got an error from HTTP for url:" + url, e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
            post.releaseConnection();
        }
    }

    public static void main(String[] args) throws Exception {
        //星星
        String tDataSecret = "82c91325e74bef0f";
        String tDataSecretIv = "9af2e7b2d7562ad5";
        String tSigSecret = "a2164ada0026ccf7";
        String operatorId = "313744932";
        //北京互联互通token
		String token="648451345BF130EECBB14B97BF601139";

        Map<String,String> header = new HashMap<String,String>();
        header.put("Content-Type", "application/json;charset=utf-8");

        if (!zcsyurl.endsWith("query_token")) {//获取token
            header.put("Authorization", "Bearer "+token);
        }

        //加密后的data
        String dataStr = EncryptUtil.encryptAES(data, tDataSecret, tDataSecretIv);

        JSONObject param = new JSONObject();
        param.put("OperatorID",operatorId);// 定时任务用的是sdt.getPushOperatorId()因为是向对方推送//SiteDockingThird sdt
        String timestamp = DateUtils.getNowTimestamp();
        param.put("TimeStamp", timestamp);
        String seq = "0001";
        param.put("Seq", seq);
        param.put("Data", dataStr);

        String orgStr = param.get("OperatorID").toString()+param.get("Data")+param.get("TimeStamp")+param.get("Seq");
        String sign = EncryptUtil.encryptHMAC(orgStr, tSigSecret);//ti.getTSigSecret()//ThirdInfo ti = getThirdInfo(operatorId);

        param.put("Sig", sign);

        String rs = HttpClientUtils.getInstance().executePost(zcsyurl, param.toJSONString(), header);
//		if(rs!=null) {
//			System.out.println(JSONObject.parseObject(rs));
//		}
        System.out.println("rs=="+rs);
    }

    //根据城市编号查询桩群信息
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20190705/queryStubGroupWithCityCode";
    //token生成
//    public static String zcsyurl = "http://192.168.6.13:8080/enterprise-service/evcs/v20200403/query_token";
    //查询充电站信息
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20200403/query_stations_info";
    //设备接口状态查询
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20200403/query_station_status";
    //查询业务策略信息结果
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20190705/query_equip_business_policy";
    //查询充电站信息
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20200420/query_stations_info";
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20190705/query_stations_info";
    //开始充电
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20200420/query_start_charge";
//    public static String zcsyurl = "http://192.168.6.13:8080/enterprise-service/evcs/v20200420/query_start_charge";
    //停止充电
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20200420/query_stop_charge";
    //public static String zcsyurl = "http://192.168.6.13:8080/enterprise-service/evcs/v20200420/query_stop_charge";
    public static String zcsyurl = "http://localhost:8080/enterprise-service/evcs/v20190705/query_token";

    //查询充电状态
//    public static String zcsyurl = "http://192.168.1.4:8080/enterprise-service/evcs/v20200420/query_equip_charge_status";

    //星星充电token
    public static String data = "{\"OperatorID\":\"313744932\",\"OperatorSecret\":\"acb93539fc9bg78k\"}";
    //蚂蚁充电token生成
//public static String data = "{\"OperatorID\":\"325616137\",\"OperatorSecret\":\"EvEYrP1Ca8q2WzLd\"}";
    //查询充电站信息
    //public static String data = "{\"PageNo\":1,\"PageSize\":10,\"LastQueryTime\":\"2020-01-01 12:23:21\"}";
//    public static String data = "{\"PageNo\":1,\"PageSize\":10}";
    //设备接口状态查询
//	public static String data = "{\"StationIDs\":[\"588\"]}";
    //查询业务策略信息结果
//	public static String data = "{\"EquipBizSeq\":\"348375727\",\"ConnectorID\":\"1150100066001022\"}";
    //开始充电参数
//    public static String data = "{\"StartChargeSeq\":\"202005282104082811\",\"ConnectorID\":\"11201020030010020\",\"chargingAmt\":1}";
    //停止充电参数
    //public static String data = "{\"StartChargeSeq\":\"202005282104082811\",\"ConnectorID\":\"11201020030010020\"}";
    //查询充电状态
//    public static String data = "{\"StartChargeSeq\":\"202004241334082811\"}";

}
