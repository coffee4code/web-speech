package com.iflytek.speech.webspeech.component;

import com.iflytek.speech.webspeech.util.HttpUtil;
import io.netty.util.internal.StringUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xghuang
 * @date 2018/7/17
 * @time 10:32
 * @desc:语音合成
 */
@Component
public class TTSComponent {
    @Value("${speech.appid}")
    private String APP_ID;

    @Value("${speech.tts.apikey}")
    private String API_KEY;

    @Value("${speech.webtts-url}")
    private String WEBTTS_URL;

    /**
     * 获取语音合成结果
     * @param text
     * @return
     * @throws Exception
     */
    public Map<String,Object> getResultMap(String text) throws Exception {
        String aue = "raw";
        Map<String, String> header = getHeader("audio/L16;rate=16000", aue, "xiaoyan", "50", "50", "", "text", "50");
        Map<String, Object> resultMap = HttpUtil.doMultiPost(WEBTTS_URL, header, "text=" + text);
        return resultMap;
    }

    /**
     * 组装http请求头
     * @param auf
     * @param aue
     * @param voiceName
     * @param speed
     * @param volume
     * @param engineType
     * @param textType
     * @param pitch
     * @return
     * @throws UnsupportedEncodingException
     */
    private Map<String, String> getHeader(String auf, String aue, String voiceName, String speed, String volume, String engineType, String textType, String pitch) throws UnsupportedEncodingException {
        String curTime = System.currentTimeMillis() / 1000L + "";
        String param = "{\"auf\":\"" + auf + "\"";
        if (!StringUtil.isNullOrEmpty(aue)) {
            param += ",\"aue\":\"" + aue + "\"";
        }
        if (!StringUtil.isNullOrEmpty(voiceName)) {
            param += ",\"voice_name\":\"" + voiceName + "\"";
        }
        if (!StringUtil.isNullOrEmpty(speed)) {
            param += ",\"speed\":\"" + speed + "\"";
        }
        if (!StringUtil.isNullOrEmpty(volume)) {
            param += ",\"volume\":\"" + volume + "\"";
        }
        if (!StringUtil.isNullOrEmpty(pitch)) {
            param += ",\"pitch\":\"" + pitch + "\"";
        }
        if (!StringUtil.isNullOrEmpty(engineType)) {
            param += ",\"engine_type\":\"" + engineType + "\"";
        }
        if (!StringUtil.isNullOrEmpty(textType)) {
            param += ",\"text_type\":\"" + textType + "\"";
        }
        param += "}";

        String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
        String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
        Map<String, String> header = new HashMap<>(12);
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        header.put("X-Param", paramBase64);
        header.put("X-CurTime", curTime);
        header.put("X-CheckSum", checkSum);
        header.put("X-Real-Ip", "192.168.1.102");
        header.put("X-Appid", APP_ID);
        System.out.println(header);
        return header;
    }
}
