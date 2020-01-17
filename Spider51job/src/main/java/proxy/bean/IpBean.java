package proxy.bean;

/**
 * @author: PowerZZJ
 * @date: 2020/1/9
 */
public class IpBean {
    private String ipAddress;
    private String ipPort;
    private String ipType;
    private String ipSpeed;

    public IpBean() {
    }


    @Override
    public String toString() {
        return ipAddress + ":" + ipPort;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpPort() {
        return ipPort;
    }

    public void setIpPort(String ipPort) {
        this.ipPort = ipPort;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    public String getIpSpeed() {
        return ipSpeed;
    }

    public void setIpSpeed(String ipSpeed) {
        this.ipSpeed = ipSpeed;
    }
}
