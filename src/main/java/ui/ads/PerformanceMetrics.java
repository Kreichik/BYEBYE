package ui.ads;

public class PerformanceMetrics {
    private long adStartMillis;
    private Long adEndMillis;
    private int errorCount;

    public void markAdStart() { adStartMillis = System.currentTimeMillis(); adEndMillis = null; }
    public void markAdEnd() { adEndMillis = System.currentTimeMillis(); }
    public void markAdError() { errorCount++; }
    public long getAdStartMillis() { return adStartMillis; }
    public Long getAdEndMillis() { return adEndMillis; }
    public int getErrorCount() { return errorCount; }
}

