package com.honghe.livemanager.cloud.srs.model;

/**
 * Description:/api/v1/streams/{id} 接口返回的数据结构
 * Author：hyh
 * Date: 2018/10/17
 */
public class StreamModel {

    /**
     * id : 10
     * name : ffmpeg1
     * vhost : 9
     * app : live
     * live_ms : 1539756783328
     * clients : 2
     * frames : 20852
     * send_bytes : 101898060
     * recv_bytes : 101709689
     * kbps : {"recv_30s":1150,"send_30s":1153}
     * publish : {"active":true,"cid":205}
     * video : {"codec":"H264","profile":"Other","level":"Other"}
     * audio : {"codec":"AAC","sample_rate":44100,"channel":2,"profile":"LC"}
     */

    private int id;
    private String name;
    private int vhost;
    private String app;
    private long live_ms;
    private int clients;
    private int frames;
    private int send_bytes;
    private int recv_bytes;
    private KbpsBean kbps;
    private PublishBean publish;
    private VideoBean video;
    private AudioBean audio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVhost() {
        return vhost;
    }

    public void setVhost(int vhost) {
        this.vhost = vhost;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public long getLive_ms() {
        return live_ms;
    }

    public void setLive_ms(long live_ms) {
        this.live_ms = live_ms;
    }

    public int getClients() {
        return clients;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public int getSend_bytes() {
        return send_bytes;
    }

    public void setSend_bytes(int send_bytes) {
        this.send_bytes = send_bytes;
    }

    public int getRecv_bytes() {
        return recv_bytes;
    }

    public void setRecv_bytes(int recv_bytes) {
        this.recv_bytes = recv_bytes;
    }

    public KbpsBean getKbps() {
        return kbps;
    }

    public void setKbps(KbpsBean kbps) {
        this.kbps = kbps;
    }

    public PublishBean getPublish() {
        return publish;
    }

    public void setPublish(PublishBean publish) {
        this.publish = publish;
    }

    public VideoBean getVideo() {
        return video;
    }

    public void setVideo(VideoBean video) {
        this.video = video;
    }

    public AudioBean getAudio() {
        return audio;
    }

    public void setAudio(AudioBean audio) {
        this.audio = audio;
    }

    public static class KbpsBean {
        /**
         * recv_30s : 1150
         * send_30s : 1153
         */

        private float recv_30s;
        private float send_30s;

        public float getRecv_30s() {
            return recv_30s;
        }

        public void setRecv_30s(float recv_30s) {
            this.recv_30s = recv_30s;
        }

        public float getSend_30s() {
            return send_30s;
        }

        public void setSend_30s(float send_30s) {
            this.send_30s = send_30s;
        }
    }

    public static class PublishBean {
        /**
         * active : true
         * cid : 205
         */

        private boolean active;
        private int cid;

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }
    }

    public static class VideoBean {
        /**
         * codec : H264
         * profile : Other
         * level : Other
         */

        private String codec;
        private String profile;
        private String level;

        public String getCodec() {
            return codec;
        }

        public void setCodec(String codec) {
            this.codec = codec;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }
    }

    public static class AudioBean {
        /**
         * codec : AAC
         * sample_rate : 44100
         * channel : 2
         * profile : LC
         */

        private String codec;
        private int sample_rate;
        private int channel;
        private String profile;

        public String getCodec() {
            return codec;
        }

        public void setCodec(String codec) {
            this.codec = codec;
        }

        public int getSample_rate() {
            return sample_rate;
        }

        public void setSample_rate(int sample_rate) {
            this.sample_rate = sample_rate;
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }
    }
}
