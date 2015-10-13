package com.coltcn.chatserver.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by majf on 2015/10/13.
 */
public class Header {
    private int crcCode = 0xabef0101;
    private int length ;//��Ϣ����
    private long sessionID;//�ỰID
    private byte type;//��Ϣ����
    private byte priority;//��Ϣ���ȼ�
    private Map<String,Object> attachment = new HashMap<String, Object>();//����

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return String.format("Header [crcCode=%s,length=%d,sessionID=%l,type=%b,priority=%b,attachment=%s]",crcCode,length,sessionID,type,priority,attachment);
    }
}
