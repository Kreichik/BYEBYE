package ui.ads;

public class AdConfig {
    private final long thresholdMillis;
    private final long adDurationMillis;
    private final String videoPath;

    public AdConfig(long thresholdMillis, long adDurationMillis, String videoPath) {
        this.thresholdMillis = thresholdMillis;
        this.adDurationMillis = adDurationMillis;
        this.videoPath = videoPath;
    }

    public long getThresholdMillis() { return thresholdMillis; }
    public long getAdDurationMillis() { return adDurationMillis; }
    public String getVideoPath() { return videoPath; }
}

